package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;
    private List<GeneratorHolder> generators;
    private ChunkGenerator fallbackGenerator;

    public GeneratorFinder(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,
        ChunkAccess chunk
    ) {
        this.generatorMap = new ArrayList<>();

        ChunkPos pos = chunk.getPos();

        int actual_x = pos.getMinBlockX();
        int actual_z = pos.getMinBlockZ();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                ChunkGenerator generatorAtPos = Janerator.shouldOverride(x + actual_x, z + actual_z) ? modifiedGenerator : defaultGenerator;

                this.generatorMap.add(generatorAtPos);
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

    private List<Integer> getIndices(ChunkGenerator generator) {
        return IntStream.range(0, 256)
            .filter(index -> this.generatorMap.get(index) == generator)
            .boxed().toList();
    }

    public int size() {
        return this.generatorMap.stream().distinct().toList().size();
    }

    public ChunkGenerator getAt(int x, int z) {
        return generatorMap.get(Janerator.toListCoordinate(x, z));
    }

    public List<GeneratorHolder> getAll() {
        return generators;
    }

    public ChunkGenerator getDefault() {
        return fallbackGenerator;
    }
}
