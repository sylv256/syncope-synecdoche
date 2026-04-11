package io.github.rehtea.syncope.impl.item.component;

import static io.github.rehtea.syncope.impl.Mod.id;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record ModTerrainMaterial(Identifier identifier) {
	public static final StreamCodec<RegistryFriendlyByteBuf, ModTerrainMaterial> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			ModTerrainMaterial::identifier,
			ModTerrainMaterial::new
	);
	public static final Codec<ModTerrainMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("identifier").forGetter(ModTerrainMaterial::identifier)
	).apply(instance, ModTerrainMaterial::new));
	public static final DataComponentType<ModTerrainMaterial> TYPE = DataComponentType
			.<ModTerrainMaterial>builder()
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.build();
	public static final Registry<ModTerrainMaterial> REGISTRY = new MappedRegistry<>(
			ResourceKey.createRegistryKey(id("mod_terrain_material")),
			Lifecycle.stable(),
			false
	);
	public static final ModTerrainMaterial DEFAULT = register("default");
	public static final ModTerrainMaterial DESTABILIZE = register("destabilize");
	public static final ModTerrainMaterial DISINTEGRATE = register("disintegrate");
	public static final ModTerrainMaterial SYNCOPATE = register("syncopate");

	public static void initialize() {
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("mod_terrain_material"), TYPE);
	}

	private static ModTerrainMaterial register(String path) {
		Identifier identifier = id(path);
		return Registry.register(REGISTRY, identifier, new ModTerrainMaterial(identifier));
	}
}
