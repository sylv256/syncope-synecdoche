package io.github.rehtea.syncope.impl.command.argument;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public final class EnumArgumentType<T extends Enum<T> & StringRepresentable> implements ArgumentType<T> {
	private static final Map<Class<?>, EnumArgumentType<?>> CACHE = new HashMap<>();
	private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
			arg -> Component.translatableEscape(
					"argument.syncope-synecdoche.enum.invalid",
					(Object[]) arg
			)
	);
	private final Class<T> clazz;
	private final Map<String, T> name2Value;

	private EnumArgumentType(Class<T> clazz) {
		this.clazz = clazz;
		T[] values = Objects.requireNonNull(
				clazz.getEnumConstants(),
				"So-called enum " + clazz.getName() + " has no constants"
		);
		this.name2Value = new HashMap<>(values.length);

		for (T value : values) {
			this.name2Value.put(value.getSerializedName(), value);
		}
	}

	public static <T extends Enum<T> & StringRepresentable> EnumArgumentType<T> of(Class<T> clazz) {
		//noinspection unchecked // Unchecked but safe Class<?> cast
		return (EnumArgumentType<T>)
				CACHE.computeIfAbsent(clazz, key -> new EnumArgumentType<>((Class<T>) key));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
			CommandContext<S> context,
			SuggestionsBuilder builder
	) {
		return context.getSource() instanceof SharedSuggestionProvider
				? SharedSuggestionProvider.suggest(this.name2Value.keySet(), builder)
				: Suggestions.empty();
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		String valueString = reader.readUnquotedString();
		T value = this.name2Value.get(valueString);

		if (value == null) {
			throw ERROR_INVALID.createWithContext(reader, new String[]{valueString, this.clazz.getName()});
		} else {
			return value;
		}
	}
}
