package dev.pixirora.janerator.worldgen;

import java.util.List;

import dev.pixirora.janerator.graphing.Coordinate;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PlacementSelection {
    private ChunkGenerator generator;
    private List<Integer> placements;

    public PlacementSelection(ChunkGenerator generator, List<Integer> placements) {
        this.generator = generator;
        this.placements = placements;
    }

    public boolean contains(int x, int z) {
        return this.placements.contains(
            new Coordinate(x, z).toListIndex()
        );
    }

    public ChunkGenerator getUsedGenerator() {
        return this.generator;
    }

    public int size() {
        return this.placements.size();
    }
}
