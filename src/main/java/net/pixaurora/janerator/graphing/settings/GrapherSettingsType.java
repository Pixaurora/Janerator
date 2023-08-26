package net.pixaurora.janerator.graphing.settings;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.pixaurora.janerator.config.GraphingConfigException;

public class GrapherSettingsType {
    public static Codec<GrapherSettingsType> CODEC = Codec.STRING.xmap(GrapherSettingsType::fromName, GrapherSettingsType::getName);

    public static GrapherSettingsType CUSTOM = new GrapherSettingsType("custom", CustomGrapherSettings.CODEC);
    public static List<GrapherSettingsType> settingTypes = List.of(
        CUSTOM
    );

    private final String name;
    private final Codec<? extends GrapherSettings> appliedCodec;

    public GrapherSettingsType(String name, Codec<? extends GrapherSettings> codec) {
        this.name = name;
        this.appliedCodec = codec;
    }

    public static GrapherSettingsType fromName(String name) {
        Optional<GrapherSettingsType> foundSettings = settingTypes.stream()
            .filter(type -> type.name == name)
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

    public Codec<? extends GrapherSettings> getAppliedCodec() {
        return appliedCodec;
    }
}
