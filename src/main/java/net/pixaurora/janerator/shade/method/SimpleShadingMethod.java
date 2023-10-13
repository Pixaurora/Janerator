package net.pixaurora.janerator.shade.method;

import java.util.ArrayList;
import java.util.List;

import net.pixaurora.janerator.graphing.Coordinate;

public interface SimpleShadingMethod extends ShadingMethod {
    public ShadeData getShade(Coordinate pos);

    public default List<ShadeData> shadeIn(List<Coordinate> points) {
        List<ShadeData> shading = new ArrayList<>(points.size());
        for (Coordinate pos : points) {
            shading.add(this.getShade(pos));
        }

        return shading;
    }
}
