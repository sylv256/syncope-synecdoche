package io.github.rehtea.syncope.client.impl.mixin;

import static io.github.rehtea.syncope.impl.Mod.id;

import java.nio.FloatBuffer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.Identifier;

import io.github.rehtea.syncope.client.impl.ModClient;
import io.github.rehtea.syncope.impl.Mod;

@Mixin(PostPass.class)
public abstract class Mixin_PostPass {
	@Shadow
	@Final
	private Identifier outputTargetId;
	@Shadow
	@Final
	private String name;
	@Unique
	@Nullable
	private FloatBuffer floatBuffer;
	@Unique
	@Nullable
	private GpuBuffer gpuBuffer;

	@WrapOperation(method = "lambda$addToFrame$1", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBuffer;)V", ordinal = 1))
	private void setUniforms(
			RenderPass instance,
			String name,
			GpuBuffer value,
			Operation<Void> original
	) {
		if (this.name.split(":")[0].equals(Mod.MOD_ID) && name.equals("FaintConfig")) {
			if (this.floatBuffer != null) {
				MemoryUtil.memFree(this.floatBuffer);
			}

			this.floatBuffer = MemoryUtil.memCallocFloat(2);
			this.floatBuffer.put(0, ModClient.getInverse());
			this.floatBuffer.put(1, ModClient.getAlpha() / 255.0f);

			if (this.gpuBuffer != null) {
				this.gpuBuffer.close();
			}

			this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "FaintConfig", GpuBuffer.USAGE_UNIFORM, MemoryUtil.memByteBuffer(this.floatBuffer));
			original.call(instance, name, this.gpuBuffer);
			return;
		}

		original.call(instance, name, value);
	}
}
