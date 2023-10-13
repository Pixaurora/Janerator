package net.pixaurora.janerator.worldgen.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import net.pixaurora.janerator.Janerator;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.shade.JaneratorLayer;
import net.pixaurora.janerator.shade.method.ShadeData;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;

public class MultiGenOrganizer {
    public static final Codec<MultiGenOrganizer> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            JaneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(MultiGenOrganizer::getLayers),
            ChunkGenerator.CODEC.fieldOf("default_generator").forGetter(MultiGenOrganizer::getDefaultGenerator)
        ).apply(instance, MultiGenOrganizer::new)
    );

    private final List<JaneratorLayer> layers;
    private final ChunkGenerator defaultGenerator;

    private int generatorCount;
    private LoadingCache<ChunkPos, FullGeneratorLookup> selectionCache;

    public MultiGenOrganizer(List<JaneratorLayer> layers, ChunkGenerator defaultShade) {
        this.layers = layers;
        this.defaultGenerator = defaultShade;

        this.generatorCount = this.involvedGenerators().size();

        this.selectionCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(CacheLoader.from(this::createLookup));
    }

    public List<JaneratorLayer> getLayers() {
        return layers;
    }

    public ChunkGenerator getDefaultGenerator() {
        return defaultGenerator;
    }

    public List<ChunkGenerator> involvedGenerators() {
        List<ChunkGenerator> involvedGenerators = new ArrayList<>();
        involvedGenerators.add(this.defaultGenerator);
        involvedGenerators.addAll(
            this.layers.stream()
                .flatMap(layer -> layer.getShading().involvedGenerators().stream())
                .toList()
        );

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
        List<ChunkGenerator> shading = new ArrayList<>(Collections.nCopies(256, this.defaultGenerator));

        for (JaneratorLayer layer : this.layers) {
            for (ShadeData shade : layer.shadesIn(chunk)) {
                shading.set(shade.index(), shade.generator());
            }
        }

        return new FullGeneratorLookup(shading, this.sampleForBiomes(shading));
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
}
