package io.github.rehtea.syncope.impl.item;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;
import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

public final class ModItems {
	public static final Item PALETTE = register("palette", PaletteItem::new, new Item.Properties());

	static {
		CreativeModeTabEvents
				.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id(MOD_ID)))
				.register(creativeTab -> creativeTab.accept(PALETTE));
	}

	public static void initialize() {
	}

	private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		// Create the item key.
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));

		// Create the item instance.
		T item = itemFactory.apply(settings.setId(itemKey));

		// Register the item.
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return item;
	}

	private ModItems() {
	}
}
// legalize the character
// …,,,,,,,,,,
