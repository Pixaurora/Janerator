package net.pixaurora.janerator.graphing.grapher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction8;
import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.OutlineType;

public class GrowingTileGrapher implements ChunkGrapher {
    public static final Codec<GrowingTileGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(GrowingTileGrapher::getTileDefinition),
            GraphFunctionDefinition.UNIVARIATE_CODEC.fieldOf("tile_size(v)").forGetter(GrowingTileGrapher::getGraphDefinition),
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("corner(x, z)").forGetter(GrowingTileGrapher::getCornerDefinition)
        ).apply(instance, GrowingTileGrapher::new)
    );
    public static final SerialType<ChunkGrapher> TYPE = new SerialType<>("growing_tiles", CODEC);

    public static final int MAX_VALUE = 33_554_432; // 2^25, should be substantially larger than the max world size in Minecraft

    private FunctionGrapher stretchedGrapher;

    private GraphFunctionDefinition tileDefinition;
    private List<Integer> tileSums;

    private GraphFunctionDefinition cornerDefinition;
    private ThreadLocal<GraphFunction> cornerFunction;

    public GrowingTileGrapher(GraphFunctionDefinition graphDefinition, GraphFunctionDefinition tileGrowthDefinition, GraphFunctionDefinition cornerDefinition) {
        this.stretchedGrapher = new FunctionGrapher(graphDefinition);
        this.tileDefinition = tileGrowthDefinition;

        this.cornerDefinition = cornerDefinition;
        this.cornerFunction = ThreadLocal.withInitial(() -> GraphFunction.fromDefinition(cornerDefinition));

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

    public SerialType<ChunkGrapher> type() {
        return TYPE;
    }

    public GraphFunctionDefinition getGraphDefinition() {
        return this.stretchedGrapher.getGraphDefinition();
    }

    public GraphFunctionDefinition getTileDefinition() {
        return this.tileDefinition;
    }

    public GraphFunctionDefinition getCornerDefinition() {
        return this.cornerDefinition;
    }

    public static record Tile(int pos, double start, double end) {
        public double size() {
            return this.end - this.start;
        }
    }

    public Tile convertToTile(int realPos) {
        int sign = (int) Math.signum(realPos);
        realPos = Math.abs(realPos);

        int tileStart = 0;

        for (int tilePos = 0; tilePos < this.tileSums.size(); tilePos++) {
            int tileEnd = this.tileSums.get(tilePos);

            if (realPos < tileEnd) {
                return new Tile(sign * tilePos, tileStart, tileEnd);
            }

            tileStart = tileEnd;
        }

        throw new RuntimeException(String.format("Value %d is above the limit of stored values %d!", realPos, MAX_VALUE));
    }

    private boolean isTileShaded(Coordinate tilePos) {
        GraphedChunk graph = this.stretchedGrapher.getChunkGraph(tilePos.toChunkPos());

        return graph.isShaded(tilePos.makeLegal());
    }

    public static double positionIn(Tile tile, double realPos, double minSize) {
        realPos = Math.abs(realPos);
        double sizeDiff = tile.size() - minSize;

        return (realPos - tile.start() - sizeDiff) / minSize;
    }

    public boolean isCornerShaded(Coordinate realPos, Tile xTile, Tile zTile, Coordinate offset) {
        double minSize = Math.min(xTile.size(), zTile.size());

        double xPos = positionIn(xTile, realPos.x(), minSize);
        double zPos = positionIn(zTile, realPos.z(), minSize);

        if (xPos < 0 || zPos < 0) {
            // Corners are only drawn as squares, if the corner tile is rectangular then part of it will just be the default shade.
            return true;
        }

        double cornerX = offset.x() * xPos;
        double cornerZ = offset.z() * zPos;

        return this.cornerFunction.get().evaluate(cornerX, cornerZ) == 1.0;
    }

    @Override
    public boolean isPointShaded(Coordinate pos) {
        Tile xTile = this.convertToTile(pos.x());
        Tile zTile = this.convertToTile(pos.z());
        Coordinate tilePos = new Coordinate(xTile.pos(), zTile.pos());

        boolean shade = isTileShaded(tilePos);

        Coordinate noOffset = new Coordinate(0, 0);

        Coordinate offset = noOffset;
        int nudges = 0;
        for (Direction8 direction : OutlineType.INCLUDES_DIAGONALS.getNeighborsToCheck()) {
            Coordinate neighborPos = tilePos.offsetIn(direction);

            if (shade != this.isTileShaded(neighborPos)) {
                offset = offset.offsetIn(direction);
                nudges += 1;
            }
        }

        if (nudges == 2 && !offset.equals(noOffset)) {
            boolean cornerShade = this.isCornerShaded(pos, xTile, zTile, offset);
            shade = shade ? cornerShade : ! cornerShade;
        }

        return shade;
    }

    @Override
    public GraphedChunk getChunkGraph(ChunkPos chunk) {
        return new GraphedChunk(this, chunk);
    }
}
