package dev.kyanbirb.touys;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ServiceLoader;
import java.util.function.BiFunction;

public interface PlatformHelper {
	PlatformHelper INSTANCE = ServiceLoader.load(PlatformHelper.class).findFirst().orElseThrow(() -> new RuntimeException("Unable to find %s implementation".formatted(PlatformHelper.class.getName())));

	static CreativeModeTab.Builder getCreativeTabBuilder() {
		return INSTANCE.creativeTabBuilder();
	}

	static ModelResourceLocation createModelResourceLocation(ResourceLocation resourceLocation) {
		return INSTANCE.modelResourceLocation(resourceLocation);
	}

	static <T extends BlockEntity> BlockEntityType.Builder<T> createBlockEntityTypeBuilder(BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks) {
		return INSTANCE.blockEntityTypeBuilder(factory, validBlocks);
	}

	static boolean isModLoaded(String modId) {
		return INSTANCE.modLoaded(modId);
	}

	CreativeModeTab.Builder creativeTabBuilder();

	<T extends BlockEntity> BlockEntityType.Builder<T> blockEntityTypeBuilder(BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks);

	ModelResourceLocation modelResourceLocation(ResourceLocation resourceLocation);

	boolean modLoaded(String modId);
}
