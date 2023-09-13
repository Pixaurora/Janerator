package net.pixaurora.janerator.worldgen;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphingUtils;

public class GeneratorLookup {
    private List<ChunkGenerator> generatorMap;
    private int chunkLength;

    private Map<ChunkGenerator, PlacementSelection> selections;
    private ChunkGenerator fallbackGenerator;

    public GeneratorLookup(
        List<ChunkGenerator> generatorMap,
        int chunkLength
    ) {
        this.generatorMap = generatorMap;
        this.chunkLength = chunkLength;

        this.selections = this.generatorMap
            .stream()
            .distinct()
            .map(generator -> new PlacementSelection(generator, GraphingUtils.getIndices(this.generatorMap, generator)))
            .collect(Collectors.toMap(PlacementSelection::getUsedGenerator, Function.identity()));
        this.fallbackGenerator = this.getAllSelections()
            .stream()
            .max((selection1, selection2) -> selection1.size() - selection2.size())
            .get().getUsedGenerator();
    }

    public int size() {
        return this.selections.size();
    }

    public ChunkGenerator getAt(Coordinate coordinate) {
        return this.generatorMap.get(coordinate.makeLegal(this.chunkLength).toListIndex(this.chunkLength));
    }

    public ChunkGenerator getAt(BlockPos pos) {
        return this.getAt(new Coordinate(pos.getX(), pos.getZ()));
    }

    public Collection<PlacementSelection> getAllSelections() {
        return this.selections.values();
    }

    public PlacementSelection getSelection(ChunkGenerator generator) {
        if (! this.selections.containsKey(generator)) {
            throw new RuntimeException("No selection found associated with this generator.");
        }

        return this.selections.get(generator);
    }

    public ChunkGenerator getDefault() {
        return this.fallbackGenerator;
    }
}
