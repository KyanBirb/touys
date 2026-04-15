package dev.kyanbirb.touys.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanbirb.touys.util.CodecUtil;
import dev.kyanbirb.touys.util.SubLevelTemplate;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3dc;
import org.joml.Vector3ic;

public record CopiedPlot(String name, Direction pasteDirection, Vector3ic size, Vector3dc offset, SubLevelTemplate template) {
	public static Codec<CopiedPlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").orElse("").forGetter(CopiedPlot::name),
			Direction.CODEC.fieldOf("direction").orElse(Direction.NORTH).forGetter(CopiedPlot::pasteDirection),
			CodecUtil.VECTOR_3I.fieldOf("size").forGetter(CopiedPlot::size),
			CodecUtil.VECTOR_3D.fieldOf("offset").forGetter(CopiedPlot::offset),
			SubLevelTemplate.CODEC.fieldOf("template").forGetter(CopiedPlot::template)
	).apply(instance, CopiedPlot::new));

	public static StreamCodec<FriendlyByteBuf, CopiedPlot> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, CopiedPlot::name,
			Direction.STREAM_CODEC, CopiedPlot::pasteDirection,
			CodecUtil.VECTOR_3I_STREAM, CopiedPlot::size,
			CodecUtil.VECTOR_3D_STREAM, CopiedPlot::offset,
			SubLevelTemplate.STREAM_CODEC, CopiedPlot::template,
			CopiedPlot::new
	);

	public float getRotation(Player player) {
		return (float) -Math.toRadians(-this.pasteDirection().toYRot() + player.getYRot());
	}
}
