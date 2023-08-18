package net.pixaurora.janerator.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.worldgen.generator.MultiGenerator;

public class JaneratorConfig {
    public static Codec<JaneratorConfig> CODEC =
        RecordCodecBuilder.create(
            instance -> instance.group(
                GraphProperties.CODEC.listOf().fieldOf("presets").forGetter(JaneratorConfig::getAllPresets)
                )
                .apply(instance, JaneratorConfig::new)
        );

    private static JaneratorConfig INSTANCE = null;

    public static JaneratorConfig getInstance() {
        if (Objects.isNull(JaneratorConfig.INSTANCE)) {
            throw new RuntimeException("Janerator's Config was accessed before creation.");
        }

        return JaneratorConfig.INSTANCE;
    }

    public static void create() {
        JaneratorConfig.INSTANCE = new ConfigFileManager().createConfig();
    }

    public static void destroy() {
        JaneratorConfig.INSTANCE = null;
    }

    private Map<ResourceKey<Level>, GraphProperties> presets;
    private Map<ResourceKey<Level>, MultiGenerator> generators;

    public JaneratorConfig(List<GraphProperties> presets) {
        this.presets = new HashMap<>(presets.size());
        this.generators = new HashMap<>(presets.size());

        for (GraphProperties preset : presets) {
            this.presets.put(preset.getDimension(), preset);
        }
    }

    public List<GraphProperties> getAllPresets() {
        return this.presets.values().stream()
            .toList();
    }

    public GraphProperties getPresetFor(ResourceKey<Level> dimension) {
        return this.presets.get(dimension);
    }

    public boolean missingPresetFor(ResourceKey<Level> dimension) {
        return ! this.presets.containsKey(dimension);
    }

    public synchronized ChunkGenerator createGenerator(ResourceKey<Level> dimension, ChunkGenerator defaultGenerator) {
        GraphProperties preset = this.presets.get(dimension);

        return this.generators.computeIfAbsent(
            dimension,
            (dim) -> new MultiGenerator(
                preset.getGrapher(),
                defaultGenerator,
                preset.getShadedGenerator(),
                preset.getOutlinesGenerator()
            )
        );
    }

    public ChunkGenerator getIntendedGenerator(ResourceKey<Level> dimension, ChunkGenerator defaultGenerator) {
        if (this.missingPresetFor(dimension)) {
            return defaultGenerator;
        }

        if (! this.generators.containsKey(dimension)) {
            return this.createGenerator(dimension, defaultGenerator);
        } else {
            return this.generators.get(dimension);
        }
    }
}
