package io.github.rehtea.syncope.impl;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.fantasy.RuntimeLevelHandle;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import io.github.rehtea.syncope.impl.attachment.DreamLayer;

public final class DreamLayers {
	public static final Map<Identifier, BiFunction<MinecraftServer, RuntimeLevelConfig, RuntimeLevelConfig>> BUILDERS = new HashMap<>();
	// In order from start to finish
	public static final DreamLayer SYNECDOCHE = register(
			"synecdoche",
			0,
			(_, config) -> config
	);
	public static final DreamLayer SYNCOPE = register(
			"syncope",
			2,
			(_, config) -> config
	);
	public static final DreamLayer SIMILITUDE = register(
			"similitude",
			4,
			(_, config) -> config
	);
	public static final DreamLayer LIMBO = register(
			"limbo",
			7,
			(_, config) -> config
	);
	public static final DreamLayer LUCIDITY = register(
			"lucidity",
			1,
			(_, config) -> config
	);
	public static final List<DreamLayer> PATH = List.of(SYNECDOCHE, SYNCOPE, SIMILITUDE, LIMBO, LUCIDITY);

	private DreamLayers() {
	}

	public static DreamLayer register(String path, int distance, BiFunction<MinecraftServer, RuntimeLevelConfig, RuntimeLevelConfig> biConsumer) {
		Identifier id = id(path);
		BUILDERS.put(id, biConsumer);
		return new DreamLayer(id, distance);
	}

	public static RuntimeLevelHandle getHandle(DreamLayer layer) {
		RuntimeLevelHandle handle = ModServer.HANDLES.get(layer.identifier().getPath());
		return Objects.requireNonNull(handle, layer + " does not have a level handle");
	}

	public static DreamLayer getNext(DreamLayer layer) {
		int index = PATH.indexOf(layer) + 1;

		if (index >= PATH.size()) {
			index = 0;
		}

		return PATH.get(index);
	}
}
