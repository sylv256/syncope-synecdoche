package io.github.rehtea.syncope.client.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.sounds.MusicManager;

import io.github.rehtea.syncope.client.impl.ModClient;

@Mixin(MusicManager.class)
public abstract class Mixin_MusicManager {
	@WrapMethod(method = "tick")
	private void stopTalkingForSixHoursOnTick(Operation<Void> original) {
		if (!ModClient.shutUpStopTalkingForSixHours) {
			original.call();
		}
	}
}
