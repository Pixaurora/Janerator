package net.pixaurora.janerator.graphing.grapher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.grapher.tile.TileData;

public class GrowingTileGrapher implements ChunkGrapher {
    public static final Codec<GrowingTileGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(GrowingTileGrapher::getTileDefinition),
            GraphFunctionDefinition.UNIVARIATE_CODEC.fieldOf("tile_size(v)").forGetter(GrowingTileGrapher::getGraphDefinition)
        ).apply(instance, GrowingTileGrapher::new)
    );

    public static final int MAX_VALUE = (int) Math.pow(2, 25);

    private CustomGrapher stretchedGrapher;

    private GraphFunctionDefinition tileDefinition;
    private List<Integer> tileSums;

    public GrowingTileGrapher(GraphFunctionDefinition graphDefinition, GraphFunctionDefinition tileGrowthDefinition) {
        this.stretchedGrapher = new CustomGrapher(graphDefinition);
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

    public GraphFunctionDefinition getGraphDefinition() {
        return this.stretchedGrapher.getGraphDefinition();
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

    private boolean isTileShaded(Coordinate tilePos) {
        GraphedChunk graph = this.stretchedGrapher.getChunkGraph(new ChunkPos(new BlockPos(tilePos.x(), 0, tilePos.z())));

        return graph.isShaded(tilePos.makeLegal());
    }

    @Override
    public boolean isPointShaded(Coordinate pos) {
        TileData tileX = this.convertToTile(pos.x());
        TileData tileZ = this.convertToTile(pos.z());

        Coordinate tilePos = new Coordinate(tileX.pos(), tileZ.pos());

        return this.isTileShaded(tilePos);
    }

    @Override
    public GraphedChunk getChunkGraph(ChunkPos chunk) {
        return new GraphedChunk(this, chunk);
    }

    @Override
    public GrapherType type() {
        return GrapherType.GROWING_TILES;
    }
}
