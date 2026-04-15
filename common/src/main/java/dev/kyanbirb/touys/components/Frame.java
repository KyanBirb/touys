package dev.kyanbirb.touys.components;

import com.mojang.serialization.Codec;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.util.SableBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;

public record Frame(Vector3d position, Quaterniond orientation) {
    public Frame() {
        this(new Vector3d(), new Quaterniond());
    }

    public Frame(Pose3d pose3d) {
        this(pose3d.position(), pose3d.orientation());
    }

    public static final Codec<Frame> CODEC = Codec.DOUBLE.listOf(7, 7).xmap(
            list -> new Frame(
                    new Vector3d(list.get(0), list.get(1), list.get(2)),
                    new Quaterniond(list.get(3), list.get(4), list.get(5), list.get(6))),
            frame -> List.of(
                    frame.position().x, frame.position().y, frame.position().z,
                    frame.orientation().x, frame.orientation().y, frame.orientation().z, frame.orientation().w
            )
    );

    public static final StreamCodec<ByteBuf, Frame> STREAM_CODEC = StreamCodec.of(
            (buf, frame) -> {
                SableBufferUtils.write(buf, frame.position());
                SableBufferUtils.write(buf, frame.orientation());
            },
            (buf) -> {
                Frame frame = new Frame();
                SableBufferUtils.read(buf, frame.position());
                SableBufferUtils.read(buf, frame.orientation());
                return frame;
            }
    );

}
