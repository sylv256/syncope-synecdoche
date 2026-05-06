package io.github.rehtea.syncope.client.impl.mixin;

import static io.github.rehtea.syncope.client.impl.ModClient.faintPause;
import static io.github.rehtea.syncope.client.impl.ModClient.fainted;
import static io.github.rehtea.syncope.impl.Mod.id;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.client.impl.ModClient;

@Mixin(Minecraft.class)
public abstract class Mixin_Minecraft {
	@Shadow
	public abstract ShaderManager getShaderManager();

	@Shadow
	public abstract RenderTarget getMainRenderTarget();

	@Shadow
	@Final
	public GameRenderer gameRenderer;

	@Unique

	@Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V"))
	private void beforeBlit(boolean advanceGameTime, CallbackInfo ci) {
		if (fainted == null) {
			return;
		}

		if (faintPause) {
			Identifier postId = id("faint");

			if (Instant.now().isBefore(ModClient.fainted.plusMillis(50))) {
				postId = id("faint_begin");
			}

			PostChain postChain = this.getShaderManager().getPostChain(postId, Sets.union(Set.of(id("main")), LevelTargetBundle.MAIN_TARGETS));

			if (postChain != null) {
				// deprecated but i don't give a crap because this is a 26.1-only mod
				//noinspection deprecation
				ScopedValue.where(ModClient.SYNCOPE_RENDER_TARGET, ModClient.renderTarget)
						.run(() -> postChain.process(this.getMainRenderTarget(), ((Accessor_GameRenderer) this.gameRenderer).syncope_synecdoche$getResourcePool()));
			}
		}
	}
}
