package net.pixaurora.janerator.graphing.grapher;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphedChunk;

public interface ChunkGrapher {
    public static Codec<ChunkGrapher> CODEC = GrapherType.CODEC.dispatch("type", ChunkGrapher::type, GrapherType::getAppliedCodec);

    public GrapherType type();

    public GraphedChunk getChunkGraph(ChunkPos pos);

    public boolean isPointShaded(Coordinate pos);

    public default boolean isPointShaded(int x, int z) {
        return this.isPointShaded(new Coordinate(x, z));
    }

    public default boolean isPointShaded(BlockPos pos) {
        return this.isPointShaded(new Coordinate(pos));
    }
}
