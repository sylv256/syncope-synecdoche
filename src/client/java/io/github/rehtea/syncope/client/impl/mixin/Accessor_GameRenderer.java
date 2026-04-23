package io.github.rehtea.syncope.client.impl.mixin;

import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public interface Accessor_GameRenderer {
	@Accessor("resourcePool")
	CrossFrameResourcePool syncope_synecdoche$getResourcePool();
}
