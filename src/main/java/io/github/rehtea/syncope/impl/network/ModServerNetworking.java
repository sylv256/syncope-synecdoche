package io.github.rehtea.syncope.impl.network;

import java.util.Set;

import xyz.nucleoid.fantasy.RuntimeLevelHandle;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import io.github.rehtea.syncope.impl.DreamLayers;
import io.github.rehtea.syncope.impl.attachment.DreamLayer;
import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundDreamLayerChangePayload;
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
		ServerPlayNetworking.registerGlobalReceiver(
				ServerboundDreamLayerChangePayload.TYPE, (payload, context) -> {
				ServerPlayer player = context.player();
				Inventory inventory = player.getInventory();
				ItemStack stack = inventory.getItem(payload.slot());
				stack.set(DreamLayer.DATA_COMPONENT_TYPE, payload.layer());
				context.player().sendOverlayMessage(Component.translatable("syncope-synecdoche.dream_layer." + payload.layer().identifier().getPath()));
			});
		ServerPlayNetworking.registerGlobalReceiver(ServerboundFaintPayload.TYPE, (_, context) -> {
			ServerPlayer player = context.player();
			DreamLayer dreamLayer = player.getAttached(ModAttachments.DREAM_LAYER);

			if (dreamLayer == null) {
				dreamLayer = DreamLayers.SYNECDOCHE;
			}

			DreamLayer nextLayer = DreamLayers.getNext(dreamLayer);
			RuntimeLevelHandle nextHandle = DreamLayers.getHandle(nextLayer);
			RuntimeLevelHandle handle = DreamLayers.getHandle(dreamLayer);

			if (dreamLayer.identifier().getPath().equals("synecdoche") && !player.level().equals(nextHandle.asLevel())) {
				player.teleportTo(
						nextHandle.asLevel(),
						player.getX(),
						player.getY(),
						player.getZ(),
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
				return;
			}

			if (player.level().equals(handle.asLevel())) {
				if (nextLayer.identifier().getPath().equals("synecdoche")) {
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
				} else {
					player.teleportTo(
							nextHandle.asLevel(),
							player.getX(),
							player.getY(),
							player.getZ(),
							Set.of(),
							player.getYRot(),
							player.getXRot(),
							false
					);
				}
			} else {
				if (dreamLayer.identifier().getPath().equals("synecdoche")) {
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
				} else {
					player.teleportTo(
							handle.asLevel(),
							player.getX(),
							player.getY(),
							player.getZ(),
							Set.of(),
							player.getYRot(),
							player.getXRot(),
							false
					);
				}
			}
		});
	}
}
