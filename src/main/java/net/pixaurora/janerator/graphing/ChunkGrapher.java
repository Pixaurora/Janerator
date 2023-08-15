package net.pixaurora.janerator.graphing;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkGrapher {
    private ConfiguredGrapherSettings settings;
    private ThreadLocal<PointGrapher> localPointGrapher;

    public ChunkGrapher(ConfiguredGrapherSettings settings) {
        this.settings = settings;
        this.localPointGrapher = ThreadLocal.withInitial(() -> PointGrapher.fromConfig(this.settings));
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

    public GraphedChunk getChunkGraph(ChunkPos pos) {
        return new GraphedChunk(this, pos);
    }

    public CompletableFuture<GraphedChunk> scheduleChunkGraphing(ChunkPos pos) {
        return CompletableFuture.supplyAsync(() -> this.getChunkGraph(pos), GraphingUtils.threadPool);
    }
}
