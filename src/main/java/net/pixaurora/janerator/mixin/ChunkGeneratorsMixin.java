package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.pixaurora.janerator.worldgen.generator.SlantedFlatGenerator;

@Mixin(ChunkGenerators.class)
public class ChunkGeneratorsMixin {
    @Inject(method="bootstrap", at=@At("HEAD"))
    private static void janerator$stopSelectingaddJaneratorGenerators(Registry<Codec<? extends ChunkGenerator>> registry, CallbackInfoReturnable<Codec<? extends ChunkGenerator>> cir) {
        Registry.register(registry, new ResourceLocation("janerator", "slanted_flat"), SlantedFlatGenerator.CODEC);
    }
}
