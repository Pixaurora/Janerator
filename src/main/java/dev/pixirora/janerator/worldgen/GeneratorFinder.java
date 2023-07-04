package dev.pixirora.janerator.worldgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import dev.pixirora.janerator.Janerator;
import dev.pixirora.janerator.config.OverrideLogic;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GeneratorFinder {
    private List<ChunkGenerator> generatorMap;
    private List<ChunkGenerator> biomeGeneratorMap;
    private List<GeneratorHolder> generators;
    private ChunkGenerator fallbackGenerator;

    public GeneratorFinder(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,
        ChunkAccess chunk
    ) {
        this.generatorMap = new ArrayList<>();
        this.biomeGeneratorMap = new ArrayList<>();

        ChunkPos pos = chunk.getPos();

        List<CompletableFuture<Boolean>> overridingFutures = new ArrayList<>();

        int start_x = pos.getMinBlockX();
        int start_z = pos.getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                overridingFutures.add(
                    CompletableFuture.supplyAsync(
                        new GeneratorFinder.Overrider(x + start_x, z + start_z),
                        Janerator.overridingThreadPool
                    )
                );
            }
        }

        for(CompletableFuture<Boolean> future : overridingFutures) {
            ChunkGenerator generatorAtPos = defaultGenerator;

            try {
                generatorAtPos = future.get() ? modifiedGenerator : defaultGenerator;
            } catch (InterruptedException exception) {
                Janerator.LOGGER.error("Caught InterruptedException during override future.");
            } catch (ExecutionException exception) {
                Janerator.LOGGER.error("Caught ExecutionException during override future.");
            }

            this.generatorMap.add(generatorAtPos);
        }

        // Because biomes are placed per every 4 blocks, we sample 
        // the most common generator in 4 block sections throughout the chunk
        // so that the biome placements line up with the blocks better
        for (int section_x = 0; section_x < 16; section_x += 4) {
            for (int section_z = 0; section_z < 16; section_z += 4) {
                Map<ChunkGenerator, Integer> generatorSample = new HashMap<>();

                for (int x = section_x; x < section_x + 4; x++) {
                    for (int z = section_z; z < section_z + 4; z++) {
                        ChunkGenerator generator = this.getAt(x, z);

                        int currentScore = generatorSample.getOrDefault(generator, 0);
                        generatorSample.put(generator, currentScore + 1);
                    }
                }

                biomeGeneratorMap.add(
                    generatorSample.entrySet()
                        .stream()
                        .max((entry1, entry2) -> entry1.getValue() - entry2.getValue())
                        .get().getKey()
                );
            }
        }

        this.generators = this.generatorMap
            .stream()
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
        return this.generatorMap.get(Janerator.toListCoordinate(x, z));
    }

    public ChunkGenerator getAtForBiomes(int x, int z) {
        return this.biomeGeneratorMap.get(Janerator.toListCoordinate(x, z, 4));
    }

    public List<GeneratorHolder> getAll() {
        return this.generators;
    }

    public ChunkGenerator getDefault() {
        return this.fallbackGenerator;
    }

    public static class Overrider implements Supplier<Boolean> {
        private int x;
        private int z;

        private static ThreadLocal<OverrideLogic> overriding = ThreadLocal.withInitial(() -> new OverrideLogic());

        public Overrider(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public Boolean get() {
            return Overrider.overriding.get().shouldOverride(this.x, this.z);
        }
    }
}
