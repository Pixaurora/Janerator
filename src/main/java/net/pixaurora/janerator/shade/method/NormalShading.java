package net.pixaurora.janerator.shade.method;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.graphing.Coordinate;

public record NormalShading(String generatorKey) implements SimpleShadingMethod {
    public static final Codec<NormalShading> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("generator_key").forGetter(NormalShading::generatorKey)
        ).apply(instance, NormalShading::new)
    );
    public static final SerialType<ShadingMethod> TYPE = new SerialType<>("simple", CODEC);

    @Override
    public SerialType<? extends ShadingMethod> type() {
        return TYPE;
    }

    @Override
    public String getShade(Coordinate pos) {
        return this.generatorKey;
    }

    @Override
    public List<String> involvedGeneratorKeys() {
        return List.of(this.generatorKey);
    }
}
