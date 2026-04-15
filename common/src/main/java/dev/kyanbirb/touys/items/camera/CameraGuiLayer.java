package dev.kyanbirb.touys.items.camera;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class CameraGuiLayer {

    private static int maxTime = 0;
    private static int flashTime = 0;
    private static int lastFlashTime = 0;
    private static int x1, y1, x2, y2 = 0;

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(flashTime > 0) {
            float time = Mth.lerp(deltaTracker.getGameTimeDeltaPartialTick(true), lastFlashTime, CameraGuiLayer.flashTime) / maxTime;
            float flash = time;
            int alpha = (int) (flash * 150);
            int color = (alpha << 24) | 0x00FFFFFF;
            guiGraphics.fill(x1, y1, x2, y2, color);
        }
    }

    public static void tick() {
        lastFlashTime = flashTime;
        flashTime = Math.max(0, flashTime - 1);
    }

    public static void setFlash(int ticks, int x1, int y1, int x2, int y2) {
        lastFlashTime = ticks;
        flashTime = ticks;
        maxTime = ticks;
        CameraGuiLayer.x1 = x1;
        CameraGuiLayer.y1 = y1;
        CameraGuiLayer.x2 = x2;
        CameraGuiLayer.y2 = y2;
    }
}
