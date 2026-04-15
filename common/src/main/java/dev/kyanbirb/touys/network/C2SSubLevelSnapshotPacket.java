package dev.kyanbirb.touys.network;

import dev.kyanbirb.touys.components.Frame;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.items.camera.PhotoItem;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.kyanbirb.touys.SableTouys.path;

public record C2SSubLevelSnapshotPacket(List<UUID> subLevels) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, C2SSubLevelSnapshotPacket> STREAM_CODEC = UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
            C2SSubLevelSnapshotPacket::new,
            C2SSubLevelSnapshotPacket::subLevels
    );

    public static final Type<C2SSubLevelSnapshotPacket> TYPE = new Type<>(path("to_server/sub_level_snapshot"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        if(!(mainHandItem.is(TouysItems.CAMERA.get()) || offHandItem.is(TouysItems.CAMERA.get()))) return;

        Level level = context.level();
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        Map<UUID, Frame> snapshot = new HashMap<>();
        for (UUID id : this.subLevels) {
            SubLevel subLevel = container.getSubLevel(id);
            if(subLevel != null) {
                Pose3d pose3d = subLevel.logicalPose();
                snapshot.put(id, new Frame(new Vector3d(pose3d.position()), new Quaterniond(pose3d.orientation())));
            }
        }
        player.addItem(PhotoItem.create(snapshot));
    }
}
