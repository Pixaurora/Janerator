package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction8;
import net.minecraft.world.level.ChunkPos;

public record Coordinate(int x, int z) {
    public Coordinate(BlockPos pos) {
        this(pos.getX(), pos.getZ());
    }

    public int toListIndex(int chunkLength) {
        return chunkLength * GraphingUtils.mod(this.x, chunkLength) + GraphingUtils.mod(this.z, chunkLength);
    }

    public int toListIndex() {
        return this.toListIndex(16);
    }

    public ChunkPos toChunkPos(int chunkLength) {
        return new ChunkPos(Math.floorDiv(this.x, chunkLength), Math.floorDiv(this.z, chunkLength));
    }

    public ChunkPos toChunkPos() {
        return this.toChunkPos(16);
    }

    public static Coordinate fromListIndex(int index, int chunkLength) {
        return new Coordinate(Math.floorDiv(index, chunkLength), GraphingUtils.mod(index, chunkLength));
    }

    public static Coordinate fromListIndex(int index) {
        return fromListIndex(index, 16);
    }

    public boolean isLegal(int chunkLength) {
        return 0 <= this.x && this.x < chunkLength && 0 <= this.z && this.z < chunkLength;
    }

    public boolean isLegal() {
        return this.isLegal(16);
    }

    public Coordinate makeLegal(int chunkLength) {
        return new Coordinate(GraphingUtils.mod(this.x, chunkLength), GraphingUtils.mod(this.z, chunkLength));
    }

    public Coordinate makeLegal() {
        return this.makeLegal(16);
    }

    public Coordinate offset(int x, int z) {
        return new Coordinate(this.x + x, this.z + z);
    }

    public Coordinate offsetIn(Direction8 direction) {
        return this.offset(direction.getStepX(), direction.getStepZ());
    }

    public List<Coordinate> getNeighbors() {
        return this.getNeighbors(Direction8.values());
    }

    public List<Coordinate> getNeighbors(Direction8... neighborDirections) {
        List<Coordinate> neighbors = new ArrayList<>(neighborDirections.length);

        for (Direction8 direction : neighborDirections) {
            neighbors.add(this.offsetIn(direction));
        }

        return neighbors;
    }
}
