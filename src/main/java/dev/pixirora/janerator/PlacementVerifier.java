package dev.pixirora.janerator;

import java.util.Arrays;
import java.util.List;

public class PlacementVerifier {
    private List<List<Integer>> wantedPlacements;

    public PlacementVerifier(List<List<Integer>> wantedPlacements) {
        this.wantedPlacements = wantedPlacements;
    } 

    public boolean isWanted(int x, int z) {
        return this.wantedPlacements.contains(
            Arrays.asList(
                Janerator.normalize(x), Janerator.normalize(z)
            )
        );
    }
}
