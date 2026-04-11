package io.github.rehtea.syncope.impl;

import static io.github.rehtea.syncope.impl.Mod.id;

import org.jspecify.annotations.NonNull;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.fantasy.RuntimeLevelHandle;
import xyz.nucleoid.fantasy.util.VoidChunkGenerator;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.gamerules.GameRules;

/// only if the serveur is on here we are.
public final class ModServer {
	@SuppressWarnings("NotNullFieldNotInitialized")
	public static @NonNull RuntimeLevelConfig limboConfig;
	@SuppressWarnings("NotNullFieldNotInitialized")
	public static @NonNull RuntimeLevelHandle limboHandle;

	private ModServer() {
	}

	public static void initialize(MinecraftServer server) {
		RegistryAccess.Frozen registries = server.registryAccess();
		limboConfig = new RuntimeLevelConfig()
				.setDimensionType(registries.getOrThrow(BuiltinDimensionTypes.OVERWORLD))
				.setGenerator(new VoidChunkGenerator(registries.getOrThrow(Biomes.THE_VOID)))
				.setGameRule(GameRules.ADVANCE_TIME, false)
				.setGameRule(GameRules.ADVANCE_WEATHER, false);
	}

	public static void postInitialize(MinecraftServer server) {
		Fantasy fantasy = Fantasy.get(server);
		limboHandle = fantasy.getOrOpenPersistentLevel(id("limbo"), limboConfig);
	}
}
