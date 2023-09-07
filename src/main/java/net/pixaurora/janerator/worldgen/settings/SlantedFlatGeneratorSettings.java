package net.pixaurora.janerator.worldgen.settings;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;

public record SlantedFlatGeneratorSettings(List<BlockState> blocks, int height, GraphFunctionDefinition yOffsetDefinition) {
    public static final Codec<SlantedFlatGeneratorSettings> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().xmap(Block::defaultBlockState, BlockState::getBlock).listOf().fieldOf("block").forGetter(SlantedFlatGeneratorSettings::blocks),
            Codec.INT.fieldOf("height").forGetter(SlantedFlatGeneratorSettings::height),
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("yOffset(x, z)").forGetter(SlantedFlatGeneratorSettings::yOffsetDefinition)
        ).apply(instance, SlantedFlatGeneratorSettings::new)
    );
}
