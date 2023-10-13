package net.pixaurora.janerator.worldgen;

import net.pixaurora.janerator.worldgen.generator.MultiGenerator;
import net.pixaurora.janerator.worldgen.generator.MultiGenOrganizer;

public interface JaneratorGenerator {
    public default void janerator$setupMultiGen(MultiGenerator organizer) {
        throw new RuntimeException("No implementation for `janerator$setupMultiGen` could be found.");
    }

    public default boolean janerator$isDoingMultigen() {
        throw new RuntimeException("No implementation for `janerator$isDoingMultigen` could be found.");
    }

    public default MultiGenerator janerator$getParent() {
        throw new RuntimeException("No implementation for `janerator$getParent` could be found.");
    }

    public default MultiGenOrganizer janerator$getOrganizer() {
        return this.janerator$getParent().getOrganizer();
    }
}
