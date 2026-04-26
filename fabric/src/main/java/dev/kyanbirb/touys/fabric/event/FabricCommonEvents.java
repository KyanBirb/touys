package dev.kyanbirb.touys.fabric.event;

import dev.kyanbirb.touys.events.CommonEvents;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.items.crowbar.CrowbarItem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;

public class FabricCommonEvents {
    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(CommonEvents::tick);
        CommandRegistrationCallback.EVENT.register(CommonEvents::registerCommands);
        DefaultItemComponentEvents.MODIFY.register(FabricCommonEvents::modifyDefaultComponents);
    }

    private static void modifyDefaultComponents(DefaultItemComponentEvents.ModifyContext context) {
        context.modify(TouysItems.CROWBAR.get(), builder -> builder
                .set(DataComponents.ATTRIBUTE_MODIFIERS, CrowbarItem.createAttributes(1.0f))
        );
    }

}
