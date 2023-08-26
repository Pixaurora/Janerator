package net.pixaurora.janerator.graphing.settings;

import com.mojang.serialization.Codec;

import net.pixaurora.janerator.graphing.PointGrapher;

public interface GrapherSettings {
    public static Codec<GrapherSettings> CODEC = GrapherSettingsType.CODEC.dispatch("type", GrapherSettings::type, GrapherSettingsType::getAppliedCodec);

    public GrapherSettingsType type();

    public PointGrapher createGrapher();
}
