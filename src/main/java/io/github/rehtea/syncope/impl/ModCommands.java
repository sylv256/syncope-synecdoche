package io.github.rehtea.syncope.impl;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;

import java.util.Locale;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import io.github.rehtea.syncope.impl.attachment.DreamLayer;
import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.attachment.MusicStage;
import io.github.rehtea.syncope.impl.network.clientbound.ClientboundForceFaintPayload;

public final class ModCommands {
	private ModCommands() {
	}

	public static void initialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			dispatcher.register(Commands.literal(MOD_ID + ":music_stage").executes(context -> {
				String id = context.getArgument("music_stage", String.class).toLowerCase(Locale.ROOT);

				for (MusicStage stage : MusicStage.values()) {
					if (!stage.name().toLowerCase(Locale.ROOT).equals(id)) continue;

					context.getSource().getPlayer().setAttached(ModAttachments.MUSIC_STAGE, stage);

					return 0;
				}

				return 67;
			}));

			dispatcher.register(Commands.literal(MOD_ID + ":dream_layer").executes(context -> {
				String id = context.getArgument("dream_layer", String.class).toLowerCase(Locale.ROOT);

				for (DreamLayer layer : DreamLayers.PATH) {
					if (!layer.identifier().getPath().equals(id)) continue;

					context.getSource().getPlayer().setAttached(ModAttachments.DREAM_LAYER, layer);

					return 0;
				}

				return 67;
			}));

			dispatcher.register(Commands.literal(MOD_ID + ":force_faint").executes(context -> {
				ServerPlayer player = context.getArgument("player", ServerPlayer.class);
				ServerPlayNetworking.send(player, ClientboundForceFaintPayload.INSTANCE);
				return 0;
			}));
		});
	}
}
