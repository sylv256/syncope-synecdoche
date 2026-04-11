package io.github.rehtea.syncope.impl.network;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import io.github.rehtea.syncope.impl.ModServer;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundFaintPayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPaletteMaterialChangePayload;

public final class ModServerNetworking {
	private ModServerNetworking() {
	}

	public static void initialize() {
		ServerPlayNetworking.registerGlobalReceiver(ServerboundPaletteMaterialChangePayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			Inventory inventory = player.getInventory();
			ItemStack stack = inventory.getItem(payload.slot());
			stack.set(ModTerrainMaterial.TYPE, payload.material());
			context.player().sendOverlayMessage(Component.translatable("syncope-synecdoche.material." + payload.material().identifier().getPath()));
		});
		ServerPlayNetworking.registerGlobalReceiver(ServerboundFaintPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();

			if (player.level().equals(context.server().overworld())) {
				player.teleportTo(
						ModServer.limboHandle.asLevel(),
						player.getX(),
						player.getY(),
						player.getZ(),
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			} else if (player.level().dimension().equals(ModServer.limboHandle.getRegistryKey())) {
				player.teleportTo(
						context.server().overworld(),
						player.getX(),
						player.getY(),
						player.getZ(),
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			}
		});
	}
}
