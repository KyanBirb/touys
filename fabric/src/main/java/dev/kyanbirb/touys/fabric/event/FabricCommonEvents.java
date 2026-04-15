package dev.kyanbirb.touys.fabric.event;

import dev.kyanbirb.touys.events.CommonEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricCommonEvents {
    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(CommonEvents::tick);
        CommandRegistrationCallback.EVENT.register(CommonEvents::registerCommands);
    }

}
