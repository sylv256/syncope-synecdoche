package io.github.rehtea.syncope.client.impl;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;

import io.github.rehtea.syncope.impl.attachment.MusicStage;

public final class ModMusics {
	public static final Music AS_IS_AND_AS_WAS_BEFORE_LOOP = register("as_is_and_as_was_before.stereo.loop", MusicStage.INFINITUDE);
	public static final Music AS_IS_AND_AS_WAS_BEFORE = register("as_is_and_as_was_before.stereo", MusicStage.SIMILITUDE);
	public static final Music HORIZONTAL_HOURGLASS = register("horizontal_hourglass.stereo", MusicStage.LUCIDITY);
	public static final Music HORIZONTAL_HOURGLASS_LOOP = register("horizontal_hourglass.stereo.loop", MusicStage.LOOPCIDITY);
	public static final Map<MusicStage, Music> STAGE_2_MUSIC = new HashMap<>();

	private ModMusics() {
	}

	public static void initialize() {
	}

	private static Music register(String name, MusicStage stage) {
		Identifier id = id(name);
		Music music = Musics.createGameMusic(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id)));
		STAGE_2_MUSIC.put(stage, music);
		return music;
	}
}
