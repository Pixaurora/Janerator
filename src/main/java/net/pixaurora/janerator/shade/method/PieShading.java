package net.pixaurora.janerator.shade.method;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Mth;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.graphing.Coordinate;

public class PieShading implements SimpleShadingMethod {
    public static final Codec<PieShading> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("generator_keys").forGetter(PieShading::involvedGeneratorKeys)
        ).apply(instance, PieShading::new)
    );
    public static final SerialType<ShadingMethod> TYPE = new SerialType<>("pie", CODEC);

    private final List<String> generatorKeys;

    private final double sliceArcLength;
    private final int lastSliceIndex;

    public PieShading(List<String> generatorKeys) {
        this.generatorKeys = generatorKeys;

        this.sliceArcLength = 2 * Math.PI / (generatorKeys.size());
        this.lastSliceIndex = generatorKeys.size() - 1;
    }

    @Override
    public SerialType<? extends ShadingMethod> type() {
        return TYPE;
    }

    @Override
    public List<String> involvedGeneratorKeys() {
        return this.generatorKeys;
    }

    private int getSlice(Coordinate pos) {
        int currentSlice = Mth.floor((pos.angle() + Math.PI) / this.sliceArcLength);
        return Math.min(currentSlice, lastSliceIndex); // Because there is a straight 1-thick line that is out of bounds otherwise
    }

    @Override
    public String getShade(Coordinate pos) {
        return this.generatorKeys.get(this.getSlice(pos));
    }
}
