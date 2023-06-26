package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.pixirora.janerator.RegistryCache;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void janerator$onLoadLevel(CallbackInfo callbackInfo) {
        RegistryCache.INSTANCE = new RegistryCache((MinecraftServer) (Object) this);
    }

    @Inject(method = "onServerExit", at = @At("HEAD"))
    private void janerator$onServerExit(CallbackInfo callbackInfo) {
        RegistryCache.INSTANCE = null;
    }
}
