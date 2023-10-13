package net.pixaurora.janerator.shade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;
import net.pixaurora.janerator.shade.method.ShadeData;
import net.pixaurora.janerator.shade.method.ShadingMethod;

public class JaneratorLayer {
    public static final Codec<JaneratorLayer> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ChunkGrapher.CODEC.fieldOf("grapher").forGetter(JaneratorLayer::getGrapher),
            ShadingMethod.CODEC.fieldOf("shading").forGetter(JaneratorLayer::getShading),
            ShadingMethod.CODEC.optionalFieldOf("outline_shading").forGetter(JaneratorLayer::getOutlineShading)
        ).apply(instance, JaneratorLayer::new)
    );

    private final ChunkGrapher grapher;

    private final ShadingMethod shadingMethod;
    private final Optional<ShadingMethod> outlineShading;

    public JaneratorLayer(ChunkGrapher grapher, ShadingMethod shadingMethod, Optional<ShadingMethod> outlineShading) {
        this.grapher = grapher;

        this.shadingMethod = shadingMethod;
        this.outlineShading = outlineShading;
    }

    public ChunkGrapher getGrapher() {
        return this.grapher;
    }

    public ShadingMethod getShading() {
        return shadingMethod;
    }

    public Optional<ShadingMethod> getOutlineShading() {
        return outlineShading;
    }

    public List<ShadeData> shadesIn(ChunkPos chunk) {
        GraphedChunk graph = this.grapher.getChunkGraph(chunk);

        List<ShadeData> shades = new ArrayList<>(this.shadingMethod.shadeIn(graph.getShaded()));
        this.outlineShading.ifPresent(outline -> shades.addAll(outline.shadeIn(graph.getOutlines())));

        return shades;
    }
}
