package io.github.rehtea.syncope.client.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import gay.sylv.frappe.api.ext.quad_view.FrappeMutableQuadView;
import gay.sylv.frappe.api.ext.terrain_material.MQV_ExtTerrainMaterial;
import gay.sylv.frappe.api.ext.terrain_material.TerrainMaterial;

import io.github.rehtea.syncope.client.impl.event.ClientItemScrollEvents;
import io.github.rehtea.syncope.client.impl.mixin.Accessor_RenderSectionRegion;
import io.github.rehtea.syncope.client.impl.network.ModClientNetworking;
import io.github.rehtea.syncope.client.impl.render.ModTerrainMaterials;
import io.github.rehtea.syncope.impl.DreamLayers;
import io.github.rehtea.syncope.impl.ModBlocks;
import io.github.rehtea.syncope.impl.attachment.DreamLayer;
import io.github.rehtea.syncope.impl.attachment.MaterialPalette;
import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.attachment.MusicStage;
import io.github.rehtea.syncope.impl.item.ModItems;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundDreamLayerChangePayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundFaintPayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPaletteMaterialChangePayload;
import io.github.rehtea.syncope.impl.network.serverbound.ServerboundPulsePayload;
import io.github.rehtea.syncope.impl.util.FallibleRunnable;
import io.github.rehtea.syncope.impl.util.Yeet;

public class ModClient implements ClientModInitializer {
	public static @Nullable Instant fainted = null;
	public static boolean faintPause = false;
	public static boolean faintNoising = false;
	public static @Nullable Instant playDistance = null;

	public static int getAlpha() {
		if (fainted == null) {
			return 0;
		}

		return Math.min((int) (getMillis() * getMillis() / 255), 255);
	}

	public static float getInverse() {
		return 0.8f * (getAlpha() / 127.0f);
	}

	public static long getMillis() {
		return Objects.requireNonNull(fainted).until(Instant.now()).toMillis();
	}

	public static OptionalInt getColor(int alpha) {
		return OptionalInt.empty();
	}

	private static int getColored(int alpha) {
		return ARGB.color(0, alpha / 12, 0, 0);
	}

	public static OptionalDouble getDepth() {
		return OptionalDouble.empty();
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModelLoadingPlugin.register(context -> {
			context.modifyBlockModelAfterBake()
					.register((model, _) -> new WrapperBlockStateModel(model) {
						@Override
						public void emitQuads(
								QuadEmitter emitter,
								BlockAndTintGetter level,
								BlockPos pos,
								BlockState state,
								RandomSource random,
								Predicate<@Nullable Direction> cullTest
						) {
							ClientLevel clientLevel = null;

							if (!(level instanceof ClientLevel) && !(level instanceof RenderSectionRegion)) {
								super.emitQuads(emitter, level, pos, state, random, cullTest);
								return;
							}

							if (level instanceof RenderSectionRegion region) {
								//noinspection resource
								clientLevel = ((Accessor_RenderSectionRegion) region).syncope_synecdoche$getLevel();
							}

							LevelChunk chunk = Objects.requireNonNull(clientLevel).getChunkAt(pos);
							Int2ObjectMap<@Nullable MaterialPalette> palette = chunk.getAttached(ModAttachments.MATERIAL_PALETTE);

							if (palette == null) {
								super.emitQuads(emitter, level, pos, state, random, cullTest);
								return;
							}

							MaterialPalette palette1 = palette.get(chunk.getSectionIndex(pos.getY()));

							if (palette1 == null) {
								super.emitQuads(emitter, level, pos, state, random, cullTest);
								return;
							}

							PalettedContainer<ModTerrainMaterial> palettedContainer = palette1.palettedContainer();
							ModTerrainMaterial modTerrainMaterial = palettedContainer.get(
									pos.getX() & 15,
									pos.getY() & 15,
									pos.getZ() & 15
							);

							if (modTerrainMaterial.equals(ModTerrainMaterial.DEFAULT)) {
								super.emitQuads(emitter, level, pos, state, random, cullTest);
								return;
							}

							TerrainMaterial material = Objects.requireNonNull(ModTerrainMaterials.MATERIAL_MAP.get(modTerrainMaterial.identifier()), "Terrain Material " + modTerrainMaterial.identifier() + " is unregistered on the client");

							emitter.pushTransform(quad -> {
								FrappeMutableQuadView.of(quad)
										.as(MQV_ExtTerrainMaterial.class)
										.frappe$terrainMaterial(material);
								return true;
							});
							super.emitQuads(emitter, level, pos, state, random, cullTest);
							emitter.popTransform();
						}
					});
		});

		ClientItemScrollEvents.ALLOW.register((inventory, currentSlot, _, _, yOffset) -> {
			if (inventory.getItem(currentSlot).is(ModItems.PALETTE) && inventory.player.isShiftKeyDown()) {
				int polarity = (int) Math.signum(yOffset);
				ModTerrainMaterial terrainMaterialId = inventory.getItem(currentSlot).get(ModTerrainMaterial.TYPE);

				if (terrainMaterialId == null) {
					terrainMaterialId = ModTerrainMaterial.DEFAULT;
				}

				Identifier key = terrainMaterialId.identifier();
				int index = ModTerrainMaterials.IDENTIFIERS.indexOf(key);

				if (index < 0) {
					index = 0;
				}

				int newIndex = index + polarity;

				if (newIndex >= ModTerrainMaterials.IDENTIFIERS.size()) {
					newIndex = 0;
				}

				if (newIndex < 0) {
					newIndex = ModTerrainMaterials.IDENTIFIERS.size() - 1;
				}

				terrainMaterialId = ModTerrainMaterial.REGISTRY.get(ModTerrainMaterials.IDENTIFIERS.get(newIndex)).orElseThrow().value();
				ClientPlayNetworking.send(new ServerboundPaletteMaterialChangePayload(
						currentSlot,
						terrainMaterialId
				));
				return false;
			} else if (inventory.getItem(currentSlot).is(ModItems.THREAD) && inventory.player.isShiftKeyDown()) {
				int polarity = (int) Math.signum(yOffset);
				DreamLayer dreamLayer = inventory.getItem(currentSlot).get(DreamLayer.DATA_COMPONENT_TYPE);

				if (dreamLayer == null) {
					dreamLayer = DreamLayers.SYNECDOCHE;
				}

				int index = DreamLayers.PATH.indexOf(dreamLayer);

				if (index < 0) {
					index = 0;
				}

				int newIndex = index + polarity;

				if (newIndex >= DreamLayers.PATH.size()) {
					newIndex = 0;
				}

				if (newIndex < 0) {
					newIndex = DreamLayers.PATH.size() - 1;
				}

				dreamLayer = DreamLayers.PATH.get(newIndex);
				ClientPlayNetworking.send(new ServerboundDreamLayerChangePayload(
						currentSlot,
						dreamLayer
				));
				return false;
			}

			return true;
		});

		ModClientNetworking.initialize();

		ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> {
			chunk.onAttachedSet(ModAttachments.MATERIAL_PALETTE)
					.register((oldValue, newValue) -> {
						if (newValue != null) {
							ChunkPos pos = chunk.getPos();

							for (Int2ObjectMap.Entry<MaterialPalette> entry : newValue.int2ObjectEntrySet()) {
								Minecraft.getInstance().levelRenderer.setSectionDirty(pos.x(), chunk.getSectionYFromSectionIndex(entry.getIntKey()), pos.z());
							}
						}
					});
		});

		ModKeyMappings.initialize();
		LevelRenderEvents.END_EXTRACTION.register(_ -> {
			if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
				return;
			}

			final long millis = 3962;

			if (fainted != null && faintNoising && Instant.now().isAfter(fainted.plusMillis(millis / 2))) {
				runMc(() -> {
					faintNoising = false;
					ClientPlayNetworking.send(ServerboundFaintPayload.INSTANCE);
				});
			}

			if (ModKeyMappings.FAINT.isDown()) {
				if (fainted == null) {
					fainted = Instant.now();
					faintPause = true;
					runMc(() -> {
							faintNoising = true;
						Minecraft.getInstance().level.playLocalSound(
								Minecraft.getInstance().player,
								SoundEvents.CREEPER_DEATH,
								SoundSource.NEUTRAL,
								1.0f,
								0.4875f
						);
					});
				}
			} else if (fainted != null && Instant.now().isAfter(fainted.plusMillis(3962))) {
				faintPause = false;
				fainted = null;
			}
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null || client.level == null) return;

			Vec3 location = client.player.raycastHitResult(client.missTime, client.getCameraEntity()).getLocation();
			BlockPos blockPos = new BlockPos((int) location.x, (int) location.y, (int) location.z);
			if (client.level.getBlockState(blockPos).is(ModBlocks.DESYNCOPATOR.block()) || client.level.getBlockState(blockPos).is(ModBlocks.INVERTED_DESYNCOPATOR.block())) {
				ClientPlayNetworking.send(new ServerboundPulsePayload(blockPos));
			}
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			if (!client.player.hasAttached(ModAttachments.MUSIC_STAGE)) return;
			MusicManager manager = client.getMusicManager();
			MusicStage attached = client.player.getAttached(ModAttachments.MUSIC_STAGE);
			Music music = ModMusics.STAGE_2_MUSIC.get(attached);
			boolean notPlayingCurrent = !manager.isPlayingMusic(music);
			if (attached != null && notPlayingCurrent && manager.getCurrentMusicTranslationKey() == null) {
				if (playDistance == null && !attached.loop) {
					playDistance = Instant.now().plusSeconds(160);
				}

				if (playDistance == null || Instant.now().isAfter(playDistance)) {
					playDistance = null;
					manager.startPlaying(music);
				}
			}
		});
	}

	public static void runMc(Runnable runnable) {
		Minecraft.getInstance().execute(runnable);
	}

	public static void runOnRendering(Runnable runnable) {
		Minecraft.getInstance().execute(runnable);
	}

	public static <X extends Throwable> void runOnRenderingFallible(Class<X> clazz, FallibleRunnable<X> runnable) throws X {
		try {
			Minecraft.getInstance().execute(() -> {
				try {
					runnable.run();
				} catch (Throwable throwable) {
					if (clazz.isInstance(throwable)) {
						throw new Yeet(throwable);
					} else if (throwable instanceof RuntimeException re) {
						throw re;
					} else {
						throw new RuntimeException(throwable);
					}
				}
			});
		} catch (Yeet yote) {
			//noinspection unchecked
			throw (X) yote.getCause();
		}
	}
}
