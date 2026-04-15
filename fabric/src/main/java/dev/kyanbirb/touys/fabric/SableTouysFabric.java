package dev.kyanbirb.touys.fabric;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.TouysConfig;
import dev.kyanbirb.touys.fabric.event.FabricCommonEvents;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;

public class SableTouysFabric {
	public void init() {
		FabricCommonEvents.init();
		NeoForgeConfigRegistry.INSTANCE.register(SableTouys.MOD_ID, ModConfig.Type.COMMON, TouysConfig.SPEC);
		SableTouys.init();
	}
}
