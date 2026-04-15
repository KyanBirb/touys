package dev.kyanbirb.touys.neoforge.events;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.events.ClientEvents;
import dev.kyanbirb.touys.items.camera.CameraGuiLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static dev.kyanbirb.touys.SableTouys.path;

@EventBusSubscriber(modid = SableTouys.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {

	@SubscribeEvent
	public static void postClientTick(ClientTickEvent.Post event) {
		ClientEvents.clientTick();
	}

	@SubscribeEvent
	public static void itemTooltip(ItemTooltipEvent event) {
		ClientEvents.itemTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
	}

	@EventBusSubscriber(modid = SableTouys.MOD_ID, value = Dist.CLIENT)
	public static class ModEvents {

		@SubscribeEvent
		public static void renderGui(RegisterGuiLayersEvent event) {
			event.registerAboveAll(path("camera"), CameraGuiLayer::render);
		}

	}

}
