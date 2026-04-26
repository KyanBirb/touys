package dev.kyanbirb.touys.fabric.event;

import dev.kyanbirb.touys.events.ClientEvents;
import dev.kyanbirb.touys.items.camera.CameraGuiLayer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class FabricClientEvents {
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientEvents.clientTick());
        ItemTooltipCallback.EVENT.register(ClientEvents::itemTooltip);
        ClientEvents.initRenderers();
        HudRenderCallback.EVENT.register(CameraGuiLayer::render);
        ClientEvents.registerItemColorHandlers(ColorProviderRegistry.ITEM::register);
    }
}
