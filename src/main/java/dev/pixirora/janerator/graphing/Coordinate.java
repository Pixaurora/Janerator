package dev.pixirora.janerator.graphing;

public record Coordinate(int x, int z, int scale) {
    public Coordinate(int x, int z) {
        this(x, z, 16);
    }

    private static int mod(int value, int divisor) {
        return value - divisor * Math.floorDiv(value, divisor);
    }

    public int toListCoordinate() {
        return scale * mod(this.x, this.scale) + mod(this.z, this.scale);
    }
}
