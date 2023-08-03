package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.pixaurora.janerator.RegistryCache;
import net.pixaurora.janerator.config.JaneratorConfig;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void janerator$onLoadLevel(CallbackInfo callbackInfo) {
        RegistryCache.INSTANCE = new RegistryCache((MinecraftServer) (Object) this);
    }

    @Inject(method = "onServerExit", at = @At("HEAD"))
    private void janerator$onServerExit(CallbackInfo callbackInfo) {
        RegistryCache.INSTANCE = null;
        JaneratorConfig.destroy();
    }
}
