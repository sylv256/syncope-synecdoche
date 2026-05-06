package io.github.rehtea.syncope.client.impl.mixin;

import static io.github.rehtea.syncope.impl.Mod.MOD_ID;
import static io.github.rehtea.syncope.impl.Mod.id;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.client.impl.ModClient;

@Mixin(PostChain.class)
public abstract class Mixin_PostChain {
	@Shadow
	public abstract void addToFrame(FrameGraphBuilder frame,
			int screenWidth,
			int screenHeight,
			PostChain.TargetBundle providedTargets);

	@WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain$TargetBundle;of(Lnet/minecraft/resources/Identifier;Lcom/mojang/blaze3d/resource/ResourceHandle;)Lnet/minecraft/client/renderer/PostChain$TargetBundle;"))
	private PostChain.TargetBundle addSyncopeTarget(
			Identifier targetId,
			ResourceHandle<RenderTarget> target,
			Operation<PostChain.TargetBundle> original,
			@Local(name = "frame") FrameGraphBuilder frame,
			@Local(argsOnly = true, name = "mainTarget") RenderTarget mainTarget
	) {
		PostChain.TargetBundle call = original.call(targetId, target);

		if (ModClient.SYNCOPE_RENDER_TARGET.isBound()) {
			RenderTarget renderTarget = ModClient.SYNCOPE_RENDER_TARGET.get();

			if (renderTarget.width != mainTarget.width || renderTarget.height != mainTarget.height) {
				renderTarget.resize(mainTarget.width, mainTarget.height);
			}

			PostChain.TargetBundle targets = PostChain.TargetBundle.of(id("main"), frame.importExternal(MOD_ID + ":main", renderTarget));
			return new PostChain.TargetBundle() {
				@Override
				public void replace(
						Identifier id,
						ResourceHandle<RenderTarget> handle
				) {
					if (id.equals(targetId)) {
						call.replace(id, handle);
					} else {
						targets.replace(id, handle);
					}
				}

				@Override
				public @Nullable ResourceHandle<RenderTarget> get(Identifier id) {
					if (id.equals(targetId)) {
						return call.get(id);
					} else {
						return targets.get(id);
					}
				}
			};
		}

		return call;
	}
}
