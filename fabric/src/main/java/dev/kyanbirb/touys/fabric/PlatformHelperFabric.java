package dev.kyanbirb.touys.fabric;

import dev.kyanbirb.touys.PlatformHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public class PlatformHelperFabric implements PlatformHelper {

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType.Builder<T> blockEntityTypeBuilder(BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks) {
        return BlockEntityType.Builder.of(factory::apply, validBlocks);
    }

    @Override
    public ModelResourceLocation modelResourceLocation(ResourceLocation resourceLocation) {
        return new ModelResourceLocation(resourceLocation, "standalone");
    }

    @Override
    public boolean modLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
