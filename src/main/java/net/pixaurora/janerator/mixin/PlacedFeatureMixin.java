package net.pixaurora.janerator.mixin;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.pixaurora.janerator.RegistryCache;
import net.pixaurora.janerator.graphing.Graphing;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {
    @Shadow
    private Holder<ConfiguredFeature<?, ?>> feature;

    @Redirect(
        method = "placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"
        )
    )
    public void janerator$decideIfPlace(Stream<BlockPos> posStream, Consumer<BlockPos> function) {
        if (RegistryCache.INSTANCE.getRemovedFeatures().contains(this.feature.value())) {
            List<BlockPos> positions = posStream.toList();

            Map<BlockPos, Boolean> positionShading = positions
                .stream()
                .distinct()
                .collect(Collectors.toMap(pos -> pos, pos -> Graphing.scheduleGraphing(pos)))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> Graphing.completeGraphing(entry.getValue())));

            posStream = positions
                .stream()
                .filter(pos -> ! positionShading.get(pos));
        }

        posStream.forEach(function);
    }
}
