package dev.kyanbirb.touys.mixin.sub_level_template;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelPlot.class)
public interface LevelPlotAccessor {

    @Accessor(value = "container", remap = false)
    SubLevelContainer touys$getContainer();

    @Accessor(value = "logSize", remap = false)
    int touys$getLogSize();

    @Accessor(value = "biome", remap = false)
    ResourceKey<Biome> touys$getBiome();

    @Accessor(value = "expandPlotIfNecessary", remap = false)
    boolean touys$getExpandPlotIfNecessary();

    @Accessor(value = "expandPlotIfNecessary", remap = false)
    void touys$setExpandPlotIfNecessary(boolean expandPlotIfNecessary);

}
