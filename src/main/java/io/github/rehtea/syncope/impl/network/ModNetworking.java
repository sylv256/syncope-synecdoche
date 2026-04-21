package io.github.rehtea.syncope.impl.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import io.github.rehtea.syncope.impl.network.serverbound.ServerboundDreamLayerChangePayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundFaintPayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPaletteMaterialChangePayload;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(
				ServerboundPaletteMaterialChangePayload.TYPE,
				ServerboundPaletteMaterialChangePayload.CODEC
		);
		PayloadTypeRegistry.serverboundPlay().register(
				ServerboundDreamLayerChangePayload.TYPE,
				ServerboundDreamLayerChangePayload.CODEC
		);
		PayloadTypeRegistry.serverboundPlay().register(
				ServerboundFaintPayload.TYPE,
				ServerboundFaintPayload.CODEC
		);
		ModServerNetworking.initialize();
	}
}
