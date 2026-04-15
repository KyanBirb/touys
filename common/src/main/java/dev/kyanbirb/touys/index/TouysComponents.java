package dev.kyanbirb.touys.index;

import com.mojang.serialization.Codec;
import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.components.CopiedPlot;
import dev.kyanbirb.touys.components.Frame;
import foundry.veil.platform.registry.RegistrationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class TouysComponents {
	private static final RegistrationProvider<DataComponentType<?>> REGISTRY = RegistrationProvider.get(BuiltInRegistries.DATA_COMPONENT_TYPE, SableTouys.MOD_ID);

	public static final DataComponentType<CopiedPlot> COPIED_PLOT = create("copied_plot", builder -> builder
			.persistent(CopiedPlot.CODEC)
			.networkSynchronized(CopiedPlot.STREAM_CODEC)
	);

	public static final DataComponentType<List<Frame>> RECORDING = create("recording", builder -> builder
			.persistent(Frame.CODEC.listOf())
			.networkSynchronized(Frame.STREAM_CODEC.apply(ByteBufCodecs.list())));

	public static final DataComponentType<UUID> TRACKED_SUB_LEVEL = create("tracked_sub_level", builder -> builder
			.persistent(UUIDUtil.CODEC)
			.networkSynchronized(UUIDUtil.STREAM_CODEC));

	public static final DataComponentType<Integer> ITEM_DESCRIPTION = create("item_description", builder -> builder
			.persistent(Codec.INT)
			.networkSynchronized(ByteBufCodecs.INT)
	);

	public static final DataComponentType<BlockPos> ASSEMBLY_START = create("assembly_start", builder -> builder
			.networkSynchronized(BlockPos.STREAM_CODEC)
	);

	public static final DataComponentType<Map<UUID, Frame>> SNAPSHOT = create("snapshot", builder -> builder
			.persistent(Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), Frame.CODEC))
	);

	private static <T> DataComponentType<T> create(String id, UnaryOperator<DataComponentType.Builder<T>> operator) {
		DataComponentType<T> t = operator.apply(DataComponentType.builder()).build();
		REGISTRY.register(id, () -> t);
		return t;
	}

	public static void init() {

	}
}
