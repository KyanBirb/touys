package dev.kyanbirb.touys.index;

import dev.kyanbirb.touys.PlatformHelper;
import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.blocks.projector.ProjectorBlockEntity;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TouysBlockEntityTypes {
    private static final RegistrationProvider<BlockEntityType<?>> REGISTER = RegistrationProvider.get(BuiltInRegistries.BLOCK_ENTITY_TYPE, SableTouys.MOD_ID);

    public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR = REGISTER.register("projector", () -> PlatformHelper.createBlockEntityTypeBuilder(
            ProjectorBlockEntity::new,
            TouysBlocks.PROJECTOR.get()
    ).build(null));

    public static void init() {

    }
}
