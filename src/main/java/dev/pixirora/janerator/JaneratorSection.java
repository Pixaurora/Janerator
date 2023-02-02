package dev.pixirora.janerator;

import dev.pixirora.janerator.worldgen.PlacementVerifier;

public interface JaneratorSection {
    public default void janerator$setVerifier(PlacementVerifier verifier) {
        throw new RuntimeException("No implementation for janerator$setPlacementVerifier could be found.");
    }
}
