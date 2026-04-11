package io.github.rehtea.syncope.client.impl.mixin;

import static io.github.rehtea.syncope.client.impl.ModClient.faintPause;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;

@Mixin(Minecraft.class)
public abstract class Mixin_Minecraft {
	@Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V"))
	private void beforeBlit(boolean advanceGameTime, CallbackInfo ci) {
		final int color = ARGB.color(255, 0, 0, 0);

		if (faintPause) {
			CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
			RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();

			//noinspection EmptyTryBlock
			try (RenderPass _ = commandEncoder.createRenderPass(
					() -> "Syncope",
					Objects.requireNonNull(mainRenderTarget.getColorTextureView()),
					OptionalInt.of(color),
					mainRenderTarget.getDepthTextureView(),
					OptionalDouble.of(0.0)
			)) {
			}
		}
	}
}
