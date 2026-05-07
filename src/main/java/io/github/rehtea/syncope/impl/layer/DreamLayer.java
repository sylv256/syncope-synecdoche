package io.github.rehtea.syncope.impl.layer;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.function.BiFunction;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum DreamLayer implements StringRepresentable {
	SYNECDOCHE("synecdoche", 0),
	SYNCOPE("syncope", 2),
	SIMILITUDE("similitude", 4),
	LIMBO("limbo", 7),
	LUCIDITY("lucidity", 1);

	public static final Codec<DreamLayer> CODEC = StringRepresentable.fromEnum(DreamLayer::values);
	public static final IntFunction<DreamLayer> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, DreamLayer> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
	public static final DataComponentType<DreamLayer> DATA_COMPONENT_TYPE = DataComponentType.<DreamLayer>builder()
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.build();
	private final String name;
	private final Identifier identifier;
	private final int distance;

	DreamLayer(String name, int distance) {
		this(name, distance, (_, config) -> config);
	}

	DreamLayer(
			String name,
			int distance,
			BiFunction<MinecraftServer, RuntimeLevelConfig, RuntimeLevelConfig> biConsumer
	) {
		this.name = name;
		this.identifier = id(name);
		this.distance = distance;
		DreamLayers.BUILDERS.put(this.identifier, biConsumer);
	}

	public static void initialize() {
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("dream_layer"), DATA_COMPONENT_TYPE);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public Identifier identifier() {
		return identifier;
	}

	public int distance() {
		return distance;
	}
}
