package net.pixaurora.janerator.shade.method;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.config.SpecifiesType;
import net.pixaurora.janerator.graphing.GraphedChunk;

public interface ShadingMethod extends SpecifiesType<ShadingMethod> {
    public static final List<SerialType<ShadingMethod>> TYPES = new ArrayList<>(List.of(NormalShading.TYPE));
    public static final Codec<ShadingMethod> CODEC = new SerialType.Group<>("Shading method", TYPES).dispatchCodec();

    public List<ShadeData> shadeIn(GraphedChunk chunk);

    public List<ChunkGenerator> involvedGenerators();

    public default int generatorCount() {
        return this.involvedGenerators().size();
    }
}
