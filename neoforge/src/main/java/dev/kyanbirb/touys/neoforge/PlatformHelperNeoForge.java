package dev.kyanbirb.touys.neoforge;

import dev.kyanbirb.touys.PlatformHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;

import java.util.function.BiFunction;

public class PlatformHelperNeoForge implements PlatformHelper {

	@Override
	public CreativeModeTab.Builder creativeTabBuilder() {
		return CreativeModeTab.builder();
	}

	@Override
	public <T extends BlockEntity> BlockEntityType.Builder<T> blockEntityTypeBuilder(BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks) {
		return BlockEntityType.Builder.of(factory::apply, validBlocks);
	}

	@Override
	public ModelResourceLocation modelResourceLocation(ResourceLocation resourceLocation) {
		return ModelResourceLocation.standalone(resourceLocation);
	}

	@Override
	public boolean modLoaded(String modId) {
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}
}
