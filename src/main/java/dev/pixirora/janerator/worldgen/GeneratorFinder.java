package dev.pixirora.janerator.worldgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import dev.pixirora.janerator.graphing.Coordinate;
import dev.pixirora.janerator.graphing.Graphing;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;
    private List<ChunkGenerator> biomeGeneratorMap;
    private List<PlacementSelection> selections;

    private ChunkGenerator fallbackGenerator;

    public GeneratorFinder(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,
        ChunkGenerator outlineGenerator,
        ChunkAccess chunk
    ) {
        this.biomeGeneratorMap = new ArrayList<>();

        ChunkPos pos = chunk.getPos();

        List<CompletableFuture<Boolean>> graphingFutures = new ArrayList<>();

        int startX = pos.getMinBlockX();
        int startZ = pos.getMinBlockZ();

        int endX = startX + 16;
        int endZ = startZ + 16;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                graphingFutures.add(Graphing.scheduleGraphing(x, z));
            }
        }

        this.generatorMap = graphingFutures
            .stream()
            .map(future -> Graphing.completeGraphing(future))
            .map(shouldOverride -> shouldOverride ? modifiedGenerator : defaultGenerator)
            .toList();
        this.generatorMap = new ArrayList<>(this.generatorMap);

        // Because biomes are placed per every 4 blocks, we sample 
        // the most common generator in 4 block sections throughout the chunk
        // so that the biome placements line up with the blocks better
        for (int section_x = 0; section_x < 16; section_x += 4) {
            for (int section_z = 0; section_z < 16; section_z += 4) {
                Map<ChunkGenerator, Integer> generatorSample = new HashMap<>();
 
                for (int x = section_x; x < section_x + 4; x++) {
                    for (int z = section_z; z < section_z + 4; z++) {
                        ChunkGenerator generator = this.getAt(new Coordinate(x, z));

                        int currentScore = generatorSample.getOrDefault(generator, 0);
                        generatorSample.put(generator, currentScore + 1);
                    }
                }

                biomeGeneratorMap.add(
                    generatorSample.entrySet()
                        .stream()
                        .max((entry1, entry2) -> entry1.getValue() - entry2.getValue())
                        .get().getKey()
                );
            }
        }

        List<ChunkGenerator> uniqueGenerators = this.generatorMap
            .stream()
            .distinct()
            .toList();
        if (uniqueGenerators.size() > 1) {
            List<Coordinate> coordinates = this.getIndices(modifiedGenerator)
                .stream()
                .map(Coordinate::fromListIndex)
                .toList();

            for (Coordinate coordinate : coordinates) {
                if (coordinate.getNeighbors().anyMatch(coord -> this.getAt(coord) == defaultGenerator)) {
                    this.generatorMap.set(coordinate.toListIndex(), outlineGenerator);
                }
            }
        }

        this.selections = this.generatorMap
            .stream()
            .distinct()
            .map(generator ->
                new PlacementSelection(generator, this.getIndices(generator))
            ).toList();

        this.fallbackGenerator = this.selections
            .stream()
            .max((selection1, selection2) -> selection1.size() - selection2.size())
            .get().getUsedGenerator();
    }

    private List<Integer> getIndices(ChunkGenerator generator) {
        return IntStream.range(0, 256)
            .filter(index -> this.generatorMap.get(index) == generator)
            .boxed().toList();
    }

    public int size() {
        return this.generatorMap.stream().distinct().toList().size();
    }

    public ChunkGenerator getAt(Coordinate coordinate) {
        return this.generatorMap.get(coordinate.toListIndex());
    }

    public ChunkGenerator getAtForBiomes(Coordinate coordinate) {
        return this.biomeGeneratorMap.get(coordinate.toListIndex());
    }

    public List<PlacementSelection> getAllSelections() {
        return this.selections;
    }

    public ChunkGenerator getDefault() {
        return this.fallbackGenerator;
    }
}
