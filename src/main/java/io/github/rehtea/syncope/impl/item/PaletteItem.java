package io.github.rehtea.syncope.impl.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;

import io.github.rehtea.syncope.impl.attachment.MaterialPalette;
import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.item.component.ModTerrainMaterial;

public class PaletteItem extends Item {
	public PaletteItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();

		if (!level.isClientSide() && level.getServer() != null) {
			ModTerrainMaterial terrainMaterial = context.getItemInHand().get(ModTerrainMaterial.TYPE);
			BlockPos pos = context.getClickedPos();
			ChunkAccess chunk = level.getChunk(pos);
			Int2ObjectMap<MaterialPalette> attachment = chunk.getAttached(ModAttachments.MATERIAL_PALETTE);

			if (attachment == null) {
				attachment = new Int2ObjectOpenHashMap<>();
			}

			MaterialPalette palette = attachment
					.computeIfAbsent(
							chunk.getSectionIndex(pos.getY()),
							_ -> MaterialPalette.createDefault()
					);
			PalettedContainer<ModTerrainMaterial> palettedContainer = palette.palettedContainer();
			palettedContainer.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, terrainMaterial);
			chunk.setAttached(ModAttachments.MATERIAL_PALETTE, null); // FIXME: ugly hack to force sync
			chunk.setAttached(ModAttachments.MATERIAL_PALETTE, attachment);
			level.playSound(context.getPlayer(), pos, SoundEvents.CRAFTER_CRAFT, SoundSource.PLAYERS, 1.0f, 0.25f);
		} else {
			level.playLocalSound(context.getPlayer(), SoundEvents.CRAFTER_CRAFT, SoundSource.PLAYERS, 1.0f, 0.25f);
		}

		return InteractionResult.SUCCESS_SERVER;
	}
}
