package net.pixaurora.janerator.shade.method;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.config.SpecifiesType;
import net.pixaurora.janerator.graphing.Coordinate;

public interface ShadingMethod extends SpecifiesType<ShadingMethod> {
    public static final List<SerialType<ShadingMethod>> TYPES = new ArrayList<>(List.of(NormalShading.TYPE));
    public static final Codec<ShadingMethod> BASE_CODEC = new SerialType.Group<>("Shading method", TYPES).dispatchCodec();
    public static final Codec<ShadingMethod> CODEC = Codec.either(
        ChunkGenerator.CODEC,
        ShadingMethod.BASE_CODEC
    ).xmap(
        either -> either.map(NormalShading::new, Function.identity()),
        shading -> {
            if (shading instanceof NormalShading basicShading) {
                return Either.left(basicShading.generator());
            } else {
                return Either.right(shading);
            }
        }
    );

    public List<ShadeData> shadeIn(List<Coordinate> points);

    public List<ChunkGenerator> involvedGenerators();

    public default int generatorCount() {
        return this.involvedGenerators().size();
    }
}
