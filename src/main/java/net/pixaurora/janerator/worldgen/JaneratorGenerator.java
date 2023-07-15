package net.pixaurora.janerator.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface JaneratorGenerator {
    public default void janerator$setDimension (ResourceKey<Level> dimension) {}
}
