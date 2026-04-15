package dev.kyanbirb.touys.items.camcorder;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.client.SubLevelHighlighter;
import dev.kyanbirb.touys.components.Frame;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.items.camcorder.recording.RecordingManager;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class CamcorderItem extends Item {
    public CamcorderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide()) {
            ItemStack item = player.getItemInHand(usedHand);
            SubLevel subLevel = SableTouys.getTargetedSubLevel(level, player);

            if(subLevel != null && !item.has(TouysComponents.TRACKED_SUB_LEVEL)) {
                RecordingManager.startRecording(subLevel);
                item.applyComponents(DataComponentMap.builder()
                        .set(TouysComponents.TRACKED_SUB_LEVEL, subLevel.getUniqueId())
                        .build());
                player.displayClientMessage(Component.literal("Recording started"), true);
                return InteractionResultHolder.success(item);
            } else if(item.has(TouysComponents.TRACKED_SUB_LEVEL)) {
                SubLevel recorded = SubLevelContainer.getContainer(level).getSubLevel(item.get(TouysComponents.TRACKED_SUB_LEVEL));
                Component message = Component.literal("Recording saved");
                if(player.isShiftKeyDown() || recorded == null) {
                    message = Component.literal("Recording discarded");
                } else {
                    List<Frame> recording = RecordingManager.getRecording(recorded);
                    player.addItem(TapeItem.create(recording));
                }

                player.displayClientMessage(message, true);
                RecordingManager.stopRecording(recorded);
                item.remove(TouysComponents.TRACKED_SUB_LEVEL);
                return InteractionResultHolder.success(item);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemStack item = itemEntity.getItem();
        if(item.has(TouysComponents.TRACKED_SUB_LEVEL)) {
            SubLevel recorded = SubLevelContainer.getContainer(itemEntity.level()).getSubLevel(item.get(TouysComponents.TRACKED_SUB_LEVEL));
            if(recorded != null) RecordingManager.stopRecording(recorded);
        }
        super.onDestroyed(itemEntity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(isSelected && level.isClientSide() && stack.has(TouysComponents.TRACKED_SUB_LEVEL)) {
            UUID subLevel = stack.get(TouysComponents.TRACKED_SUB_LEVEL);
            SubLevelHighlighter.highlight(subLevel, 0xFFFF0000);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
