package dev.kyanbirb.touys.blocks.projector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Projectable<Data> {
    void project(ServerLevel level, Data data, ItemStack item, @Nullable BlockPos hitPos);

    default boolean requiresRayCast(ItemStack stack) {
        return false;
    }

    DataComponentType<Data> projectionDataType();
}
