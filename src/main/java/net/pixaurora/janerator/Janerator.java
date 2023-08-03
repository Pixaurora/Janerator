package net.pixaurora.janerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.pixaurora.janerator.config.GraphProperties;
import net.pixaurora.janerator.config.JaneratorConfig;
import net.pixaurora.janerator.worldgen.MultiGenerator;

public class Janerator {
    public static final Logger LOGGER = LoggerFactory.getLogger("Janerator");

    public static ChunkGenerator getGeneratorAt(
        ResourceKey<Level> dimension,
        ChunkGenerator defaultGenerator,
        ChunkAccess chunk
    ) {
        JaneratorConfig config = JaneratorConfig.getInstance();

        boolean chunkAlreadyGenerated = chunk instanceof LevelChunk || chunk instanceof ImposterProtoChunk;

        if (chunkAlreadyGenerated || config.missingPresetFor(dimension)) {
            return defaultGenerator;
        }

        GraphProperties dimensionPreset = config.getPresetFor(dimension);

        ChunkGenerator modifiedGenerator = dimensionPreset.getShadedGenerator();
        ChunkGenerator outlineGenerator = dimensionPreset.getOutlineGenerator();

        return new MultiGenerator(defaultGenerator, modifiedGenerator, outlineGenerator, chunk, dimensionPreset);
    }
}
