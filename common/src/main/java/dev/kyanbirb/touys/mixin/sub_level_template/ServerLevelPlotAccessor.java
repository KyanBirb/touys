package dev.kyanbirb.touys.mixin.sub_level_template;

import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLevelPlot.class)
public interface ServerLevelPlotAccessor {

    @Accessor(value = "lightEngine", remap = false)
    LevelLightEngine touys$getLightEngine();

    @Invoker(value = "newNonLitChunk", remap = false)
    void touys$invokeNewNonLitChunk(final ChunkPos pos);

    @Invoker(value = "logLoadingErrors", remap = false)
    static void touys$invokeLogLoadingErrors(final ChunkPos chunkPos, final int y, final String errorText) {

    }

    @Invoker(value = "lightChunk", remap = false)
    void touys$invokeLightChunk(final LevelChunk chunk);
}
