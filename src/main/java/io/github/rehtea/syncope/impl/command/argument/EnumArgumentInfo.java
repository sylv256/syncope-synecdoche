package io.github.rehtea.syncope.impl.command.argument;

import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class EnumArgumentInfo implements ArgumentTypeInfo<EnumArgumentType<?>, EnumArgumentInfo.Template> {
	@Override
	public void serializeToNetwork(
			Template template,
			FriendlyByteBuf out
	) {
		String name = template.clazz.getName();
		out.writeVarInt(name.length());
		out.writeCharSequence(name, StandardCharsets.UTF_8);
	}

	@Override
	public Template deserializeFromNetwork(FriendlyByteBuf in) {
		int length = in.readVarInt();
		CharSequence name = in.readCharSequence(length, StandardCharsets.UTF_8);
		Class<?> clazz;

		try {
			// FIXME: this is awful and probably a security vulnerability
			clazz = Class.forName(name.toString());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return new Template(clazz);
	}

	@Override
	public Template unpack(EnumArgumentType<?> argument) {
		return new Template(argument.getClazz());
	}

	@Override
	public void serializeToJson(
			Template template,
			JsonObject out
	) {
		out.addProperty("clazz", template.clazz.getName());
	}

	public class Template implements ArgumentTypeInfo.Template<EnumArgumentType<?>> {
		private final Class<?> clazz;

		public Template(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public EnumArgumentType<?> instantiate(CommandBuildContext context) {
			return EnumArgumentType.ofUnsafe(this.clazz);
		}

		@Override
		public ArgumentTypeInfo<EnumArgumentType<?>, ?> type() {
			return EnumArgumentInfo.this;
		}
	}
}
