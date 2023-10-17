package net.pixaurora.janerator.shade.method;

import net.pixaurora.janerator.graphing.Coordinate;

public record ShadeData(Coordinate location, String generatorKey) {
    public int index() {
        return this.location.toListIndex();
    }
}
