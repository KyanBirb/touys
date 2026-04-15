package dev.kyanbirb.touys.fabric;

import dev.kyanbirb.touys.SableTouysClient;
import dev.kyanbirb.touys.fabric.event.FabricClientEvents;

public class SableTouysClientFabric {
	public static void init() {
		FabricClientEvents.init();
		SableTouysClient.init();
	}
}
