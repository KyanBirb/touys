package dev.kyanbirb.touys.mixin.crowbar_punch_effect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanbirb.touys.index.TouysItems;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.network.packets.tcp.ServerboundPunchSubLevelPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerboundPunchSubLevelPacket.class)
public class ServerboundPunchSubLevelPacketMixin {

    @WrapOperation(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private<T extends ParticleOptions> int touys$sendParticles(ServerLevel instance, T options, double posX, double posY, double posZ, int count, double xOffset, double yOffset, double zOffset, double speed, Operation<Integer> original, @Local(name = "player") Player player, @Local(name = "globalDirection") Vector3d globalDirection) {
        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(item.is(TouysItems.CROWBAR.get())) {
            float pitch = (float) (0.4f + Math.random() * 0.9f);
            Vec3 vec3 = Sable.HELPER.projectOutOfSubLevel(instance, new Vec3(posX, posY, posZ));
            posX = vec3.x();
            posY = vec3.y();
            posZ = vec3.z();
            xOffset = 0.1;
            yOffset = 0.1;
            zOffset = 0.1;
            instance.playSound(null, posX, posY, posZ, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.PLAYERS, 1.0f, pitch + 1.0f);
            instance.playSound(null, posX, posY, posZ, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.PLAYERS, 1.0f, pitch);

            options = (T) ParticleTypes.CRIT;
            count = 8;
            speed = 0.5;
        }

        return original.call(instance, options, posX, posY, posZ, count, xOffset, yOffset, zOffset, speed);
    }

}
