package io.github.rehtea.syncope.impl;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class StreamCodecs {
	public static final StreamCodec<ByteBuf, Integer> INT = ByteBufCodecs.INT;

	private StreamCodecs() {
	}
}
