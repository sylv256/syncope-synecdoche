package io.github.rehtea.syncope.impl.attachment;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.network.codec.ByteBufCodecs;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class ModAttachments {
	public static final AttachmentType<Int2ObjectMap<MaterialPalette>> MATERIAL_PALETTE = register(
			"material_palette",
			builder -> builder
					.persistent(Codec.unboundedMap(
							Codec.STRING.xmap(
									str -> Integer.parseInt(str),
									integer -> integer.toString()
							),
							MaterialPalette.CODEC
					).xmap(
							map -> {
								if (map instanceof Int2ObjectMap<MaterialPalette> int2ObjectMap) {
									return int2ObjectMap;
								} else {
									return new Int2ObjectOpenHashMap<>(map);
								}
							},
							Function.identity()
					))
					.syncWith(
							ByteBufCodecs.map(
									Int2ObjectOpenHashMap::new,
									ByteBufCodecs.VAR_INT,
									MaterialPalette.STREAM_CODEC
							),
							AttachmentSyncPredicate.all()
					)
	);

	private ModAttachments() {
	}

	public static void initialize() {
	}

	private static <T> AttachmentType<T> register(String path, Consumer<AttachmentRegistry.Builder<T>> builderConsumer) {
		return AttachmentRegistry.create(id(path), builderConsumer);
	}
}
