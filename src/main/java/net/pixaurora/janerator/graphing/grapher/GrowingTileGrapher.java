package net.pixaurora.janerator.graphing.grapher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;

public class GrowingTileGrapher extends CustomGrapher {
    public static final Codec<GrowingTileGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(GrowingTileGrapher::getTileDefinition),
            GraphFunctionDefinition.UNIVARIATE_CODEC.fieldOf("tileSize(v)").forGetter(GrowingTileGrapher::getGraphDefinition)
        ).apply(instance, GrowingTileGrapher::new)
    );

    private GraphFunctionDefinition tileDefinition;
    private ThreadLocal<GraphFunction> tileFunction;

    public GrowingTileGrapher(GraphFunctionDefinition graphDefinition, GraphFunctionDefinition tileGrowthDefinition) {
        super(graphDefinition);

        this.tileDefinition = tileGrowthDefinition;
        this.tileFunction = ThreadLocal.withInitial(() -> GraphFunction.fromDefinition(this.tileDefinition));
    }

    public int inverseOfTileGrowth(int value) {
        int tileSizeSum = 0;
        int guess = 0;

        while (tileSizeSum < value) {
            tileSizeSum += Math.max(1, Math.abs(this.tileFunction.get().evaluate(guess)));
            guess++;
        }

        return guess - 1;
    }

    public int convertToTile(int value) {
        return (int) Math.signum(value) * this.inverseOfTileGrowth(Math.abs(value));
    }

    @Override
    public boolean isPointShaded(int x, int z) {
        return super.isPointShaded(this.convertToTile(x), this.convertToTile(z));
    }

    @Override
    public GrapherType type() {
        return GrapherType.GROWING_TILES;
    }

    public GraphFunctionDefinition getTileDefinition() {
        return this.tileDefinition;
    }
}
