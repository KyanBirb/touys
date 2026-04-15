package dev.kyanbirb.touys.items.camera;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.TouysConfig;
import dev.kyanbirb.touys.blocks.projector.Projectable;
import dev.kyanbirb.touys.compatibility.SubLevelLocker;
import dev.kyanbirb.touys.components.Frame;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.items.camcorder.recording.RecordingManager;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhotoItem extends Item implements Projectable<Map<UUID, Frame>> {
    public PhotoItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide()) {
            ItemStack item = player.getItemInHand(usedHand);

            Map<UUID, Frame> snapshot = item.get(TouysComponents.SNAPSHOT);
            if(snapshot != null) {
                activateSnapshot((ServerLevel) level, snapshot);
                return InteractionResultHolder.success(item);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if(stack.has(TouysComponents.SNAPSHOT)) {
            Map<UUID, Frame> snapshot = stack.get(TouysComponents.SNAPSHOT);
            String s = snapshot.size() == 1 ? "" : "s";
            components.add(SableTouys.translate("tooltip.contains", SableTouys.translate("tooltip.snapshot", snapshot.size(), s).withStyle(ChatFormatting.GREEN)));
        }
        super.appendHoverText(stack, context, components, flag);
    }

    @Override
    public void project(ServerLevel level, Map<UUID, Frame> snapshot, ItemStack item, @Nullable BlockPos hitPos) {
        activateSnapshot(level, snapshot);
    }

    @Override
    public DataComponentType<Map<UUID, Frame>> projectionDataType() {
        return TouysComponents.SNAPSHOT;
    }

    private static void activateSnapshot(ServerLevel level, Map<UUID, Frame> snapshot) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

        for (Map.Entry<UUID, Frame> entry : snapshot.entrySet()) {
            ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(entry.getKey());

            Frame value = entry.getValue();
            if(subLevel != null) {
                boolean wasLocked = SubLevelLocker.get().isSubLevelLocked(subLevel);
                if(TouysConfig.PHOTO_LOCKING_BEHAVIOR.get().canUnlock()) {
                    SubLevelLocker.get().unlockSubLevel(subLevel);
                }

                RecordingManager.stopReplay(subLevel);
                pipeline.resetVelocity(subLevel);
                pipeline.teleport(subLevel, value.position(), value.orientation());

                if(wasLocked && TouysConfig.PHOTO_LOCKING_BEHAVIOR.get().restoresLock()) {
                    SubLevelLocker.get().lockSubLevel(subLevel);
                }
            }
        }
    }

    public static ItemStack create(Map<UUID, Frame> snapshot) {
        ItemStack stack = TouysItems.PHOTO.get().getDefaultInstance();
        stack.applyComponents(DataComponentMap.builder()
                .set(TouysComponents.SNAPSHOT, snapshot)
                .build());
        return stack;
    }
}
