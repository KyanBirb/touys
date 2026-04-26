package dev.kyanbirb.touys.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.client.CustomItemRenderer;
import dev.kyanbirb.touys.client.SubLevelHighlighter;
import dev.kyanbirb.touys.index.TouysBlocks;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.index.TouysModels;
import dev.kyanbirb.touys.items.bubble_blower.BubbleBlowerRenderer;
import dev.kyanbirb.touys.items.camera.CameraGuiLayer;
import dev.kyanbirb.touys.items.clone_gun.CloneGunRenderer;
import dev.kyanbirb.touys.items.crowbar.CrowbarItem;
import dev.kyanbirb.touys.items.evil_ass_orb.EvilAssOrbRenderer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientEvents {
	public static final Matrix4f VIEW_MATRIX = new Matrix4f();
	public static final Matrix4f PROJECTION_MATRIX = new Matrix4f();

	public static void renderLevelStage(VeilRenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource,
										MatrixStack matrixStack, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, int renderTick, DeltaTracker deltaTracker, Camera camera, Frustum frustum) {
		if(stage != VeilRenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
		frustumMatrix.get(VIEW_MATRIX);
		projectionMatrix.get(PROJECTION_MATRIX);

		PoseStack poseStack = matrixStack.toPoseStack();

		poseStack.pushPose();
		SubLevelHighlighter.render(poseStack, bufferSource, deltaTracker, camera);
		poseStack.popPose();

		poseStack.pushPose();
		CustomItemRenderer.renderLevel(poseStack, bufferSource, deltaTracker, camera);
		poseStack.popPose();
	}

	public static void clientTick() {
		CameraGuiLayer.tick();
		SubLevelHighlighter.tick();
	}

	public static void initRenderers() {
		CustomItemRenderer.add(TouysItems.CLONE_GUN.get(), new CloneGunRenderer());
		CustomItemRenderer.add(TouysItems.EVIL_ASS_ORB.get(), new EvilAssOrbRenderer());
		CustomItemRenderer.add(TouysItems.BUBBLE_BLOWER.get(), new BubbleBlowerRenderer());
	}


	public static void itemTooltip(ItemStack itemStack, Item.TooltipContext context, TooltipFlag flag, List<Component> tooltipLines) {
		LocalPlayer player = Minecraft.getInstance().player;
		if(player == null) return;

		UUID tracking = itemStack.get(TouysComponents.TRACKED_SUB_LEVEL);
		if(tracking != null) {
			SubLevel subLevel = SubLevelContainer.getContainer(player.level()).getSubLevel(tracking);
			if(subLevel != null) {
				String name = Objects.requireNonNullElse(subLevel.getName(), "Sub-level");
				tooltipLines.add(SableTouys.translate("tooltip.tracking", Component.literal(name).withStyle(ChatFormatting.AQUA)));
			}
		}

		Integer lines = itemStack.get(TouysComponents.ITEM_DESCRIPTION);
		if(lines != null) {
			if(Screen.hasShiftDown()) {
				for (int i = 0; i < lines; i++) {
					String id = itemStack.getDescriptionId();
					tooltipLines.add(Component.translatable(id + ".description_" + i).withStyle(ChatFormatting.GRAY));
				}
			} else {
				MutableComponent component = SableTouys.translate("tooltip.description")
						.withStyle(ChatFormatting.GRAY);
				tooltipLines.add(component);
			}
		}
	}

	public static void buildCreativeTabContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(TouysItems.CLONE_GUN.get());
		output.accept(TouysItems.EVIL_ASS_ORB.get());
		output.accept(TouysItems.BUBBLE_BLOWER.get());
		output.accept(TouysItems.CROWBAR.get());
		output.accept(TouysItems.CAMCORDER.get());
		output.accept(TouysItems.CAMERA.get());
		output.accept(TouysBlocks.PROJECTOR.get());
	}

    public static void registerAdditional(Consumer<ModelResourceLocation> register) {
		for (ModelResourceLocation modelResourceLocation : TouysModels.ALL) {
			register.accept(modelResourceLocation);
		}
		initRenderers();
    }

	public static void registerItemColorHandlers(BiConsumer<ItemColor, ItemLike> registry) {
		registry.accept(CrowbarItem::getColor, TouysItems.CROWBAR.get());
	}
}
