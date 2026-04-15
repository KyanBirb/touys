package dev.kyanbirb.touys.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class RenderHelper {

    public static void renderAdditionalModel(ModelResourceLocation modelResourceLocation, PoseStack poseStack, MultiBufferSource bufferSource) {
        renderAdditionalModel(modelResourceLocation, poseStack, bufferSource, 1.0f, 1.0f, 1.0f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
    }

    public static void renderAdditionalModel(ModelResourceLocation modelResourceLocation, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        renderAdditionalModel(modelResourceLocation, poseStack, bufferSource, 1.0f, 1.0f, 1.0f, light, overlay);
    }

    public static void renderAdditionalModel(ModelResourceLocation modelResourceLocation, PoseStack poseStack, MultiBufferSource bufferSource, float r, float g, float b, int light, int overlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        PoseStack.Pose pose = poseStack.last();
        blockRenderer.getModelRenderer()
                .renderModel(pose, buffer, null, model, 1.0f, 1.0f, 1.0f, light, overlay);
    }

}
