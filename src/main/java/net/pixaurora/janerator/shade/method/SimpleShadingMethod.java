package net.pixaurora.janerator.shade.method;

import java.util.ArrayList;
import java.util.List;

import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphedChunk;

public interface SimpleShadingMethod extends ShadingMethod {
    public ShadeData getShade(Coordinate pos);

    public default List<ShadeData> shadeIn(GraphedChunk chunk) {
        List<Coordinate> shadedCoordinates = chunk.getShadedCoordinates();

        List<ShadeData> shading = new ArrayList<>(shadedCoordinates.size());
        for (Coordinate pos : shadedCoordinates) {
            shading.add(this.getShade(pos));
        }

        return shading;
    }
}
