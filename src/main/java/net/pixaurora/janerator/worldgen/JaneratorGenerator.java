package net.pixaurora.janerator.worldgen;

import net.pixaurora.janerator.graphing.ChunkGrapher;
import net.pixaurora.janerator.worldgen.generator.MultiGenerator;

public interface JaneratorGenerator {
    public default void janerator$setupMultiGenerating(ChunkGrapher dimension, MultiGenerator parent) {
        throw new RuntimeException("No implementation for `janerator$setGrapher` could be found.");
    }

    public default boolean janerator$notMultiGenerating() {
        throw new RuntimeException("No implementation for `janerator$notMultiGenerating` could be found.");
    }

    public default ChunkGrapher janerator$getGrapher() {
        throw new RuntimeException("No implementation for `janerator$getGrapher` could be found.");
    }

    public default MultiGenerator janerator$getParent() {
        throw new RuntimeException("No implementation for `janerator$getParent` could be found.");
    }
}
