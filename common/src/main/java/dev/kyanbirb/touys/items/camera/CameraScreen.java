package dev.kyanbirb.touys.items.camera;

import dev.kyanbirb.touys.client.SubLevelHighlighter;
import dev.kyanbirb.touys.events.ClientEvents;
import dev.kyanbirb.touys.index.TouysSounds;
import dev.kyanbirb.touys.network.C2SSubLevelSnapshotPacket;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraScreen extends Screen {
    public CameraScreen() {
        super(Component.literal("camera"));
    }

    private static final FrustumIntersection INTERSECTION = new FrustumIntersection();

    private final Vector2d dragStart = new Vector2d();
    private final Vector2d mousePos = new Vector2d();
    private boolean dragging = false;

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height - this.height / 10;
        int buttonWidth = 100;
        int buttonHeight = 20;
        Button.Builder cancelButton = new Button.Builder(Component.literal("Cancel"), button -> this.onClose())
                .pos(centerX - buttonWidth / 2, centerY - buttonHeight / 2)
                .size(buttonWidth, buttonHeight);

        this.addRenderableWidget(cancelButton.build());
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);

        graphics.drawCenteredString(this.font, "Click and drag over sub-level(s) to capture", this.width / 2, this.height / 20, 0xFFFFFFFF);

        if(this.dragging) {
            drawSelectionBox(graphics);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.dragging) {
            List<UUID> subLevels = selectSubLevels();
            for (UUID subLevel : subLevels) {
                SubLevelHighlighter.highlight(subLevel, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pButton == 0) {
            this.dragging = true;
            this.dragStart.set(
                    this.minecraft.mouseHandler.xpos() / this.minecraft.getWindow().getScreenWidth(),
                    1.0 - (this.minecraft.mouseHandler.ypos() / this.minecraft.getWindow().getScreenHeight())
            );
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if(pButton == 0) {
            this.dragging = false;
            List<UUID> subLevels = selectSubLevels();
            if(!subLevels.isEmpty()) {
                VeilPacketManager.server().sendPacket(new C2SSubLevelSnapshotPacket(subLevels));
                CameraGuiLayer.setFlash(8,
                        (int) (dragStart.x * this.width),
                        (int) (this.height - dragStart.y * this.height),
                        (int) (mousePos.x * this.width),
                        (int) (this.height - mousePos.y * this.height)
                );
                String s = subLevels.size() == 1 ? "" : "s";
                this.minecraft.player.displayClientMessage(Component.literal("Captured %s sub-level%s".formatted(subLevels.size(), s)), true);
                this.minecraft.player.playSound(TouysSounds.CAMERA_CLICK.value(), 1.0f, (float) (1.0f + Math.random() * 0.5f));
                this.onClose();
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private List<UUID> selectSubLevels() {
        Matrix4f projectionView = getProjectionViewMatrix();
        Matrix4f selection = getSelectionMatrix();

        selection.mul(projectionView);
        INTERSECTION.set(selection);

        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.minecraft.level);
        List<UUID> subLevels = new ArrayList<>();
        for (ClientSubLevel subLevel : container.getAllSubLevels()) {
            BoundingBox3dc bb = subLevel.boundingBox();
            if(INTERSECTION.testAab((float) bb.minX(), (float) bb.minY(), (float) bb.minZ(), (float) bb.maxX(), (float) bb.maxY(), (float) bb.maxZ())) {
                subLevels.add(subLevel.getUniqueId());
            }
        }
        return subLevels;
    }

    private Matrix4f getSelectionMatrix() {
        Vector2d end = this.getMousePos();
        Vector2d min = this.dragStart.min(end, new Vector2d());
        Vector2d max = this.dragStart.max(end, new Vector2d());
        min.mul(2.0).sub(1.0, 1.0);
        max.mul(2.0).sub(1.0, 1.0);

        Vector2d size = new Vector2d(
                Math.abs(max.x - min.x),
                Math.abs(max.y - min.y)
        );

        Vector2d center = new Vector2d(
                min.x + size.x / 2.0,
                min.y + size.y / 2.0
        );

        Matrix4f selection = new Matrix4f();
        selection.translate((float) (center.x), (float) (center.y), 0);
        selection.scale((float) size.x / 2, (float) size.y / 2, 1.0f);

        return selection.invert();
    }

    private Matrix4f getProjectionViewMatrix() {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        Matrix4f projectionMatrix = new Matrix4f(ClientEvents.PROJECTION_MATRIX);
        Matrix4f viewMatrix = new Matrix4f(ClientEvents.VIEW_MATRIX)
                .translate(camera.getPosition().toVector3f().negate());
        return projectionMatrix.mul(viewMatrix);
    }

    private void drawSelectionBox(GuiGraphics graphics) {
        Vector2d min = this.dragStart.min(this.getMousePos(), new Vector2d()).mul(this.width, this.height);
        Vector2d max = this.dragStart.max(this.getMousePos(), new Vector2d()).mul(this.width, this.height);
        min.y = this.height - min.y;
        max.y = this.height - max.y;
        int x1 = (int) min.x();
        int y1 = (int) min.y();
        int x2 = (int) max.x();
        int y2 = (int) max.y();
        int width = x2 - x1;
        int height = y2 - y1;
        graphics.fill(x1, y1, x2, y2, 0x11FFFFFF);
        graphics.renderOutline(x1, y1, width, height, 0xFFFFFFFF);
    }

    private Vector2d getMousePos() {
        return this.mousePos.set(
                this.minecraft.mouseHandler.xpos() / this.minecraft.getWindow().getScreenWidth(),
                1.0 - (this.minecraft.mouseHandler.ypos() / this.minecraft.getWindow().getScreenHeight())
        );
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open(Player player) {
        if(player == Minecraft.getInstance().player) {
            Minecraft.getInstance().setScreen(new CameraScreen());
        }
    }
}