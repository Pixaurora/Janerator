package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MultiGenerator {
    Map<ChunkGenerator, List<List<Integer>>> generatorMap;
    ChunkGenerator majorityGenerator;

    public MultiGenerator(Map<ChunkGenerator, List<List<Integer>>> generatorMap) {
        this.generatorMap = generatorMap;
    }

    public boolean isOneGenerator() {
        return this.generatorMap.keySet().size() == 1;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doGeneration(
        ChunkStatus.GenerationTask generationTask,
        ChunkStatus chunkStatus,
        Executor executor,
        ServerLevel world,
        StructureTemplateManager structureTemplateManager,
        ThreadedLevelLightEngine threadedLevelLightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function,
        List<ChunkAccess> list,
        ChunkAccess chunk,
        boolean bl
    ) {
        if (this.generatorMap.size() == 1) {
            ChunkGenerator onlyGenerator = (ChunkGenerator) this.generatorMap.keySet().toArray()[0];
            return generationTask.doWork(chunkStatus, executor, world, onlyGenerator, structureTemplateManager, threadedLevelLightEngine, function, list, chunk, bl);
        }

        List<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> futures = new ArrayList<>();

        for (Entry<ChunkGenerator, List<List<Integer>>> generatorEntry : generatorMap.entrySet()) {
            ChunkGenerator correctGenerator = generatorEntry.getKey();
            ChunkAccess selectivePlacer = SelectiveProtoChunk.getMeIfNecessary(chunk, generatorEntry.getValue());

            futures.add(
                generationTask.doWork(
                    chunkStatus, 
                    executor, 
                    world, 
                    correctGenerator, 
                    structureTemplateManager, 
                    threadedLevelLightEngine, 
                    function, 
                    list, 
                    selectivePlacer, 
                    bl
                )
            );
        }

        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generatedChunk = new CompletableFuture<>();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).whenCompleteAsync(
            (result, error) -> {
                if (error == null) {
                    Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> value = null;
                    for (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future: futures) {
                        try {
                            value = future.get();
                            if (value.right().isPresent()) {
                                generatedChunk.complete(value);
                                value = null;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            generatedChunk.completeExceptionally(e);
                            value = null;
                        }
                        if (value == null) {
                            break;
                        }
                    }

                    if (value != null) {
                        generatedChunk.complete(value);
                    }
                } else {
                    generatedChunk.completeExceptionally(error);
                } 
            }
        );

        return generatedChunk;
    }
}
