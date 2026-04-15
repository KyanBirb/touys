package dev.kyanbirb.touys.neoforge;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.SableTouysClient;
import dev.kyanbirb.touys.neoforge.events.NeoForgeClientEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SableTouys.MOD_ID, dist = Dist.CLIENT)
public class SableTouysClientNeoForge {
	public SableTouysClientNeoForge(IEventBus eventBus) {
		NeoForge.EVENT_BUS.register(NeoForgeClientEvents.class);
		eventBus.register(NeoForgeClientEvents.ModEvents.class);
		SableTouysClient.init();
	}
}
