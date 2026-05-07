package io.github.rehtea.syncope.impl.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import io.github.rehtea.syncope.impl.attachment.ModAttachments;
import io.github.rehtea.syncope.impl.layer.DreamLayer;

public class ThreadItem extends Item {
	public ThreadItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(
			Level level,
			Player player,
			InteractionHand hand
	) {
		if (!level.isClientSide() && level.getServer() != null) {
			ItemStack stack = player.getItemInHand(hand);

			if (stack.has(DreamLayer.DATA_COMPONENT_TYPE)) {
				player.setAttached(ModAttachments.DREAM_LAYER, stack.get(DreamLayer.DATA_COMPONENT_TYPE));
			}
		}

		return InteractionResult.SUCCESS_SERVER;
	}
}
