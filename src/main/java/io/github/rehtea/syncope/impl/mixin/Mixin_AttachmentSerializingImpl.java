package io.github.rehtea.syncope.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;

// you surely will not regret Mixin to Fabric API internals in production code
@Mixin(AttachmentSerializingImpl.class)
public abstract class Mixin_AttachmentSerializingImpl {
	@WrapOperation(method = "serializeAttachmentData", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
	private void fixSerialzeAttachmentData() {
	}
}
