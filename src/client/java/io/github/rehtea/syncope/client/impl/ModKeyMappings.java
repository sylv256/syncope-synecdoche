package io.github.rehtea.syncope.client.impl;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public final class ModKeyMappings {
	public static final KeyMapping FAINT = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"Faint",
			InputConstants.Type.KEYSYM,
			InputConstants.KEY_Z,
			KeyMapping.Category.GAMEPLAY
	));

	private ModKeyMappings() {
	}

	public static void initialize() {
	}
}
