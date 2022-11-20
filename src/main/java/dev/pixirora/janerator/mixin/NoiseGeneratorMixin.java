package dev.pixirora.janerator.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import dev.pixirora.janerator.Janerator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseGeneratorMixin {
    @Shadow
    private Holder<NoiseGeneratorSettings> settings;

    protected FlatLevelSource getGenerator() {
        return Janerator.getGenerator(this.settings);
    }

    @Inject(method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideBuildSurface(WorldGenRegion worldGenRegion, StructureManager structureManager,
            RandomState randomState, ChunkAccess chunk, CallbackInfo cInfo) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            cInfo.cancel();
        }
    }

    @Inject(method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideBuildSurface2(
            ChunkAccess chunk,
            WorldGenerationContext worldGenerationContext,
            RandomState randomState,
            StructureManager structureManager,
            BiomeManager biomeManager,
            Registry<Biome> registry,
            Blender blender, CallbackInfo cInfo) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            cInfo.cancel();
        }
    }

    @Inject(method = "fillFromNoise(Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideFillFromNoise(Executor executor, Blender blender, RandomState randomState,
            StructureManager structureManager, ChunkAccess chunk,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            cir.setReturnValue(
                    this.getGenerator().fillFromNoise(executor, blender, randomState, structureManager, chunk));
            cir.cancel();
        }
    }

    @Inject(method = "getBaseHeight(IILnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)I", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideGetBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world,
            RandomState randomState, CallbackInfoReturnable<Integer> cir) {
        if (Janerator.shouldOverride(x, z)) {
            cir.setReturnValue(this.getGenerator().getBaseHeight(x, z, heightmap, world, randomState));
            cir.cancel();
        }
    }

    @Inject(method = "getBaseColumn(IILnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)Lnet/minecraft/world/level/NoiseColumn;", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideGetBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState,
            CallbackInfoReturnable<NoiseColumn> cir) {
        if (Janerator.shouldOverride(x, z)) {
            cir.setReturnValue(this.getGenerator().getBaseColumn(x, z, world, randomState));
            cir.cancel();
        }
    }

    @Inject(method = "doFill(Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;II)Lnet/minecraft/world/level/chunk/ChunkAccess;", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideDoFill(Blender blender, StructureManager structureManager, RandomState randomState,
            ChunkAccess chunk, int startY, int noiseSizeY, CallbackInfoReturnable<ChunkAccess> callbackInfo) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            callbackInfo.setReturnValue(chunk);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "applyCarvers(Lnet/minecraft/server/level/WorldGenRegion;JLnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/GenerationStep$Carving;)V", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void overrideApplyCarvers(
            WorldGenRegion chunkRegion,
            long seed,
            RandomState randomState,
            BiomeManager biomeAccess,
            StructureManager structureManager,
            ChunkAccess chunk,
            GenerationStep.Carving generationStep,
            CallbackInfo callbackInfo) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            callbackInfo.cancel();
        }
    }

    // This is commented out because it makes the chunks inaccessible for some
    // reason I'm not sure of.

    // @Inject(
    // method="createBiomes(Lnet/minecraft/core/Registry;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;",
    // at=@At("HEAD"),
    // cancellable=true,
    // locals=LocalCapture.CAPTURE_FAILHARD
    // )
    // public void overrideCreateBiomes(Registry<Biome> biomeRegistry, Executor
    // executor, RandomState randomState, Blender blender, StructureManager
    // structureManager, ChunkAccess chunk,
    // CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
    // if (Janerator.shouldOverride(chunk.getPos())) {
    // cir.setReturnValue(getGenerator().createBiomes(biomeRegistry, executor,
    // randomState, blender, structureManager, chunk));
    // cir.cancel();
    // }
    // }
}
