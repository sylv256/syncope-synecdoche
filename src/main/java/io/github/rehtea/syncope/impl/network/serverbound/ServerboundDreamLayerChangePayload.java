package io.github.rehtea.syncope.impl.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.impl.Mod;
import io.github.rehtea.syncope.impl.StreamCodecs;
import io.github.rehtea.syncope.impl.attachment.DreamLayer;

public record ServerboundDreamLayerChangePayload(int slot, DreamLayer layer) implements CustomPacketPayload {
	public static final Identifier IDENTIFIER = Mod.id("dream_layer_change");
	public static final Type<ServerboundDreamLayerChangePayload> TYPE = new Type<>(IDENTIFIER);
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDreamLayerChangePayload> CODEC = StreamCodec.composite(
			StreamCodecs.INT, ServerboundDreamLayerChangePayload::slot,
			DreamLayer.STREAM_CODEC, ServerboundDreamLayerChangePayload::layer,
			ServerboundDreamLayerChangePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
