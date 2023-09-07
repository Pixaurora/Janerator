package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2i;

public record Coordinate(int x, int z, int scale) {
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

    public Coordinate(int x, int z) {
        this(x, z, 16);
    }

    public int toListIndex() {
        return this.scale * GraphingUtils.mod(this.x, this.scale) + GraphingUtils.mod(this.z, this.scale);
    }

    public static Coordinate fromListIndex(int index, int scale) {
        return new Coordinate(Math.floorDiv(index, scale), GraphingUtils.mod(index, scale), scale);
    }

    public static Coordinate fromListIndex(int index) {
        return fromListIndex(index, 16);
    }

    public boolean isLegal() {
        return 0 <= this.x && this.x < this.scale && 0 <= this.z && this.z < this.scale;
    }

    public Coordinate makeLegal() {
        return new Coordinate(GraphingUtils.mod(this.x, this.scale), GraphingUtils.mod(this.z, this.scale), this.scale);
    }

    public Coordinate offsetBy(Vector2i delta) {
        return new Coordinate(this.x + delta.x, this.z + delta.y, this.scale);
    }

    public List<Coordinate> getNeighbors() {
        List<Coordinate> neighbors = new ArrayList<>(NEIGHBOR_OFFSETS.length);

        for (Vector2i neighborOffset : NEIGHBOR_OFFSETS) {
            neighbors.add(this.offsetBy(neighborOffset));
        }

        return neighbors;
    }
}
