package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.pixaurora.janerator.graphing.Graphing;
import net.pixaurora.janerator.worldgen.JaneratorGenerator;
import net.pixaurora.janerator.worldgen.MultiGenerator;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin implements JaneratorGenerator {
    public void janerator$setDimension(ResourceKey<Level> dimension) {}

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
        if (((ChunkGenerator) (Object) this) instanceof MultiGenerator) {
            return;
        }

        BlockPos pos = chunk.getPos().getMiddleBlockPosition(0);

        if (Graphing.isOverridden(pos)) {
            callbackInfo.cancel();
        }
    }
}
