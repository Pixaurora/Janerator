package dev.pixirora.janerator.mixin.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import dev.pixirora.janerator.Janerator;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.ChunkHolder.ChunkLoadingFailure;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    private static void processChunkStuff(ChunkStatus targetStatus, ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        if (Math.abs(pos.x) + Math.abs(pos.z) < 1) {
            Janerator.LOGGER.info(
                "Status of Chunk " + String.valueOf(pos.x) + ", " + String.valueOf(pos.z) + ": " + targetStatus.toString());
        }
    }

    @Inject(method = "m_vbgdocyn", at = @At("HEAD"))
    private static void lambdaInjector1(
        ChunkStatus targetStatus,
        ServerLevel level,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_kmlmrqsn", at = @At("HEAD"))
    private static void lambdaInjector2(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_fkhswqxw", at = @At("HEAD"))
    private static void lambdaInjector3(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_nzdcnnbt", at = @At("HEAD"))
    private static void lambdaInjector4(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_lebfctff", at = @At("HEAD"))
    private static void lambdaInjector5(
        ChunkStatus targetStatus,
        ServerLevel level,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_wclifdrn", at = @At("HEAD"))
    private static void lambdaInjector6(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_xccpmyhv", at = @At("HEAD"))
    private static void lambdaInjector7(
        ChunkStatus targetStatus,
        ServerLevel level,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_qhvbhtvf", at = @At("HEAD"))
    private static void lambdaInjector8(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_tmdxarkg", at = @At("HEAD"))
    private static void lambdaInjector9(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_jfkmhrnr", at = @At("HEAD"))
    private static void lambdaInjector10(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_vqbbnlkp", at = @At("HEAD"))
    private static void lambdaInjector11(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_meahrkeh", at = @At("HEAD"))
    private static void lambdaInjector12(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_uutycyes", at = @At("HEAD"))
    private static void lambdaInjector13(
        ChunkStatus targetStatus,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_dttkcvbh", at = @At("HEAD"))
    private static void lambdaInjector14(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_wmvnnjtz", at = @At("HEAD"))
    private static void lambdaInjector15(
        ChunkStatus targetStatus,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_unftbcnr", at = @At("HEAD"))
    private static void lambdaInjector16(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_yioizakj", at = @At("HEAD"))
    private static void lambdaInjector17(
        ChunkStatus targetStatus,
        ServerLevel level,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_dffwbojp", at = @At("HEAD"))
    private static void lambdaInjector18(
        ChunkStatus targetStatus,
        Executor executor,
        ServerLevel level,
        ChunkGenerator generator,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        boolean bl,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_pqesvfts", at = @At("HEAD"))
    private static void lambdaInjector19(
        ChunkStatus targetStatus,
        ServerLevel level,
        ChunkGenerator generator,
        List<ChunkAccess> chunks,
        ChunkAccess chunk,
        CallbackInfo ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }

    @Inject(method = "m_cyfvgftu", at = @At("HEAD"))
    private static void lambdaInjector20(
        ChunkStatus targetStatus,
        ServerLevel level,
        StructureTemplateManager structureManager,
        ThreadedLevelLightEngine lightEngine,
        Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> function,
        ChunkAccess chunk,
        CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci
    ) {
        processChunkStuff(targetStatus, chunk);
    }
}
