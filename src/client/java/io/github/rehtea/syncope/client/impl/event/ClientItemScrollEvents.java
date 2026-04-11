package io.github.rehtea.syncope.client.impl.event;

import net.minecraft.world.entity.player.Inventory;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientItemScrollEvents {
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, listeners -> (inventory, currentSlot, newSlot, xOffset, yOffset) -> {
		for (Allow listener : listeners) {
			boolean allow = listener.allowScroll(inventory, currentSlot, newSlot, xOffset, yOffset);

			if (!allow) {
				return false;
			}
		}

		return true;
	});

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, listeners -> (inventory, currentSlot, newSlot, xOffset, yOffset) -> {
		for (Before listener : listeners) {
			listener.beforeScroll(inventory, currentSlot, newSlot, xOffset, yOffset);
		}
	});

	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, listeners -> (inventory, currentSlot, newSlot, xOffset, yOffset) -> {
		for (After listener : listeners) {
			listener.afterScroll(inventory, currentSlot, newSlot, xOffset, yOffset);
		}
	});

	@FunctionalInterface
	public interface Allow {
		boolean allowScroll(Inventory inventory, int currentSlot, int newSlot, double xOffset, double yOffset);
	}

	@FunctionalInterface
	public interface Before {
		void beforeScroll(Inventory inventory, int currentSlot, int newSlot, double xOffset, double yOffset);
	}

	@FunctionalInterface
	public interface After {
		void afterScroll(Inventory inventory, int currentSlot, int newSlot, double xOffset, double yOffset);
	}

	private ClientItemScrollEvents() {
	}
}
