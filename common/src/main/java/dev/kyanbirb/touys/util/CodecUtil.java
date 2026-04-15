package dev.kyanbirb.touys.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.List;

public class CodecUtil {

    public static final Codec<Vector3ic> VECTOR_3I = Codec.INT.listOf(3, 3)
            .xmap(CodecUtil::vec3iFromList, CodecUtil::vec3iAsList);

    public static final StreamCodec<ByteBuf, Vector3ic> VECTOR_3I_STREAM = ByteBufCodecs.INT.apply(ByteBufCodecs.list(3))
            .map(CodecUtil::vec3iFromList, CodecUtil::vec3iAsList);

    public static final Codec<Vector3dc> VECTOR_3D = Codec.DOUBLE.listOf(3, 3)
            .xmap(CodecUtil::vec3dFromList, CodecUtil::vec3dAsList);

    public static final StreamCodec<ByteBuf, Vector3dc> VECTOR_3D_STREAM = ByteBufCodecs.DOUBLE.apply(ByteBufCodecs.list(3))
            .map(CodecUtil::vec3dFromList, CodecUtil::vec3dAsList);


    public static List<Integer> vec3iAsList(Vector3ic vector3ic) {
        return List.of(vector3ic.x(), vector3ic.y(), vector3ic.z());
    }

    public static Vector3ic vec3iFromList(List<Integer> list) {
        return new Vector3i(list.get(0), list.get(1), list.get(2));
    }

    public static List<Double> vec3dAsList(Vector3dc vector3dc) {
        return List.of(vector3dc.x(), vector3dc.y(), vector3dc.z());
    }

    public static Vector3dc vec3dFromList(List<Double> list) {
        return new Vector3d(list.get(0), list.get(1), list.get(2));
    }
}
