package net.pixaurora.janerator;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

public class RegistryCache {
    public static RegistryCache INSTANCE;

    private MinecraftServer server;

    public RegistryCache(MinecraftServer server) {
        this.server = server;
    }

    public <T> Registry<T> getRegistry(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return this.server.registryAccess().registryOrThrow(registryKey);
    }

    public RegistryAccess getRegistry() {
        return this.server.registryAccess();
    }

    public <T> HolderGetter<T> getProvider(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return this.server.registryAccess().lookupOrThrow(registryKey);
    }
}
