package net.pixaurora.janerator.graphing;

import net.minecraft.core.Direction8;

public enum OutlineType {
    ADJACENT_ONLY(Direction8.WEST, Direction8.SOUTH, Direction8.NORTH, Direction8.EAST),
    INCLUDES_DIAGONALS(Direction8.values());

    private final Direction8[] neighborsToCheck;

    private OutlineType(Direction8... neighborsToCheck) {
        this.neighborsToCheck = neighborsToCheck;
    }

    public Direction8[] getNeighborsToCheck() {
        return this.neighborsToCheck;
    }
}
