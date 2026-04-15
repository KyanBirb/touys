package dev.kyanbirb.touys.items.camcorder;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.blocks.projector.Projectable;
import dev.kyanbirb.touys.components.Frame;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.items.camcorder.recording.RecordingManager;
import dev.kyanbirb.touys.items.camcorder.recording.ReplaySession;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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
import java.util.UUID;

public class TapeItem extends Item implements Projectable<List<Frame>> {
    public TapeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide()) {
            ItemStack item = player.getItemInHand(usedHand);
            List<Frame> recording = item.get(TouysComponents.RECORDING);
            if(player.isShiftKeyDown()) {
                SubLevel subLevel = SableTouys.getTargetedSubLevel(level, player);
                if(subLevel == null) {
                    item.remove(TouysComponents.TRACKED_SUB_LEVEL);
                } else {
                    item.set(TouysComponents.TRACKED_SUB_LEVEL, subLevel.getUniqueId());
                }
                return InteractionResultHolder.success(item);
            } else {
                SubLevel subLevel;
                UUID tracked = item.get(TouysComponents.TRACKED_SUB_LEVEL);
                if(tracked != null) {
                    subLevel = SubLevelContainer.getContainer(level).getSubLevel(tracked);
                } else {
                    subLevel = SableTouys.getTargetedSubLevel(level, player);
                }

                if(subLevel != null && item.has(TouysComponents.RECORDING)) {
                    ReplaySession replay = RecordingManager.startReplay((ServerSubLevel) subLevel, recording);

                    if(item.has(DataComponents.CUSTOM_NAME)) {
                        replay.name = item.get(DataComponents.CUSTOM_NAME).getString();
                    }

                    player.displayClientMessage(Component.literal("Replay started"), true);
                    return InteractionResultHolder.success(item);
                }
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.get(TouysComponents.TRACKED_SUB_LEVEL) != null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if(stack.has(TouysComponents.RECORDING)) {
            List<Frame> frames = stack.get(TouysComponents.RECORDING);
            float time = frames.size() / 20.0f;
            components.add(SableTouys.translate("tooltip.contains", SableTouys.translate("tooltip.recording", time).withStyle(ChatFormatting.GREEN)));
        }
        super.appendHoverText(stack, context, components, flag);
    }

    public static ItemStack create(List<Frame> recording) {
        ItemStack stack = TouysItems.TAPE.get().getDefaultInstance();
        stack.applyComponents(DataComponentMap.builder()
                .set(TouysComponents.RECORDING, recording)
                .build());
        return stack;
    }

    @Override
    public void project(ServerLevel level, List<Frame> frames, ItemStack item, @Nullable BlockPos hitPos) {
        SubLevel subLevel;
        if(hitPos != null) {
            subLevel = Sable.HELPER.getContaining(level, hitPos);
        } else {
            UUID uuid = item.get(TouysComponents.TRACKED_SUB_LEVEL);
            subLevel = SubLevelContainer.getContainer(level).getSubLevel(uuid);
        }

        if(subLevel != null) {
            RecordingManager.startReplay((ServerSubLevel) subLevel, frames);
        }
    }

    @Override
    public boolean requiresRayCast(ItemStack stack) {
        return !stack.has(TouysComponents.TRACKED_SUB_LEVEL);
    }

    @Override
    public DataComponentType<List<Frame>> projectionDataType() {
        return TouysComponents.RECORDING;
    }
}
