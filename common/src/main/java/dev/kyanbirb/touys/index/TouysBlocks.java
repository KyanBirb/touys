package dev.kyanbirb.touys.index;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.blocks.projector.ProjectorBlock;
import dev.kyanbirb.touys.data.TouysLang;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class TouysBlocks {
    private static final RegistrationProvider<Item> REGISTER_ITEMS = RegistrationProvider.get(BuiltInRegistries.ITEM, SableTouys.MOD_ID);
    private static final RegistrationProvider<Block> REGISTER = RegistrationProvider.get(BuiltInRegistries.BLOCK, SableTouys.MOD_ID);

    public static final RegistryObject<Block> PROJECTOR = register("projector",
            () -> new ProjectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)), true, properties -> properties
                    .component(TouysComponents.ITEM_DESCRIPTION, 2));

    public static RegistryObject<Block> register(String id, Supplier<Block> supplier, boolean blockItem, UnaryOperator<Item.Properties> properties) {
        TouysLang.addBlock(id);
        RegistryObject<Block> object = REGISTER.register(id, supplier);
        if(blockItem) {
            REGISTER_ITEMS.register(id, () -> new BlockItem(object.get(), properties.apply(new Item.Properties())));
        }
        return object;
    }

    public static void init() {

    }
}
