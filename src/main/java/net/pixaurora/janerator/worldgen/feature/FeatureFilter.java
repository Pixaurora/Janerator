package net.pixaurora.janerator.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class FeatureFilter {
    public static final Codec<FeatureFilter> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ConfiguredFeature.LIST_CODEC.fieldOf("features").forGetter(filter -> filter.individualFeatures),
            HandpickedFeatureCategory.CODEC.listOf().fieldOf("preset_categories").forGetter(filter -> filter.presetCategories)
        ).apply(instance, FeatureFilter::new)
    );

    private final HolderSet<ConfiguredFeature<?, ?>> individualFeatures;
    private final List<HandpickedFeatureCategory> presetCategories;

    private final List<ResourceKey<ConfiguredFeature<?, ?>>> categorizedKeys;

    public FeatureFilter(HolderSet<ConfiguredFeature<?, ?>> individualFeatures, List<HandpickedFeatureCategory> handpickedCategories) {
        this.individualFeatures = individualFeatures;
        this.presetCategories = handpickedCategories;

        this.categorizedKeys = handpickedCategories.stream().flatMap(features -> features.includedFeatures().stream()).toList();
    }

    public static FeatureFilter defaultInstance() {
        return new FeatureFilter(HolderSet.direct(List.of()), List.of());
    }

    public boolean filtersOut(Holder<ConfiguredFeature<?, ?>> feature) {
        return this.individualFeatures.contains(feature)
                || feature.unwrapKey().map(featureKey -> this.categorizedKeys.contains(featureKey)).orElse(false);
    }
}
