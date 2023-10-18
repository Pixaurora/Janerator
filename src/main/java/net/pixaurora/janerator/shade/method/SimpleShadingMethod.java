package net.pixaurora.janerator.shade.method;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.Coordinate;

public interface SimpleShadingMethod extends ShadingMethod {
    public String getShade(Coordinate pos);

    public default List<ShadeData> shadeIn(List<Coordinate> points, ChunkPos chunk) {
        Coordinate offsetAmount = new Coordinate(chunk.getBlockAt(0, 0, 0));

        List<ShadeData> shading = new ArrayList<>(points.size());
        for (Coordinate pos : points) {
            shading.add(new ShadeData(pos, this.getShade(pos.offset(offsetAmount))));
        }

        return shading;
    }
}
