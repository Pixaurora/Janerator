package net.pixaurora.janerator.graphing;

import java.util.stream.Stream;

public record Coordinate(int x, int z, int scale) {
    public Coordinate(int x, int z) {
        this(x, z, 16);
    }

    private static int mod(int value, int divisor) {
        return value - divisor * Math.floorDiv(value, divisor);
    }

    public int toListIndex() {
        return scale * mod(this.x, this.scale) + mod(this.z, this.scale);
    }

    public static Coordinate fromListIndex(int index, int scale) {
        return new Coordinate(Math.floorDiv(index, scale), mod(index, scale), scale);
    }

    public static Coordinate fromListIndex(int index) {
        return fromListIndex(index, 16);
    }

    public Stream<Coordinate> getNeighbors() {
        return Stream.of(
            new Coordinate(this.x + 1, this.z, this.scale),
            new Coordinate(this.x - 1, this. z, this.scale),
            new Coordinate(this.x, this.z + 1, this.scale),
            new Coordinate(this.x, this.z - 1, this.scale)
        ).filter(coord -> 0 <= coord.x && coord.x < this.scale && 0 <= coord.z && coord.z < this.scale);
    }
}
