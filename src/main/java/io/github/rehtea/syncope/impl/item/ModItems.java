package io.github.rehtea.syncope.impl.item;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;
import static io.github.rehtea.syncope.impl.Mod.id;

import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import io.github.rehtea.syncope.impl.block.ModBlocks;
import io.github.rehtea.syncope.impl.layer.DreamLayer;
import io.github.rehtea.syncope.impl.layer.DreamLayers;

public final class ModItems {
	public static final Item PALETTE = register("palette", PaletteItem::new, new Item.Properties());
	public static final Item THREAD = register("thread", ThreadItem::new, new Item.Properties());
	public static final CreativeModeTab TAB = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(THREAD))
			.title(Component.translatable("creativeTab." + MOD_ID))
			.displayItems((_, output) -> {
				output.acceptAll(Stream.of(
						PALETTE,
						THREAD,
						ModBlocks.SYNCOPATOR.item(),
						ModBlocks.DESYNCOPATOR.item(),
						ModBlocks.INVERTED_SYNCOPATOR.item(),
						ModBlocks.INVERTED_DESYNCOPATOR.item()
				).map(ItemStack::new).toList());
			})
			.build();

	public static void initialize() {
		DreamLayers.initialize();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(MOD_ID), TAB);
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
