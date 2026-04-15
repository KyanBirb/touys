package dev.kyanbirb.touys.items.clone_gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.client.CustomItemRenderer;
import dev.kyanbirb.touys.components.CopiedPlot;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3ic;

public class CloneGunRenderer implements CustomItemRenderer {

	private static final Quaternionf ORIENTATION = new Quaternionf();

	@Override
	public void renderLevel(ClientLevel level, LocalPlayer player, ItemStack stack, PoseStack pose,
								   MultiBufferSource.BufferSource bufferSource, DeltaTracker deltaTracker) {
		CopiedPlot copied = stack.get(TouysComponents.COPIED_PLOT);
		if(copied == null) return;

		// raycast and project end out of plot
		BlockHitResult hitResult = SableTouys.getTargetedBlock(level, player, deltaTracker.getGameTimeDeltaPartialTick(true));
		Vec3 pos = Sable.HELPER.projectOutOfSubLevel(level, hitResult.getLocation());

		// move origin to raycast end
		pose.translate(pos.x, pos.y, pos.z);

		// get sublevel aabb
		Vector3ic size = copied.size();
		AABB aabb = AABB.ofSize(new Vec3(0, size.y() / 2.0f, 0), size.x(), size.y(), size.z());

		// rotate aabb to face player
		ORIENTATION.identity();
		ORIENTATION.rotateY(copied.getRotation(player));
		pose.mulPose(ORIENTATION);

		LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.LINES), aabb, 0.0f, 1.0f, 1.0f, 1.0f);

	}

	@Override
	public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
								  MultiBufferSource bufferSource, DeltaTracker deltaTracker, int packedLight, int packedOverlay) {
		if(!context.firstPerson()) return;

		CopiedPlot copied = stack.get(TouysComponents.COPIED_PLOT);
		if(copied == null) return;

		Font font = Minecraft.getInstance().font;
		String name = copied.name().isEmpty() ? "Sub-Level" : copied.name();
		int width = font.width(name);

		poseStack.pushPose();
		poseStack.mulPose(Axis.XP.rotation((float) Math.toRadians(-22.5f))); // align with screen
		poseStack.scale(1, -1, 1); // flip along y
		float xOff = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ?
				7.0f : 6.3f;
		poseStack.translate(xOff / 16f, -2.5 / 16f, 10.07 / 16f); // position on top left of screen

		float scale = 0.005f;
		poseStack.scale(scale, scale, scale);

		poseStack.scale(33f / width, 1, 1);


		Matrix4f pose = poseStack.last().pose();
		font.drawInBatch(name, 0, 0, 0xFF00FF00, true, pose, bufferSource, Font.DisplayMode.NORMAL, 0x00000000, packedLight);

		poseStack.popPose();
	}

	@Override
	public boolean shouldRenderOriginal(ItemDisplayContext context) {
		return true;
	}

}
