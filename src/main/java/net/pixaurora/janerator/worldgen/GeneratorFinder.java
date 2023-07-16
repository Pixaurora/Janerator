package net.pixaurora.janerator.worldgen;

import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graph.Coordinate;
import net.pixaurora.janerator.graph.GraphedChunk;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;
    private List<ChunkGenerator> biomeGeneratorMap;

    private ChunkGenerator fallbackGenerator;
    private List<PlacementSelection> selections;

    public GeneratorFinder(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,
        ChunkGenerator outlineGenerator,
        GraphedChunk graphedArea
    ) {
        this.generatorMap = graphedArea.getGeneratorMap(defaultGenerator, modifiedGenerator, outlineGenerator);
        this.biomeGeneratorMap = graphedArea.sampleBiomeGeneratorMap(defaultGenerator, modifiedGenerator);

        this.selections = this.generatorMap
            .stream()
            .distinct()
            .map(generator -> new PlacementSelection(generator, this.getIndices(generator)))
            .toList();

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
