package io.github.rehtea.syncope.impl.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.impl.Mod;
import io.github.rehtea.syncope.impl.StreamCodecs;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;

public record ServerboundPaletteMaterialChangePayload(int slot, ModTerrainMaterial material) implements CustomPacketPayload {
	public static final Identifier IDENTIFIER = Mod.id("palette_material_change");
	public static final CustomPacketPayload.Type<ServerboundPaletteMaterialChangePayload> TYPE = new CustomPacketPayload.Type<>(IDENTIFIER);
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPaletteMaterialChangePayload> CODEC = StreamCodec.composite(
			StreamCodecs.INT,
			ServerboundPaletteMaterialChangePayload::slot,
			ModTerrainMaterial.STREAM_CODEC,
			ServerboundPaletteMaterialChangePayload::material,
			ServerboundPaletteMaterialChangePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
