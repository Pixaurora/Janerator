package dev.pixirora.janerator.mixin;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.pixirora.janerator.Janerator;
import dev.pixirora.janerator.RegistryCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

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
            posStream = posStream.filter(pos -> !Janerator.shouldOverride(pos));
        }

        posStream.forEach(function);
    }
}
