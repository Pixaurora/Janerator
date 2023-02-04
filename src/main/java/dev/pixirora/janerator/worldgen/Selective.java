package dev.pixirora.janerator.worldgen;

public interface Selective {
    public default void janerator$stopSelecting() {
        throw new RuntimeException("No implementation for janerator$stopSelecting could be found.");
    }

    public default boolean janerator$allowWrites(int x, int z) {
        throw new RuntimeException("No implementation for janerator$allowWrites could be found.");
    }

    public default boolean janerator$disallowWrites(int x, int z) {
        return !janerator$allowWrites(x, z);
    }
}
