package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2i;

public record Coordinate(int x, int z) {
    private static final Vector2i[] NEIGHBOR_OFFSETS = new Vector2i[]{
        new Vector2i(1, 0),
        new Vector2i(1, 1),
        new Vector2i(0, 1),
        new Vector2i(-1, 1),
        new Vector2i(-1, 0),
        new Vector2i(-1, -1),
        new Vector2i(0, -1),
        new Vector2i(1, -1)
    };

    public int toListIndex(int chunkLength) {
        return chunkLength * GraphingUtils.mod(this.x, chunkLength) + GraphingUtils.mod(this.z, chunkLength);
    }

    public int toListIndex() {
        return this.toListIndex(16);
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

    public Coordinate offsetBy(Vector2i delta) {
        return new Coordinate(this.x + delta.x, this.z + delta.y);
    }

    public List<Coordinate> getNeighbors() {
        List<Coordinate> neighbors = new ArrayList<>(NEIGHBOR_OFFSETS.length);

        for (Vector2i neighborOffset : NEIGHBOR_OFFSETS) {
            neighbors.add(this.offsetBy(neighborOffset));
        }

        return neighbors;
    }
}
