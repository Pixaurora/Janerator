package net.pixaurora.janerator.graphing.grapher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.graphing.grapher.tile.TileData;

public class GrowingTileGrapher extends CustomGrapher {
    public static final Codec<GrowingTileGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(GrowingTileGrapher::getTileDefinition),
            GraphFunctionDefinition.UNIVARIATE_CODEC.fieldOf("tileSize(v)").forGetter(GrowingTileGrapher::getGraphDefinition)
        ).apply(instance, GrowingTileGrapher::new)
    );

    public static final int MAX_VALUE = (int) Math.pow(2, 25);

    private GraphFunctionDefinition tileDefinition;
    private List<Integer> tileSums;

    public GrowingTileGrapher(GraphFunctionDefinition graphDefinition, GraphFunctionDefinition tileGrowthDefinition) {
        super(graphDefinition);
        this.tileDefinition = tileGrowthDefinition;

        this.tileSums = new ArrayList<>();

        GraphFunction tileFunction = GraphFunction.fromDefinition(tileGrowthDefinition);

        int tileSum = 0;
        for (int tilePos = 0; tilePos < MAX_VALUE; tilePos++) {
            int evaluatedSize = (int) Math.floor(tileFunction.evaluate(tilePos));
            int legalSize = Math.max(1, Math.abs(evaluatedSize));

            tileSum += legalSize;
            tileSums.add(tileSum);

            if (tileSum > MAX_VALUE) {
                break;
            }
        }
    }

    public GraphFunctionDefinition getTileDefinition() {
        return this.tileDefinition;
    }

    public TileData convertToTile(int realPosition) {
        int sign = (int) Math.signum(realPosition);
        realPosition = Math.abs(realPosition);

        int lastStepValue = 0;

        for (int tilePos = 0; tilePos < this.tileSums.size(); tilePos++) {
            int stepValue = this.tileSums.get(tilePos);
            if (realPosition < stepValue) {
                return new TileData(sign * tilePos, stepValue, lastStepValue);
            }

            lastStepValue = stepValue;
        }

        throw new RuntimeException(String.format("Value %d is above the limit of stored values %d!", realPosition, MAX_VALUE));
    }

    @Override
    public boolean isPointShaded(int x, int z) {
        TileData tileX = this.convertToTile(x);
        TileData tileZ = this.convertToTile(z);

        return this.graphFunction.get().evaluate(tileX.pos(), tileZ.pos()) == 1.0;
    }

    @Override
    public GrapherType type() {
        return GrapherType.GROWING_TILES;
    }
}
