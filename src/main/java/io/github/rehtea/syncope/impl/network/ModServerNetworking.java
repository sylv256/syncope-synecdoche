package io.github.rehtea.syncope.impl.network;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import xyz.nucleoid.fantasy.RuntimeLevelHandle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import io.github.rehtea.syncope.impl.DreamLayers;
import io.github.rehtea.syncope.impl.Mod;
import io.github.rehtea.syncope.impl.attachment.DreamLayer;
import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.block.DesyncopatorBlock;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundDreamLayerChangePayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundFaintPayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPaletteMaterialChangePayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPulsePayload;

public final class ModServerNetworking {
//	private static final Map<Block5dPos, Instant> PULSE_TIMERS = new ConcurrentHashMap<>();

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
		ServerPlayNetworking.registerGlobalReceiver(ServerboundPulsePayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			ServerLevel level = player.level();

			BlockState state = level.getBlockState(payload.pos());

			if (state.getBlock() instanceof DesyncopatorBlock block) {
				block.setPowered(true, payload.pos(), state, level);
//				PULSE_TIMERS.put(new Block5dPos(player.level().dimension(), payload.pos()), Instant.now().plusMillis(750));
			}
		});
//
//		ServerTickEvents.START_LEVEL_TICK.register(level -> {
//			for (Map.Entry<Block5dPos, Instant> entry : PULSE_TIMERS.entrySet()) {
//				BlockPos pos = entry.getKey().pos();
//				BlockState state = Objects.requireNonNull(level
//								.getServer()
//								.getLevel(entry.getKey().dimension()))
//						.getBlockState(pos);
//
//				if (entry.getValue().isBefore(Instant.now())) {
//					if (state.getBlock() instanceof DesyncopatorBlock block) {
//						block.setPowered(false, pos, state, level);
//					} else {
//						Mod.LOGGER.warn("Non-desyncopator attempted to unpower @ {}", pos);
//					}
//
//					PULSE_TIMERS.remove(entry.getKey());
//				}
//			}
//		});
	}

	public record Block5dPos(ResourceKey<Level> dimension, BlockPos pos) {
	}
}
