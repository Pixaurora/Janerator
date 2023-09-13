package net.pixaurora.janerator.worldgen;

import java.util.List;

import net.minecraft.world.level.chunk.ChunkGenerator;

public class FullGeneratorLookup extends GeneratorLookup {
    private GeneratorLookup biomeScale;

    public FullGeneratorLookup(List<ChunkGenerator> blockLevelMapping, List<ChunkGenerator> biomeLevel) {
        super(blockLevelMapping, 16);
        this.biomeScale = new GeneratorLookup(biomeLevel, 4);
    }

    public GeneratorLookup atBiomeScale() {
        return this.biomeScale;
    }
}
