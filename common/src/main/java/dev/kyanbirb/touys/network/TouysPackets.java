package dev.kyanbirb.touys.network;

import dev.kyanbirb.touys.SableTouys;
import foundry.veil.api.network.VeilPacketManager;

public class TouysPackets {
	private static final VeilPacketManager PACKETS = VeilPacketManager.create(SableTouys.MOD_ID, "1");

	public static void init() {
		PACKETS.registerServerbound(C2SSubLevelSnapshotPacket.TYPE, C2SSubLevelSnapshotPacket.STREAM_CODEC, C2SSubLevelSnapshotPacket::handle);
	}
}
