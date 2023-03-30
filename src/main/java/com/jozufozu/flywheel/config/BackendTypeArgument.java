package com.jozufozu.flywheel.config;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.jozufozu.flywheel.api.backend.BackendType;
import com.jozufozu.flywheel.lib.backend.BackendTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public enum BackendTypeArgument implements ArgumentType<BackendType> {
	INSTANCE;

	private static final Dynamic2CommandExceptionType INVALID = new Dynamic2CommandExceptionType((found, constants) -> {
		// TODO: don't steal lang
		return new TranslatableComponent("commands.forge.arguments.enum.invalid", constants, found);
	});

	@Override
	public BackendType parse(StringReader reader) throws CommandSyntaxException {
		String string = reader.readUnquotedString();

		BackendType engine = BackendTypes.getBackendType(string);

		if (engine == null) {
			throw INVALID.createWithContext(reader, string, BackendTypes.validNames());
		}

		return engine;
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(BackendTypes.validNames(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return BackendTypes.validNames();
	}

	public static BackendTypeArgument getInstance() {
		return INSTANCE;
	}
}
