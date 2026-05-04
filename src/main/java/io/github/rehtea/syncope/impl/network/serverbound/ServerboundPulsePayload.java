package io.github.rehtea.syncope.impl.network.serverbound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.impl.Mod;

public record ServerboundPulsePayload(BlockPos pos) implements CustomPacketPayload {
	public static final Identifier IDENTIFIER = Mod.id("pulse");
	public static final Type<ServerboundPulsePayload> TYPE = new Type<>(IDENTIFIER);
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPulsePayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			ServerboundPulsePayload::pos,
			ServerboundPulsePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
