package io.github.rehtea.syncope.impl.command;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;

import java.util.Collection;
import java.util.Locale;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.attachment.MusicStage;
import io.github.rehtea.syncope.impl.command.argument.EnumArgumentType;
import io.github.rehtea.syncope.impl.layer.DreamLayer;
import io.github.rehtea.syncope.impl.layer.DreamLayers;
import io.github.rehtea.syncope.impl.network.clientbound.ClientboundForceFaintPayload;

public final class ModCommands {
	private ModCommands() {
	}

	public static void initialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			LiteralArgumentBuilder<CommandSourceStack> rootBuilder = Commands.literal(MOD_ID);
			dispatcher.register(rootBuilder
					.then(Commands.literal("music_stage").executes(context -> {
						ServerPlayer player = context.getArgument("player", ServerPlayer.class);
						String id = context.getArgument("music_stage", String.class).toLowerCase(Locale.ROOT);

						for (MusicStage stage : MusicStage.values()) {
							if (!stage.name().toLowerCase(Locale.ROOT).equals(id)) continue;

							player.setAttached(ModAttachments.MUSIC_STAGE, stage);

							return 1;
						}

						return 67;
					}))
					.then(Commands.literal("dream_layer")
							.then(Commands.argument("dream_layer", EnumArgumentType.of(DreamLayer.class)))
							.then(Commands.argument("targets", EntityArgument.players()))
							.executes(context -> {
								DreamLayer dreamLayer = context.getArgument("dream_layer", DreamLayer.class);
								Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

								for (ServerPlayer player : players) {
									player.setAttached(ModAttachments.DREAM_LAYER, dreamLayer);
								}

								return 1;
							}))
					.then(Commands.literal("force_faint")
							.then(Commands.argument("targets", EntityArgument.players()))
							.executes(context -> {
								Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

								for (ServerPlayer player : players) {
									ServerPlayNetworking.send(player, ClientboundForceFaintPayload.INSTANCE);
								}

								return 1;
							})));
		});
	}
}
