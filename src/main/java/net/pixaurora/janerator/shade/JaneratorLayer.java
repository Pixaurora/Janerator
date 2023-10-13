package net.pixaurora.janerator.shade;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;
import net.pixaurora.janerator.shade.method.ShadeData;
import net.pixaurora.janerator.shade.method.ShadingMethod;

public class JaneratorLayer {
    public static final Codec<JaneratorLayer> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ChunkGrapher.CODEC.fieldOf("grapher").forGetter(JaneratorLayer::getGrapher),
            ShadingMethod.CODEC.fieldOf("shading").forGetter(JaneratorLayer::getShading)
        ).apply(instance, JaneratorLayer::new)
    );

    private final ChunkGrapher grapher;
    private final ShadingMethod shadingMethod;

    public JaneratorLayer(ChunkGrapher grapher, ShadingMethod shadingMethod) {
        this.grapher = grapher;
        this.shadingMethod = shadingMethod;
    }

    public ChunkGrapher getGrapher() {
        return this.grapher;
    }

    public ShadingMethod getShading() {
        return shadingMethod;
    }

    public List<ShadeData> shadesIn(ChunkPos chunk) {
        return this.shadingMethod.shadeIn(this.grapher.getChunkGraph(chunk));
    }
}
