package dev.kyanbirb.touys.neoforge.data;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.blocks.projector.ProjectorBlock;
import dev.kyanbirb.touys.index.TouysBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TouysBlockStates extends BlockStateProvider {
    public TouysBlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SableTouys.MOD_ID, existingFileHelper);
    }



    @Override
    protected void registerStatesAndModels() {

        directionalBlock(TouysBlocks.PROJECTOR.get(), state -> {
            boolean powered = state.getValue(ProjectorBlock.POWERED);
            boolean tape = state.getValue(ProjectorBlock.TAPE);

            String poweredString = powered ? "_powered" : "";
            String tapeString = tape ? "_tape" : "";

            return models().cubeBottomTop(
                    "block/projector" + tapeString + poweredString,
                    modLoc("block/projector_side" + poweredString),
                    modLoc("block/projector_bottom" + tapeString),
                    modLoc("block/projector_top")
            );
        });

        itemModels().simpleBlockItem(TouysBlocks.PROJECTOR.get());


        /*directionalBlock(TouysBlocks.PROJECTOR.get(), state -> {
            boolean powered = state.getValue(ProjectorBlock.POWERED);
            boolean tape = state.getValue(ProjectorBlock.TAPE);
            return models()
                    .getBuilder("block/projector")
                    ;
        });*/

    }
}
