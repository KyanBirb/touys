package dev.kyanbirb.touys.neoforge;


import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.TouysConfig;
import dev.kyanbirb.touys.neoforge.events.NeoForgeCommonEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(SableTouys.MOD_ID)
public class SableTouysNeoForge {

	public SableTouysNeoForge(IEventBus eventBus, ModContainer modContainer) {
		NeoForge.EVENT_BUS.register(NeoForgeCommonEvents.class);
		eventBus.register(NeoForgeCommonEvents.ModEvents.class);
		modContainer.registerConfig(ModConfig.Type.COMMON, TouysConfig.SPEC);
		SableTouys.init();
	}

}
