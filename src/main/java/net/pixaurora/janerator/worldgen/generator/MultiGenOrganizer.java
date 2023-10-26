package net.pixaurora.janerator.worldgen.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.config.GraphingConfigException;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.shade.JaneratorLayerData;
import net.pixaurora.janerator.shade.JaneratorLayer;
import net.pixaurora.janerator.shade.method.ShadeData;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;

public class MultiGenOrganizer {
    public static final Codec<MultiGenOrganizer> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            PreparedGenerators.CODEC.fieldOf("generators").forGetter(MultiGenOrganizer::getGenerators),
            Codec.STRING.fieldOf("default_generator_key").forGetter(MultiGenOrganizer::getDefaultGeneratorKey),
            JaneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(MultiGenOrganizer::getLayers)
        ).apply(instance, MultiGenOrganizer::new)
    );

    private final PreparedGenerators generators;
    private final String defaultGeneratorKey;

    private final List<JaneratorLayer> layers;

    private int generatorCount;
    private LoadingCache<ChunkPos, FullGeneratorLookup> selectionCache;

    public MultiGenOrganizer(PreparedGenerators generators, String defaultGeneratorKey, List<JaneratorLayer> layers) {
        this.generators = generators;
        this.defaultGeneratorKey = defaultGeneratorKey;

        this.layers = layers;

        this.validateGeneratorKeys();


        this.generatorCount = this.involvedGenerators().size();

        this.selectionCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(CacheLoader.from(this::createLookup));
    }

    public String getDefaultGeneratorKey() {
        return this.defaultGeneratorKey;
    }

    public ChunkGenerator getDefaultGenerator() {
        return this.generators.get(defaultGeneratorKey);
    }

    public PreparedGenerators getGenerators() {
        return this.generators;
    }

    public List<JaneratorLayer> getLayers() {
        return this.layers;
    }

    private void validateGeneratorKeys() {
        Map<Integer, List<String>> layerErrors = new HashMap<>();

        for (int layerNumber = 0; layerNumber < this.layers.size(); layerNumber++) {
            JaneratorLayer layer = this.layers.get(layerNumber);

            List<String> missingKeys = layer.involvedGeneratorKeys()
                .stream()
                .filter(key -> this.generators.get(key) == null)
                .distinct()
                .toList();

            if (missingKeys.size() > 0) {
                layerErrors.put(layerNumber, missingKeys);
            }
        }

        if (layerErrors.size() > 0) {
            throw new GraphingConfigException(
                String.format(
                    "Generator definitions are missing for the following keys: %s",
                    String.join(
                        "\n",
                        layerErrors.entrySet().stream()
                            .map(entry -> String.format("In Layer %d: [%s]", entry.getKey(), String.join(",", entry.getValue())))
                            .toList()
                    )
                )
            );
        }
    }

    public List<ChunkGenerator> involvedGenerators() {
        List<ChunkGenerator> involvedGenerators = new ArrayList<>();
        involvedGenerators.addAll(this.generators.getAll());

        return involvedGenerators;
    }

    private ChunkGenerator sampleOne(List<ChunkGenerator> regularShading, int sectionX, int sectionZ) {
        Object2IntMap<ChunkGenerator> areaSample = new Object2IntOpenHashMap<>(this.generatorCount);

        ChunkGenerator foundGenerator = null;
        int highestCount = 0;

        int sectionEndX = (sectionX + 1) * 4;
        int sectionEndZ = (sectionZ + 1) * 4;
        for (int x = sectionEndX - 4; x < sectionEndX; x++) {
            for (int z = sectionEndZ - 4; z < sectionEndZ; z++) {
                ChunkGenerator generator = regularShading.get(new Coordinate(x, z).toListIndex());
                int currentCount = areaSample.getOrDefault(generator, 0) + 1;

                if (currentCount > highestCount) {
                    foundGenerator = generator;
                    highestCount = currentCount;
                }

                areaSample.put(generator, currentCount);
            }
        }

        return foundGenerator;
    }

    public List<ChunkGenerator> sampleForBiomes(List<ChunkGenerator> regularShading) {
        List<ChunkGenerator> sampledShading = new ArrayList<>();

        for (int sectionX = 0; sectionX < 4; sectionX++) {
            for (int sectionZ = 0; sectionZ < 4; sectionZ++) {
                sampledShading.add(this.sampleOne(regularShading, sectionX, sectionZ));
            }
        }

        return sampledShading;
    }

    private FullGeneratorLookup createLookup(ChunkPos chunk) {
        List<ChunkGenerator> generatorShading = new ArrayList<>(Collections.nCopies(256, this.getDefaultGenerator()));
        List<JaneratorLayerData> layerShading = new ArrayList<>(Collections.nCopies(256, JaneratorLayerData.DEFAULT));

        for (JaneratorLayer layer : this.layers) {
            for (ShadeData shade : layer.shadesIn(chunk)) {
                generatorShading.set(shade.index(), this.generators.get(shade.generatorKey()));
                layerShading.set(shade.index(), layer);
            }
        }

        return new FullGeneratorLookup(generatorShading, layerShading, this.sampleForBiomes(generatorShading));
    }

    public FullGeneratorLookup getGenerators(ChunkPos chunk) {
        try {
            return this.selectionCache.get(chunk);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public FullGeneratorLookup getGenerators(ChunkAccess chunk) {
        return this.getGenerators(chunk.getPos());
    }

    public ChunkGenerator getGenerator(Coordinate pos) {
        return this.getGenerators(pos.toChunkPos()).getAt(pos);
    }

    public JaneratorLayerData getLayer(Coordinate pos) {
        return this.getGenerators(pos.toChunkPos()).getLayerAt(pos);
    }
}
