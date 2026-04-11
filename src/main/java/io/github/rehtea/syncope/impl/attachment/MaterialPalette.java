package io.github.rehtea.syncope.impl.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.Strategy;

import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;

public record MaterialPalette(PalettedContainer<ModTerrainMaterial> palettedContainer) {
	public static final Strategy<ModTerrainMaterial> STRATEGY = Strategy.createForBlockStates(ModTerrainMaterial.REGISTRY);
	public static final Codec<PalettedContainer<ModTerrainMaterial>> PALETTED_CONTAINER_CODEC = PalettedContainer.codecRW(
			ModTerrainMaterial.CODEC, STRATEGY,
			ModTerrainMaterial.DEFAULT
	);
	public static final Codec<MaterialPalette> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PALETTED_CONTAINER_CODEC.fieldOf("paletted_container").forGetter(MaterialPalette::palettedContainer)
	).apply(instance, MaterialPalette::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, MaterialPalette> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodecWithRegistries(PALETTED_CONTAINER_CODEC),
			MaterialPalette::palettedContainer,
			MaterialPalette::new
	);

	public static MaterialPalette createDefault() {
		return new MaterialPalette(new PalettedContainer<>(ModTerrainMaterial.DEFAULT, MaterialPalette.STRATEGY));
	}
}
