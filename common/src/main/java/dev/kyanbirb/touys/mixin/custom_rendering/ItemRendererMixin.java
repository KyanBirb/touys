package dev.kyanbirb.touys.mixin.custom_rendering;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kyanbirb.touys.client.CustomItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
	private void touys$render(ItemRenderer instance, BakedModel bakedModel, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer vertexConsumer, Operation<Void> original, @Local(argsOnly = true) ItemDisplayContext context, @Local(argsOnly = true) MultiBufferSource bufferSource) {
		CustomItemRenderer renderer = CustomItemRenderer.of(stack.getItem());
		if(renderer != null) {
			if(renderer.shouldRenderOriginal(context)) {
				original.call(instance, bakedModel, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
			}
			renderer.renderItem(stack, context, poseStack, bufferSource, Minecraft.getInstance().getTimer(), combinedLight, combinedOverlay);
		} else {
			original.call(instance, bakedModel, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
		}
	}

}
