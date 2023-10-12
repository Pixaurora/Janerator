package net.pixaurora.janerator.graphing.grapher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.config.SpecifiesType;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphedChunk;

public interface ChunkGrapher extends SpecifiesType<ChunkGrapher> {
    public static final List<SerialType<ChunkGrapher>> TYPES = new ArrayList<>(
        List.of(
            FunctionGrapher.TYPE,
            GrowingTileGrapher.TYPE
        )
    );
    public static Codec<ChunkGrapher> CODEC = new SerialType.Group<>("Chunk grapher", TYPES).dispatchCodec();

    public boolean isPointShaded(Coordinate pos);

    public GraphedChunk getChunkGraph(ChunkPos pos);

    public default boolean isPointShaded(int x, int z) {
        return this.isPointShaded(new Coordinate(x, z));
    }

    public default boolean isPointShaded(BlockPos pos) {
        return this.isPointShaded(new Coordinate(pos));
    }
}
