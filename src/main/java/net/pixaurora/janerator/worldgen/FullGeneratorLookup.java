package net.pixaurora.janerator.worldgen;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.shade.JaneratorLayerData;

public class FullGeneratorLookup extends GeneratorLookup {
    private final List<JaneratorLayerData> layerMap;
    private final GeneratorLookup biomeScale;

    public FullGeneratorLookup(List<ChunkGenerator> blockLevelGeneratorMap, List<JaneratorLayerData> layerMapping, List<ChunkGenerator> biomeLevelMap) {
        super(blockLevelGeneratorMap, 16);
        this.layerMap = layerMapping;

        this.biomeScale = new GeneratorLookup(biomeLevelMap, 4);
    }

    public JaneratorLayerData getLayerAt(Coordinate pos) {
        return this.layerMap.get(pos.makeLegal(this.chunkLength).toListIndex(this.chunkLength));
    }

    public JaneratorLayerData getLayerAt(BlockPos pos) {
        return this.getLayerAt(new Coordinate(pos.getX(), pos.getZ()));
    }

    public GeneratorLookup atBiomeScale() {
        return this.biomeScale;
    }
}
