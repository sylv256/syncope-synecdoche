package io.github.rehtea.syncope.impl.attachment;

import java.util.Locale;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum MusicStage implements StringRepresentable {
	INFINITUDE("as_is_and_as_was_before", true, 0),
	SIMILITUDE("as_is_and_as_was_before", false, 1),
	LUCIDITY("horizontal_hourglass", false, 2),
	LOOPCIDITY("horizontal_hourglass", true, 3),
	NONE("none", true, 4); // super secret unused content

	public static final Codec<MusicStage> CODEC = StringRepresentable.fromEnum(MusicStage::values);
	public static final StreamCodec<ByteBuf, MusicStage> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(
			MusicStage::id,
			MusicStage.values(),
			ByIdMap.OutOfBoundsStrategy.WRAP
	), MusicStage::id);

	public final String musique;
	public final boolean loop;
	public final int id;

	MusicStage(String musique, boolean loop,
			int id
	) {
		this.id = id;
		musique = musique + "_stereo";

		if (loop) {
			musique += "_loop";
		}

		this.musique = musique;
		this.loop = loop;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public int id() {
		return this.id;
	}
}
