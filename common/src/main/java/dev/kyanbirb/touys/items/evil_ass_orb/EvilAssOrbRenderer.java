package dev.kyanbirb.touys.items.evil_ass_orb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kyanbirb.touys.client.CustomItemRenderer;
import dev.kyanbirb.touys.index.TouysModels;
import dev.kyanbirb.touys.util.RenderHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EvilAssOrbRenderer implements CustomItemRenderer {

	protected static int openTicks;
	protected static int lastOpenTicks;

	private float getOpenTime(float partialTicks) {
		return Mth.lerp(partialTicks, lastOpenTicks, openTicks) / 5.0f;
	}

	@Override
	public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, DeltaTracker deltaTracker, int packedLight, int packedOverlay) {
		if(!context.firstPerson()) return;
		poseStack.pushPose();

		ModelResourceLocation bottom = TouysModels.ORB_BOTTOM;
		ModelResourceLocation top = TouysModels.ORB;
		ModelResourceLocation orb = TouysModels.ORB_TOP;

		poseStack.mulPose(Axis.ZP.rotation((float) (Math.PI * -0.2f)));
		poseStack.translate(-10 / 16f, 5 / 16f, 0); // position to view

		float openTime = getOpenTime(deltaTracker.getGameTimeDeltaPartialTick(false));

		RenderHelper.renderAdditionalModel(bottom, poseStack, bufferSource);

		poseStack.translate(0, (openTime * openTime) / 4.0f, 0);
		RenderHelper.renderAdditionalModel(top, poseStack, bufferSource);

		poseStack.translate(0, (openTime * openTime) / 4.0f, 0);
		RenderHelper.renderAdditionalModel(orb, poseStack, bufferSource);

		poseStack.popPose();

		// laser
		poseStack.pushPose();
		poseStack.mulPose(Axis.ZP.rotation((float) (Math.PI * -0.14f)));
		poseStack.mulPose(Axis.YP.rotation((float) (Math.PI * 0.005f)));
		poseStack.translate(0, 0.65, 0.5);
		poseStack.scale(-50.0f, 0.25f, 1.0f);
		LevelRenderer.renderFace(poseStack, bufferSource.getBuffer(RenderType.debugQuads()), Direction.NORTH, 0, 0, 0, 1, 1, 1, 1.0f, 0.0f, 0.0f, Math.max(0.0f, openTime - 0.3f));

		poseStack.popPose();
	}

	@Override
	public boolean shouldRenderOriginal(ItemDisplayContext context) {
		return !context.firstPerson();
	}

}
