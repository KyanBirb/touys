package dev.kyanbirb.touys.client;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface CustomItemRenderer {
	Object2ObjectOpenHashMap<Item, CustomItemRenderer> RENDERERS = new Object2ObjectOpenHashMap<>();

	void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
					MultiBufferSource bufferSource, DeltaTracker deltaTracker, int packedLight, int packedOverlay);

	default void renderLevel(ClientLevel level, LocalPlayer player, ItemStack stack, PoseStack pose,
							 MultiBufferSource.BufferSource bufferSource, DeltaTracker deltaTracker) {
	}

	default boolean shouldRenderOriginal(ItemDisplayContext context) {
		return true;
	}

	static CustomItemRenderer of(Item item) {
		return RENDERERS.get(item);
	}

	static void add(Item item, CustomItemRenderer renderer) {
		RENDERERS.put(item, renderer);
	}

	static void renderLevel(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, DeltaTracker deltaTracker, Camera camera) {
		LocalPlayer player = Minecraft.getInstance().player;
		ClientLevel level = Minecraft.getInstance().level;
		ItemStack item = player.getMainHandItem().isEmpty() ? player.getOffhandItem() : player.getMainHandItem();

		CustomItemRenderer renderer = CustomItemRenderer.of(item.getItem());
		if(renderer != null) {
			Vec3 pos = camera.getPosition();

			poseStack.translate(-pos.x, -pos.y, -pos.z);
			renderer.renderLevel(level, player, item, poseStack, bufferSource, deltaTracker);
		}
	}
}
