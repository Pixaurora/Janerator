package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;
    private List<ChunkGenerator> generatorMapForBiomes;
    private List<GeneratorHolder> generators;
    private ChunkGenerator fallbackGenerator;

    public GeneratorFinder(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,

        ChunkAccess chunk
    ) {
        this.generatorMap = new ArrayList<>();
        this.generatorMapForBiomes = new ArrayList<>();

        ChunkPos pos = chunk.getPos();

        int actual_x = pos.getMinBlockX();
        int actual_z = pos.getMinBlockZ();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                ChunkGenerator generatorAtPos = Janerator.shouldOverride(
                    x + actual_x, z + actual_z
                ) ? modifiedGenerator : defaultGenerator;

                this.generatorMap.add(generatorAtPos);
            }
        }

        // Because biomes are placed per every 4 blocks, we sample 
        // the most common generator in 4 block sections throughout the chunk
        // so that the biome placements line up with the blocks better  
        for (int section_x = 0; section_x < 16; section_x += 4) {
            for (int section_z = 0; section_z < 16; section_z += 4) {
                List<ChunkGenerator> generatorSample = new ArrayList<>();

                for (int x = section_x; x < section_x + 4; x++) {
                    for (int z = section_z; z < section_z + 4; z++) {
                        generatorSample.add(this.getAt(x, z));
                    }
                }

                generatorMapForBiomes.add(
                    generatorSample.stream()
                        .distinct()
                        .max(
                            (generator1, generator2) -> countOf(generator1, generatorSample) - countOf(generator2, generatorSample)
                        ).get()
                );
            }
        }

        this.generators = this.generatorMap.stream()
            .distinct()
            .map(generator ->
                new GeneratorHolder(generator, this.getIndices(generator))
            ).toList();

        this.fallbackGenerator = this.generators.stream()
            .max((holder1, holder2) -> holder1.size()-holder2.size())
            .get().generator;
    }

    private static <T> int countOf(T searchItem, List<T> items) {
        return (int) items.stream().filter(item -> item == searchItem).count();
    }

    private List<Integer> getIndices(ChunkGenerator generator) {
        return IntStream.range(0, 256)
            .filter(index -> this.generatorMap.get(index) == generator)
            .boxed().toList();
    }

    public int size() {
        return this.generatorMap.stream().distinct().toList().size();
    }

    public ChunkGenerator getAt(int x, int z) {
        return this.generatorMap.get(Janerator.toListCoordinate(x, z));
    }

    public ChunkGenerator getAtForBiomes(int x, int z) {
        return this.generatorMapForBiomes.get(Janerator.toListCoordinate(x, z, 4));
    }

    public List<GeneratorHolder> getAll() {
        return generators;
    }

    public ChunkGenerator getDefault() {
        return fallbackGenerator;
    }
}
