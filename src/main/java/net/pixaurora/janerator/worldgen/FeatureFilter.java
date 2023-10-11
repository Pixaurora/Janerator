package net.pixaurora.janerator.worldgen;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public record FeatureFilter(HolderSet<ConfiguredFeature<?, ?>> features) {
    public static final Codec<FeatureFilter> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ConfiguredFeature.LIST_CODEC.fieldOf("features").forGetter(FeatureFilter::features)
        ).apply(instance, FeatureFilter::new)
    );

    public static FeatureFilter defaultInstance() {
        return new FeatureFilter(HolderSet.direct(List.of()));
    }

    public boolean filtersOut(Holder<ConfiguredFeature<?, ?>> feature) {
        return this.features.contains(feature);
    }
}
