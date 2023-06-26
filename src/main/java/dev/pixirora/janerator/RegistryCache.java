package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RegistryCache {
    public static RegistryCache INSTANCE;

    private MinecraftServer server;

    private List<ConfiguredFeature<?, ?>> removedFeatures = null;

    public RegistryCache(MinecraftServer server) {
        this.server = server;
    }

    public <T> Registry<T> getRegistry(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return this.server.registryAccess().registryOrThrow(registryKey);
    }

    public <T> HolderGetter<T> getProvider(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return this.server.registryAccess().lookupOrThrow(registryKey);
    }

    private void makeRemovedFeatures() {
        Registry<ConfiguredFeature<?, ?>> featureRegistry = this.getRegistry(Registries.CONFIGURED_FEATURE);

        this.removedFeatures = new ArrayList<>();
        for (ResourceKey<ConfiguredFeature<?, ?>> key: Janerator.getFilteredFeatures()) {
            removedFeatures.add(
                featureRegistry.getHolderOrThrow(key).value()
            );
        }
    }

    public List<ConfiguredFeature<?, ?>> getRemovedFeatures() {
        if (Objects.isNull(removedFeatures)) {
            makeRemovedFeatures();
        }

        return removedFeatures;
    }
}
