package dev.pixirora.janerator;

import java.util.List;

public class PlacementVerifier {
    private List<Integer> placements;

    public PlacementVerifier(List<Integer> placements) {
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
