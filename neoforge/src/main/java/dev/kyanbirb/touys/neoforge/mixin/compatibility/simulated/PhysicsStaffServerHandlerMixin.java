package dev.kyanbirb.touys.neoforge.mixin.compatibility.simulated;

import dev.kyanbirb.touys.items.camcorder.recording.RecordingManager;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;
import net.minecraft.server.level.ServerLevel;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PhysicsStaffServerHandler.class)
public abstract class PhysicsStaffServerHandlerMixin {

    @Shadow
    public abstract boolean isLocked(SubLevel subLevel);

    @Shadow
    private ServerLevel level;

    @Inject(method = "toggleLock", at = @At("TAIL"))
    private void touys$toggleLock(UUID uuid, CallbackInfo ci) {
        SubLevel subLevel = SubLevelContainer.getContainer(level).getSubLevel(uuid);
        if(subLevel != null && isLocked(subLevel)) {
            RecordingManager.stopReplay((ServerSubLevel) subLevel);
        }
    }

    @Inject(method = "drag", at = @At("TAIL"))
    private void touys$drag(UUID playerUUID, UUID subLevelUUID, Vector3dc globalAnchor, Vector3dc localAnchor, Quaterniondc orientation, CallbackInfo ci) {
        SubLevel subLevel = SubLevelContainer.getContainer(level).getSubLevel(subLevelUUID);
        if(subLevel != null) {
            RecordingManager.stopReplay((ServerSubLevel) subLevel);
        }
    }

}
