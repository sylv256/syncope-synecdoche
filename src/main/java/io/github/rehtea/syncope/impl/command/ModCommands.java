package io.github.rehtea.syncope.impl.command;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;
import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.Collection;
import java.util.Locale;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.command.ArgumentTypeInfosAccessor;

import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.attachment.MusicStage;
import io.github.rehtea.syncope.impl.command.argument.EnumArgumentInfo;
import io.github.rehtea.syncope.impl.command.argument.EnumArgumentType;
import io.github.rehtea.syncope.impl.layer.DreamLayer;
import io.github.rehtea.syncope.impl.network.clientbound.ClientboundForceFaintPayload;

public final class ModCommands {
	private ModCommands() {
	}

	public static void initialize() {
		// FIXME: Fabric API and type safety bug bullshit god why is this a thing
//		ArgumentTypeRegistry.registerArgumentType(id("enum"), EnumArgumentType.class, new EnumArgumentInfo());
		ArgumentTypeInfo<EnumArgumentType<?>, EnumArgumentInfo.Template> serializer = new EnumArgumentInfo();
		ArgumentTypeInfosAccessor.fabric_getClassMap().put(EnumArgumentType.class, serializer);
		Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, id("enum"), serializer);
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			LiteralArgumentBuilder<CommandSourceStack> rootBuilder = Commands.literal(MOD_ID);
			dispatcher.register(rootBuilder
					.then(Commands.literal("music_stage")
							.then(Commands.argument("targets", EntityArgument.players())
							.then(Commands.argument("music_stage", EnumArgumentType.of(MusicStage.class))
							.executes(context -> {
								Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
								MusicStage stage = context.getArgument("music_stage", MusicStage.class);

								for (ServerPlayer player : players) {
									player.setAttached(ModAttachments.MUSIC_STAGE, stage);
								}

								return 1;
							}))))
					.then(Commands.literal("dream_layer")
							.then(Commands.argument("targets", EntityArgument.players())
							.then(Commands.argument("dream_layer", EnumArgumentType.of(DreamLayer.class))
							.executes(context -> {
								DreamLayer dreamLayer = context.getArgument("dream_layer", DreamLayer.class);
								Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

								for (ServerPlayer player : players) {
									player.setAttached(ModAttachments.DREAM_LAYER, dreamLayer);
								}

								return 1;
							}))))
					.then(Commands.literal("force_faint")
							.then(Commands.argument("targets", EntityArgument.players())
							.executes(context -> {
								Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

								for (ServerPlayer player : players) {
									ServerPlayNetworking.send(player, ClientboundForceFaintPayload.INSTANCE);
								}

								return 1;
							}))));
		});
	}
}
