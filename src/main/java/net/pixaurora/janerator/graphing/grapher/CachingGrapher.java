package net.pixaurora.janerator.graphing.grapher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.GraphedChunk;

public abstract class CachingGrapher implements ChunkGrapher {
    private LoadingCache<ChunkPos, GraphedChunk> cache;

    public CachingGrapher() {
        this.cache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .maximumSize(1024)
            .build(CacheLoader.from(chunk -> new GraphedChunk(this, chunk)));
    }

    @Override
    public GraphedChunk getChunkGraph(ChunkPos chunk) {
        try {
            return this.cache.get(chunk);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
