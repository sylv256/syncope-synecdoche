package io.github.rehtea.syncope.impl.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.impl.Mod;

public record ClientboundForceFaintPayload() implements CustomPacketPayload {
	public static final Identifier IDENTIFIER = Mod.id("force_faint");
	public static final Type<ClientboundForceFaintPayload> TYPE = new Type<>(IDENTIFIER);
	public static final ClientboundForceFaintPayload INSTANCE = new ClientboundForceFaintPayload();
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundForceFaintPayload> CODEC = StreamCodec.unit(new ClientboundForceFaintPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
