package io.github.rehtea.syncope.impl.attachment;

import static io.github.rehtea.syncope.impl.Mod.id;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record DreamLayer(Identifier identifier, int distance) {
	public static final Codec<DreamLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("identifier").forGetter(DreamLayer::identifier),
			Codec.INT.fieldOf("distance").forGetter(DreamLayer::distance)
	).apply(instance, DreamLayer::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DreamLayer> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, DreamLayer::identifier,
			ByteBufCodecs.VAR_INT, DreamLayer::distance,
			DreamLayer::new
	);
	public static final DataComponentType<DreamLayer> DATA_COMPONENT_TYPE = DataComponentType.<DreamLayer>builder()
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.build();

	public static void initialize() {
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("dream_node"), DATA_COMPONENT_TYPE);
	}
}
