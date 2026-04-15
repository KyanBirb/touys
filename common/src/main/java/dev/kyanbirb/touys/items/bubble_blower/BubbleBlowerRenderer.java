package dev.kyanbirb.touys.items.bubble_blower;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kyanbirb.touys.client.CustomItemRenderer;
import dev.kyanbirb.touys.index.TouysModels;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.util.RenderHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BubbleBlowerRenderer implements CustomItemRenderer {

    private static final Vector3d PLOT_CENTER_OF_MASS = new Vector3d();
    private static final Vector3d PLOT_MIN = new Vector3d();
    private static final Vector3d OFFSET = new Vector3d();
    private static final Quaternionf ORIENTATION = new Quaternionf();

    @Override
    public void renderLevel(ClientLevel level, LocalPlayer player, ItemStack stack, PoseStack pose, MultiBufferSource.BufferSource bufferSource, DeltaTracker deltaTracker) {
        BlockPos startPos = stack.get(TouysComponents.ASSEMBLY_START);
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if(startPos != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos endPos = ((BlockHitResult) hitResult).getBlockPos();
            if(!BubbleBlowerItem.inSameGrid(level, startPos, endPos)) return;
            BlockPos zero = BlockPos.ZERO;

            ClientSubLevel subLevel = (ClientSubLevel) Sable.HELPER.getContainingClient(startPos);
            if(subLevel != null) {
                Pose3dc renderPose = subLevel.renderPose();

                Quaternionf orientation = ORIENTATION.set(renderPose.orientation());

                BoundingBox3ic boundingBox = subLevel.getPlot().getBoundingBox();
                Vector3dc position = renderPose.position();
                Vector3d plotCenter = renderPose.transformPositionInverse(position, PLOT_CENTER_OF_MASS);
                Vector3d plotMin = PLOT_MIN.set(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                Vector3d offset = plotMin.sub(plotCenter, OFFSET);

                pose.translate(position.x(), position.y(), position.z());
                pose.mulPose(orientation);
                pose.translate(offset.x(), offset.y(), offset.z());
                zero = new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
            }

            BlockPos min = BlockPos.min(startPos, endPos);
            BlockPos max = BlockPos.max(startPos, endPos);
            BlockPos pos = min.subtract(zero);
            BlockPos scale = max.subtract(min);

            pose.translate(pos.getX(), pos.getY(), pos.getZ());
            pose.scale(scale.getX() + 1, scale.getY() + 1, scale.getZ() + 1);

            ModelResourceLocation bubble = TouysModels.BUBBLE;
            RenderHelper.renderAdditionalModel(bubble, pose, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }

    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, DeltaTracker deltaTracker, int packedLight, int packedOverlay) {

    }
}