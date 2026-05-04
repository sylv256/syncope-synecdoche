package io.github.rehtea.syncope.impl;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	public static final BlockWithItem SYNCOPATOR = register(
			"syncopator",
			Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
					.lightLevel(_ -> 15),
			true
	);
	public static final BlockWithItem INVERTED_SYNCOPATOR = register(
			"inverted_syncopator",
			Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
					.mapColor(MapColor.COLOR_LIGHT_GRAY)
					.lightLevel(_ -> 15),
			true
	);
	public static final BlockWithItem DESYNCOPATOR = register(
			"desyncopator",
			Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
					.mapColor(MapColor.COLOR_BLACK)
					.lightLevel(_ -> 15),
			true
	);
	public static final BlockWithItem INVERTED_DESYNCOPATOR = register(
			"inverted_desyncopator",
			Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
					.mapColor(MapColor.COLOR_LIGHT_GRAY)
					.lightLevel(_ -> 15),
			true
	);

	private ModBlocks() {
	}

	public static void initialize() {
	}

	private static BlockWithItem register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		ResourceKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings.setId(blockKey));
		Item item = null;

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			// Items need to be registered with a different type of registry key, but the ID
			// can be the same.
			ResourceKey<Item> itemKey = keyOfItem(name);

			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
			item = blockItem;
		}

		return new BlockWithItem(Registry.register(BuiltInRegistries.BLOCK, blockKey, block), item);
	}

	private static ResourceKey<Block> keyOfBlock(String name) {
		return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Mod.MOD_ID, name));
	}

	private static ResourceKey<Item> keyOfItem(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mod.MOD_ID, name));
	}

	public record BlockWithItem(Block block, Item item) {
	}
}
