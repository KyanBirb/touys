package dev.kyanbirb.touys.blocks.projector;

import dev.kyanbirb.touys.index.TouysBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem;

public class ProjectorBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {

    private ItemStack item;

    public ProjectorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TouysBlockEntityTypes.PROJECTOR.get(), pPos, pBlockState);
        this.item = ItemStack.EMPTY;
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if(!item.isEmpty()) {
            pTag.put("item", this.item.save(pRegistries));
        }

    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("item", 10)) {
            this.item = ItemStack.parse(pRegistries, pTag.getCompound("item")).orElse(ItemStack.EMPTY);
        } else {
            this.item = ItemStack.EMPTY;
        }
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    public ItemStack getTheItem() {
        return this.item;
    }

    @Override
    public void setTheItem(ItemStack itemStack) {
        level.setBlock(getBlockPos(), getBlockState().setValue(ProjectorBlock.TAPE, !itemStack.isEmpty()), 2);
        this.item = itemStack;
    }
}
