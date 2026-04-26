package dev.kyanbirb.touys.items.crowbar;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.index.TouysItems;
import dev.ryanhcode.sable.index.SableAttributes;
import foundry.veil.api.client.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrowbarItem extends Item {
    private static final ResourceLocation ATTRIBUTE_ID = SableTouys.path("crowbar");

    public CrowbarItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(!(stack.is(TouysItems.CROWBAR.get()) && stack.has(DataComponents.ATTRIBUTE_MODIFIERS) &&
                other.is(TouysItems.CROWBAR.get()) && other.has(DataComponents.ATTRIBUTE_MODIFIERS))) {
            return false;
        }

        AttributeModifier stackModifier = getPunchAttribute(stack);
        AttributeModifier otherModifier = getPunchAttribute(other);

        if(stackModifier == null || otherModifier == null) {
            return false;
        }

        stack.applyComponents(
                DataComponentPatch.builder()
                        .set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(stackModifier.amount() + otherModifier.amount()))
                        .build()
        );
        other.shrink(1);
        return true;
    }

    public static @Nullable AttributeModifier getPunchAttribute(ItemStack stack) {
        AttributeModifier stackModifier = null;
        ItemAttributeModifiers stackModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        for (ItemAttributeModifiers.Entry entry : stackModifiers.modifiers()) {
            if(entry.matches(SableAttributes.PUNCH_STRENGTH, ATTRIBUTE_ID)) {
                stackModifier = entry.modifier();
                break;
            }
        }
        return stackModifier;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    public static ItemAttributeModifiers createAttributes(double strength) {
        return ItemAttributeModifiers.builder()
                .add(SableAttributes.PUNCH_STRENGTH, createModifier(strength), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static @NotNull AttributeModifier createModifier(double strength) {
        return new AttributeModifier(ATTRIBUTE_ID, strength, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    public static int getColor(ItemStack stack, int index) {
        if(index == 1) {
            AttributeModifier punchAttribute = CrowbarItem.getPunchAttribute(stack);
            if(punchAttribute != null) {
                float amount = (float) punchAttribute.amount() / 4096;
                float value = (amount / (amount + 0.005f));

                Color green = new Color(0xFF00FF00);
                Color yellow = new Color(0xFFFFFF00);
                Color red = new Color(0xFFFF0000);
                Color color = new Color();

                value = Mth.clamp(value, 0, 1);
                if(value <= 0.5) {
                    green.lerp(yellow, value * 2, color);
                } else {
                    yellow.lerp(red, (value - 0.5f) * 2, color);
                }

                return color.argb();
            }
        }
        return -1;
    }

}
