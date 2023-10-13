package net.pixaurora.janerator.shade.method;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.graphing.Coordinate;

public record NormalShading(ChunkGenerator generator) implements SimpleShadingMethod {
    public static final Codec<NormalShading> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ChunkGenerator.CODEC.fieldOf("generator").forGetter(NormalShading::generator)
        ).apply(instance, NormalShading::new)
    );
    public static final SerialType<ShadingMethod> TYPE = new SerialType<>("simple", CODEC);

    @Override
    public SerialType<? extends ShadingMethod> type() {
        return TYPE;
    }

    @Override
    public ShadeData getShade(Coordinate pos) {
        return new ShadeData(pos, this.generator);
    }

    @Override
    public List<ChunkGenerator> involvedGenerators() {
        return List.of(this.generator);
    }
}
