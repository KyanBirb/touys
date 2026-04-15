package dev.kyanbirb.touys;

import dev.kyanbirb.touys.events.ClientEvents;
import dev.kyanbirb.touys.index.TouysModels;
import foundry.veil.platform.VeilEventPlatform;

public class SableTouysClient {
	public static void init() {
		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(ClientEvents::renderLevelStage);
		TouysModels.init();
	}
}
