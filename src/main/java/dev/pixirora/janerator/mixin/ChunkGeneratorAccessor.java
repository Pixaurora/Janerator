package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("biomeSource")
    BiomeSource getBiomeSource();

    @Accessor("CODEC")
    Codec<? extends ChunkGenerator> getCodec();
}
