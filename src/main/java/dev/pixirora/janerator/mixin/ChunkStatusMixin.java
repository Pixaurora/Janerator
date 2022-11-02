package dev.pixirora.janerator.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
// import net.minecraft.server.level.ThreadedLevelLightEngine;
// import java.util.function.Function;
// import com.mojang.datafixers.util.Either;
// import java.util.concurrent.Executor;
import net.minecraft.world.level.chunk.ChunkAccess;
// import net.minecraft.server.level.ChunkHolder;
// import java.util.concurrent.CompletableFuture;
// import java.util.List;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    private static final Logger logger = LoggerFactory.getLogger("ChunkStatusMixin");
	@ModifyArgs(
        method = "(Ljava/util/concurrent/Executor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/function/Function;Ljava/util/List;Z)Ljava/util/concurrent/CompletableFuture;", 
        at = @At(
            value="INVOKE",
            target="Lnet/minecraft/world/level/chunk/ChunkStatus$GenerationTask;doWork(Lnet/minecraft/world/level/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/server/level/ThreadedLevelLightEngine;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;"
            )
        )
	public void overrideFlatWorld(Args args) {
		ChunkAccess chunkAccess = args.get(8);

        if (chunkAccess.getPos().getRegionX() < 0) {
            logger.info("This is a negative chunk in the x axis");
        }
	}
}
