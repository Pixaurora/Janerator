package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.pixaurora.janerator.worldgen.JaneratorGenerator;
import net.pixaurora.janerator.worldgen.generator.MultiGenerator;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin implements JaneratorGenerator {
    private MultiGenerator janerator$parent;

    @Override
    public void janerator$setupMultigen(MultiGenerator parent) {
        this.janerator$parent = parent;
    }

    @Override
    public boolean janerator$isDoingMultigen() {
        return this.janerator$parent != null;
    }

    @Override
    public MultiGenerator janerator$getParent() {
        return this.janerator$parent;
    }

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
        if (! this.janerator$isDoingMultigen()) {
            return;
        }

        BlockPos pos = chunk.getPos().getMiddleBlockPosition(0);

        if (this.janerator$getParent().getGrapher().isPointShaded(pos)) {
            callbackInfo.cancel();
        }
    }
}
