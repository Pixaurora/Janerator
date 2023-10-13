package net.pixaurora.janerator.shade.method;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Coordinate;

public record ShadeData(Coordinate location, ChunkGenerator generator) {
    public int index() {
        return this.location.toListIndex();
    }
}
