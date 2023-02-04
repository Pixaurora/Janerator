package dev.pixirora.janerator.worldgen;

public interface JaneratorChunk extends Selective {
    public default void janerator$selectWith(PlacementSelector selector, boolean selectInSections) {
        throw new RuntimeException("No implementation for janerator$selectWith could be found.");
    }
}
