package dev.kyanbirb.touys.events;

import com.mojang.brigadier.CommandDispatcher;
import dev.kyanbirb.touys.index.TouysCommands;
import dev.kyanbirb.touys.items.camcorder.recording.RecordingManager;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CommonEvents {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        TouysCommands.register(dispatcher, buildContext);
    }

    public static void physicsTick(SubLevelPhysicsSystem system, double timeStep) {
        RecordingManager.physicsTick(system);
    }

    public static void tick(Level level) {
        if(!level.isClientSide()) {
            RecordingManager.tick((ServerLevel) level);
        }
    }

}
