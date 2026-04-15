package dev.kyanbirb.touys.mixin.tab_icon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.SableTouysClient;
import dev.kyanbirb.touys.index.TouysModels;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @WrapOperation(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void touys$renderItem(GuiGraphics instance, ItemStack stack, int x, int y, Operation<Void> original, @Local(argsOnly = true) CreativeModeTab tab) {
        if(tab == SableTouys.CREATIVE_TAB) {
            touys$renderCustomIcon(instance, stack, x, y);
        } else {
            original.call(instance, stack, x, y);
        }
    }

    @Unique
    private static void touys$renderCustomIcon(GuiGraphics graphics, ItemStack stack, int x, int y) {
        int guiOffset = 0;
        PoseStack pose = graphics.pose();
        Minecraft minecraft = Minecraft.getInstance();

        BakedModel model = minecraft.getModelManager().getModel(TouysModels.DOMINO);

        if (!stack.isEmpty()) {
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, null, null, 0);

            pose.pushPose();
            pose.translate((float)(x + 8), (float)(y + 8), (float)(150 + (model.isGui3d() ? guiOffset : 0)));

            try {
                pose.scale(16.0F, -16.0F, 16.0F);
                boolean flag = !bakedmodel.usesBlockLight();
                if (flag) {
                    Lighting.setupForFlatItems();
                }

                minecraft
                        .getItemRenderer()
                        .render(stack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, model);
                graphics.flush();
                if (flag) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                throw new ReportedException(crashreport);
            }

            pose.popPose();
        }
    }

}
