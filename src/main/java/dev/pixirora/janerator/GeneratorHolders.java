package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorHolders {
    private List<GeneratorHolder> generators;
    private ChunkGenerator fallbackGenerator;

    public GeneratorHolders(Map<ChunkGenerator, List<List<Integer>>> generatorMap, ChunkGenerator fallbackGenerator) {
        this.fallbackGenerator = fallbackGenerator;

        this.generators = new ArrayList<>();
        generatorMap.forEach(
            (generator, placements) -> {
                this.generators.add(new GeneratorHolder(generator, placements));
            }
        );
    }

    public ChunkGenerator getAt(int x, int z) {
        for (GeneratorHolder holder : generators) {
            if (holder.isWanted(x, z)) {
                return holder.generator;
            }
        }

        return fallbackGenerator;
    }

    public List<GeneratorHolder> getAll() {
        return generators;
    }

    public ChunkGenerator getDefault() {
        return fallbackGenerator;
    }
}
