package net.pixaurora.janerator.worldgen;

import java.util.List;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;

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
