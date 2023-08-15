package net.pixaurora.janerator.graphing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkGrapher extends CacheLoader<ChunkPos, GraphedChunk> {
    private ConfiguredGrapherSettings settings;
    private ThreadLocal<PointGrapher> localPointGrapher;

    private LoadingCache<ChunkPos, GraphedChunk> cache;

    public ChunkGrapher(ConfiguredGrapherSettings settings) {
        this.settings = settings;
        this.localPointGrapher = ThreadLocal.withInitial(() -> PointGrapher.fromConfig(this.settings));

        this.cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(1024)
            .build(this);
    }

    public ConfiguredGrapherSettings getSettings() {
        return this.settings;
    }

    public boolean isPointShaded(int x, int z) {
        return this.localPointGrapher.get().isShaded(x, z);
    }

    public boolean isPointShaded(BlockPos pos) {
        return this.isPointShaded(pos.getX(), pos.getZ());
    }

    public CompletableFuture<Boolean> schedulePointGraphing(int x, int z) {
        return CompletableFuture.supplyAsync(() -> this.isPointShaded(x, z), GraphingUtils.threadPool);
    }

    @Override
    public GraphedChunk load(ChunkPos pos) {
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
