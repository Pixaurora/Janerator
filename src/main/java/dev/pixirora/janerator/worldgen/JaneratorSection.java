package dev.pixirora.janerator.worldgen;

public interface JaneratorSection extends Selective {
    public default void janerator$setSelector(PlacementSelector selector) {
        throw new RuntimeException("No implementation for janerator$setSelector could be found.");
    }
}
