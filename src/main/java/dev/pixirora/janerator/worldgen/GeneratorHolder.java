package dev.pixirora.janerator.worldgen;

import java.util.List;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorHolder {
    public ChunkGenerator generator;
    private PlacementSelector selector;

    public GeneratorHolder(ChunkGenerator generator, List<Integer> wantedPlacements) {
        this.generator = generator;
        this.selector = new PlacementSelector(wantedPlacements);
    }

    public ChunkAccess makeSelective(ChunkAccess chunk, boolean selectInSections) {
        chunk.janerator$selectWith(this.selector, selectInSections);
        return chunk;
    }

    public int size() {
        return selector.size();
    }
}
