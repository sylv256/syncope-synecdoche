package io.github.rehtea.syncope.impl.layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.fantasy.RuntimeLevelHandle;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import io.github.rehtea.syncope.impl.ModServer;

public final class DreamLayers {
	public static final Map<Identifier, BiFunction<MinecraftServer, RuntimeLevelConfig, RuntimeLevelConfig>> BUILDERS = new HashMap<>();
	// In order from start to finish
	public static final DreamLayer SYNECDOCHE = DreamLayer.SYNECDOCHE;
	public static final DreamLayer SYNCOPE = DreamLayer.SYNCOPE;
	public static final DreamLayer SIMILITUDE = DreamLayer.SIMILITUDE;
	public static final DreamLayer LIMBO = DreamLayer.LIMBO;
	public static final DreamLayer LUCIDITY = DreamLayer.LUCIDITY;
	public static final List<DreamLayer> PATH = List.of(DreamLayer.SYNECDOCHE, DreamLayer.SYNCOPE, DreamLayer.SIMILITUDE, DreamLayer.LIMBO, DreamLayer.LUCIDITY);

	private DreamLayers() {
	}

	public static void initialize() {
		DreamLayer.initialize();
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
