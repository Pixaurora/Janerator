package dev.pixirora.janerator.worldgen;

public interface JaneratorSection extends Selective {
    public default void janerator$setSelection(PlacementSelection selection) {
        throw new RuntimeException("No implementation for janerator$setSelection could be found.");
    }
}
