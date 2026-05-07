package io.github.rehtea.syncope.impl;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.fantasy.RuntimeLevelHandle;
import xyz.nucleoid.fantasy.util.VoidChunkGenerator;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.gamerules.GameRules;

import io.github.rehtea.syncope.impl.layer.DreamLayers;

/// only if the serveur is on here we are.
public final class ModServer {
	static final Map<String, RuntimeLevelConfig> CONFIGS = new HashMap<>();
	public static final Map<String, RuntimeLevelHandle> HANDLES = new HashMap<>();

	private ModServer() {
	}

	public static void initialize(MinecraftServer server) {
		RegistryAccess.Frozen registries = server.registryAccess();

		for (Map.Entry<Identifier, BiFunction<MinecraftServer, RuntimeLevelConfig, RuntimeLevelConfig>> entry : DreamLayers.BUILDERS.entrySet()) {
			RuntimeLevelConfig config = new RuntimeLevelConfig()
					.setDimensionType(registries.getOrThrow(BuiltinDimensionTypes.OVERWORLD))
					.setGenerator(new VoidChunkGenerator(registries.getOrThrow(Biomes.THE_VOID)))
					.setGameRule(GameRules.ADVANCE_TIME, false)
					.setGameRule(GameRules.ADVANCE_WEATHER, false);
			CONFIGS.put(entry.getKey().getPath(), entry.getValue().apply(server, config));
		}
	}

	public static void postInitialize(MinecraftServer server) {
		Fantasy fantasy = Fantasy.get(server);

		for (Map.Entry<String, RuntimeLevelConfig> entry : CONFIGS.entrySet()) {
			HANDLES.put(entry.getKey(), fantasy.getOrOpenPersistentLevel(id(entry.getKey()), entry.getValue()));
		}
	}
}
