package io.github.rehtea.syncope.client.impl.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import io.github.rehtea.syncope.client.impl.ModClient;
import io.github.rehtea.syncope.impl.network.clientbound.ClientboundForceFaintPayload;

public final class ModClientNetworking {
	private ModClientNetworking() {
	}

	public static void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundForceFaintPayload.TYPE, (_, _) -> ModClient.faint());
	}
}
