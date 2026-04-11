package io.github.rehtea.syncope.impl.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.impl.Mod;

public record ServerboundFaintPayload() implements CustomPacketPayload {
	public static final Identifier IDENTIFIER = Mod.id("faint");
	public static final Type<ServerboundFaintPayload> TYPE = new Type<>(IDENTIFIER);
	public static final ServerboundFaintPayload INSTANCE = new ServerboundFaintPayload();
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFaintPayload> CODEC = StreamCodec.unit(new ServerboundFaintPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
