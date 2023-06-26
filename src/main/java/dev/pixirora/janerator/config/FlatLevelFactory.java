package dev.pixirora.janerator.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import dev.pixirora.janerator.Janerator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelFactory {
    public static FlatLevelGeneratorSettings createFromString(
        HolderGetter<Block> blockProvider,
		HolderGetter<Biome> biomeProvider,
		HolderGetter<StructureSet> structureSetProvider,
		HolderGetter<PlacedFeature> placedFeatureProvider,
		String preset,
		FlatLevelGeneratorSettings generatorConfig
    ) {
        Iterator<String> iterator = Splitter.on(';').split(preset).iterator();
		if (!iterator.hasNext()) {
			return FlatLevelGeneratorSettings.getDefault(biomeProvider, structureSetProvider, placedFeatureProvider);
		} else {
			List<FlatLayerInfo> list = FlatLevelFactory.getLayersInfoFromString(blockProvider, (String)iterator.next());
			if (list.isEmpty()) {
				return FlatLevelGeneratorSettings.getDefault(biomeProvider, structureSetProvider, placedFeatureProvider);
			} else {
				Holder.Reference<Biome> reference = biomeProvider.getOrThrow(Biomes.PLAINS);
				Holder<Biome> holder = reference;
				if (iterator.hasNext()) {
					String string = (String)iterator.next();
					holder = (Holder<Biome>)Optional.ofNullable(ResourceLocation.tryParse(string))
						.map(resourceLocation -> ResourceKey.create(Registries.BIOME, resourceLocation))
						.flatMap(biomeProvider::get)
						.orElseGet(() -> {
							Janerator.LOGGER.warn("Invalid biome: {}", string);
							return reference;
						});
				}

				return generatorConfig.withBiomeAndLayers(list, generatorConfig.structureOverrides(), holder);
			}
		}
    }

    private static List<FlatLayerInfo> getLayersInfoFromString(HolderGetter<Block> blockProvider, String layers) {
		List<FlatLayerInfo> list = Lists.<FlatLayerInfo>newArrayList();
		String[] strings = layers.split(",");
		int i = 0;

		for(String string : strings) {
			FlatLayerInfo flatLayerInfo = FlatLevelFactory.getLayerInfoFromString(blockProvider, string, i);
			if (flatLayerInfo == null) {
				return Collections.emptyList();
			}

			list.add(flatLayerInfo);
			i += flatLayerInfo.getHeight();
		}

		return list;
	}

    @Nullable
	private static FlatLayerInfo getLayerInfoFromString(HolderGetter<Block> blockProvider, String layer, int layerStartHeight) {
		List<String> list = Splitter.on('*').limit(2).splitToList(layer);
		int i;
		String string;
		if (list.size() == 2) {
			string = (String)list.get(1);

			try {
				i = Math.max(Integer.parseInt((String)list.get(0)), 0);
			} catch (NumberFormatException var11) {
				Janerator.LOGGER.error("Error while parsing flat world string", var11);
				return null;
			}
		} else {
			string = (String)list.get(0);
			i = 1;
		}

		int j = Math.min(layerStartHeight + i, DimensionType.Y_SIZE);
		int k = j - layerStartHeight;

		Optional<Holder.Reference<Block>> optional;
		try {
			optional = blockProvider.get(ResourceKey.create(Registries.BLOCK, new ResourceLocation(string)));
		} catch (Exception var10) {
			Janerator.LOGGER.error("Error while parsing flat world string", var10);
			return null;
		}

		if (optional.isEmpty()) {
			Janerator.LOGGER.error("Error while parsing flat world string => Unknown block, {}", string);
			return null;
		} else {
			return new FlatLayerInfo(k, (Block)((Holder.Reference<Block>)optional.get()).value());
		}
	}

}
