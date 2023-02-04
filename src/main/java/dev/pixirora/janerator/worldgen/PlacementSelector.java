package dev.pixirora.janerator.worldgen;

import java.util.List;

import dev.pixirora.janerator.Janerator;

public class PlacementSelector {
    private List<Integer> placements;

    public PlacementSelector(List<Integer> placements) {
        this.placements = placements;
    } 

    public boolean isWanted(int x, int z) {
        return this.placements.contains(
            Janerator.toListCoordinate(x, z)
        );
    }

    public int size() {
        return this.placements.size();
    }
}
