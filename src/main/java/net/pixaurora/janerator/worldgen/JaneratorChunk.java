package net.pixaurora.janerator.worldgen;

import net.minecraft.world.level.chunk.ChunkAccess;

public interface JaneratorChunk extends Selective {
    public default ChunkAccess janerator$withSelection(PlacementSelection selection, boolean selectInSections) {
        throw new RuntimeException("No implementation for janerator$withSelection could be found.");
    }
}
