package net.pixaurora.janerator.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.pixaurora.janerator.worldgen.feature.HandpickedFeatureCategory;

import static net.minecraft.commands.Commands.literal;

import java.util.List;
import java.util.stream.Stream;

public class HandpickedFeatureCoverageCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
            literal("report-feature-coverage")
                .requires(source -> source.hasPermission(2))
                .requires(CommandSourceStack::isPlayer)
                .executes(commandContext -> printBlocks(commandContext.getSource()))
        );
    }

    public static int printBlocks(CommandSourceStack source) {
        RegistryAccess registry = source.registryAccess();

        List<ResourceKey<ConfiguredFeature<?, ?>>> coveredFeatures = Stream.of(HandpickedFeatureCategory.values())
            .flatMap(category -> category.includedFeatures().stream())
            .toList();

        List<String> missingFeatures = registry.lookupOrThrow(Registries.CONFIGURED_FEATURE).listElementIds()
            .filter(feature -> !coveredFeatures.contains(feature))
            .map(ResourceKey::location)
            .map(ResourceLocation::getPath)
            .toList();

        int amountMissing = missingFeatures.size();

        source.sendSuccess(() -> Component.literal(String.format("%d features that were found missing from handpicked categories!", amountMissing)), false);

        if (amountMissing > 0) {
            source.sendSuccess(() -> Component.literal(String.join(", ", missingFeatures)), false);
        }

        return 1;
    }
}
