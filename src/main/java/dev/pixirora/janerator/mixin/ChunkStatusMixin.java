package dev.pixirora.janerator.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.datafixers.util.Either;

import dev.pixirora.janerator.Janerator;
import dev.pixirora.janerator.MultiGenerator;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Redirect(
        method = "generate(Ljava/util/concurrent/Executor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/function/Function;Ljava/util/List;Z)Ljava/util/concurrent/CompletableFuture;", 
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/world/level/chunk/ChunkStatus$GenerationTask;doWork(Lnet/minecraft/world/level/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> overrideGeneratorType(
        ChunkStatus.GenerationTask generationTask,
        ChunkStatus chunkStatus,
        Executor executor,
        ServerLevel world,
        ChunkGenerator normalGenerator,
        StructureTemplateManager structureTemplateManager,
        ThreadedLevelLightEngine threadedLevelLightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function,
        List<ChunkAccess> list,
        ChunkAccess chunk,
        boolean bl
    ) {
        MultiGenerator finalGenerator = Janerator.getGeneratorAt(chunk.getPos(), world.dimension(), normalGenerator);

        return finalGenerator.doGeneration(generationTask, chunkStatus, executor, world, structureTemplateManager, threadedLevelLightEngine, function, list, chunk, bl);
    }
}
