package net.pixaurora.janerator.graphing.grapher;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.pixaurora.janerator.config.GraphingConfigException;

public class GrapherType {
    public static Codec<GrapherType> CODEC = Codec.STRING.xmap(GrapherType::fromName, GrapherType::getName);

    public static GrapherType CUSTOM = new GrapherType("custom", CustomGrapher.CODEC);
    public static List<GrapherType> TYPES = List.of(
        CUSTOM
    );

    private final String name;
    private final Codec<? extends ChunkGrapher> appliedCodec;

    public GrapherType(String name, Codec<? extends ChunkGrapher> codec) {
        this.name = name;
        this.appliedCodec = codec;
    }

    public static GrapherType fromName(String name) {
        Optional<GrapherType> foundSettings = TYPES.stream()
            .filter(type -> type.name.equals(name))
            .findFirst();

        if (foundSettings.isEmpty()) {
            throw new GraphingConfigException(String.format("Unknown Grapher type `%s` specified.", name));
        } else {
            return foundSettings.get();
        }
    }

    public String getName() {
        return name;
    }

    public Codec<? extends ChunkGrapher> getAppliedCodec() {
        return appliedCodec;
    }
}
