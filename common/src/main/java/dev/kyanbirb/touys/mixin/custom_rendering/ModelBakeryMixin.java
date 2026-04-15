package dev.kyanbirb.touys.mixin.custom_rendering;

import dev.kyanbirb.touys.events.ClientEvents;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);

    @Shadow
    abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Inject(method = "<init>", at = @At(value = "RETURN", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void touys$init(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
        ClientEvents.registerAdditional(mrl -> {
            UnbakedModel model = this.getModel(mrl.id());
            this.registerModelAndLoadDependencies(mrl, model);
        });
    }

}
