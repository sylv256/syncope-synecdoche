package io.github.rehtea.syncope.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.item.ModItems;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;
import io.github.rehtea.syncope.impl.network.ModNetworking;

public class Mod implements ModInitializer {
	public static final String MOD_ID = "syncope-synecdoche";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ModBlocks.initialize();
		ModItems.initialize();
		ModAttachments.initialize();
		ModTerrainMaterial.initialize();
		ModNetworking.initialize();
		ModCommands.initialize();
		ServerLifecycleEvents.SERVER_STARTING.register(ModServer::initialize);
		ServerLifecycleEvents.SERVER_STARTED.register(ModServer::postInitialize);
		ServerLifecycleEvents.SERVER_STOPPING.register(_ -> {
			ModServer.HANDLES.clear();
			ModServer.CONFIGS.clear();
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
