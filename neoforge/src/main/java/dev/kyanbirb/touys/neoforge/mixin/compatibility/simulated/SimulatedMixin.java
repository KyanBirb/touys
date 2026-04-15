package dev.kyanbirb.touys.neoforge.mixin.compatibility.simulated;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.compatibility.SubLevelLocker;
import dev.kyanbirb.touys.neoforge.compatibility.simulated.SimulatedSubLevelLockerImpl;
import dev.simulated_team.simulated.Simulated;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Simulated.class)
public class SimulatedMixin {

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private static void touys$init(CallbackInfo ci) {
        SubLevelLocker.set(new SimulatedSubLevelLockerImpl());
    }

}
