package net.pixaurora.janerator.graphing.grapher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.GraphingUtils;

public abstract class ChunkGrapher {
    public static Codec<ChunkGrapher> CODEC = GrapherType.CODEC.dispatch("type", ChunkGrapher::type, GrapherType::getAppliedCodec);

    private LoadingCache<ChunkPos, GraphedChunk> cache;

    public ChunkGrapher() {
        this.cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(1024)
            .build(CacheLoader.from(this::graphChunk));
    }

    public abstract boolean isPointShaded(int x, int z);

    public abstract GrapherType type();

    public boolean isPointShaded(Coordinate coord) {
        return this.isPointShaded(coord.x(), coord.z());
    }

    public boolean isPointShaded(BlockPos pos) {
        return this.isPointShaded(pos.getX(), pos.getZ());
    }

    private GraphedChunk graphChunk(ChunkPos pos) {
        return new GraphedChunk(this, pos);
    }

    public GraphedChunk getChunkGraph(ChunkPos pos) {
        try {
            return this.cache.get(pos);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<GraphedChunk> scheduleChunkGraphing(ChunkPos pos) {
        return CompletableFuture.supplyAsync(() -> this.getChunkGraph(pos), GraphingUtils.threadPool);
    }
}
