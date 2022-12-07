package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.pixirora.janerator.Janerator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(
        method = "createStructures(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGeneratorStructureState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void janerator$onCreateStructures(
        RegistryAccess registryManager,
        ChunkGeneratorStructureState chunkGeneratorStructureState,
        StructureManager structureManager,
        ChunkAccess chunk,
        StructureTemplateManager templateManager,
        CallbackInfo callbackInfo
    ) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            callbackInfo.cancel();
        }
    }
}
