package net.pixaurora.janerator.worldgen;

import java.util.List;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;

public class PlacementSelection {
    private ChunkGenerator generator;
    private List<Coordinate> placements;

    public PlacementSelection(ChunkGenerator generator, List<Coordinate> placements) {
        this.generator = generator;
        this.placements = placements;
    }

    public List<Coordinate> getPlacements() {
        return this.placements;
    }

    public ChunkGenerator getUsedGenerator() {
        return this.generator;
    }

    public boolean contains(int x, int z) {
        return this.placements.contains(
            new Coordinate(x, z).makeLegal()
        );
    }

    public int size() {
        return this.placements.size();
    }
}
