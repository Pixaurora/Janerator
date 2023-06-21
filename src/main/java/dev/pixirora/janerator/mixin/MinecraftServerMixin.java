package dev.pixirora.janerator.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.primitives.Doubles;

import dev.pixirora.janerator.Janerator;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void janerator$onLoadLevel(CallbackInfo callbackInfo) {
        Janerator.makeRegistryCache((MinecraftServer)(Object)this);
        Janerator.LOGGER.info("A" + Doubles.toArray(List.of()).toString());
    }

    @Inject(method = "onServerExit", at = @At("HEAD"))
    private void janerator$onServerExit(CallbackInfo callbackInfo) {
        Janerator.cleanup();
    }
}
