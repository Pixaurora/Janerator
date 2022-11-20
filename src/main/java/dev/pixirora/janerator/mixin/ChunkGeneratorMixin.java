package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import dev.pixirora.janerator.Janerator;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(
        method="applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V",
        at=@At("HEAD"),
        cancellable=true,
        locals=LocalCapture.CAPTURE_FAILHARD
    )
    public void overrideApplyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager, CallbackInfo callbackInfo) {
        if (Janerator.shouldOverride(chunk.getPos())) {
            callbackInfo.cancel();  
        }
    }
}
