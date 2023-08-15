package net.pixaurora.janerator.worldgen;

import java.util.List;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphingUtils;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;

    private List<PlacementSelection> selections;
    private ChunkGenerator fallbackGenerator;

    public GeneratorFinder(
        List<ChunkGenerator> generatorMap
    ) {
        this.generatorMap = generatorMap;

        this.selections = this.generatorMap
            .stream()
            .distinct()
            .map(generator -> new PlacementSelection(generator, GraphingUtils.getIndices(this.generatorMap, generator)))
            .toList();
        this.fallbackGenerator = this.selections
            .stream()
            .max((selection1, selection2) -> selection1.size() - selection2.size())
            .get().getUsedGenerator();
    }

    public int size() {
        return this.selections.size();
    }

    public ChunkGenerator getAt(Coordinate coordinate) {
        return this.generatorMap.get(coordinate.toListIndex());
    }

    public List<PlacementSelection> getAllSelections() {
        return this.selections;
    }

    public ChunkGenerator getDefault() {
        return this.fallbackGenerator;
    }
}
