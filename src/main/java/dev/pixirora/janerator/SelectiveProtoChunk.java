package dev.pixirora.janerator;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.pixirora.janerator.mixin.ChunkAccessAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.ticks.ProtoChunkTicks;

public class SelectiveProtoChunk extends ProtoChunk {
    List<List<Integer>> placedPositions;

    public SelectiveProtoChunk(ProtoChunk chunk, List<List<Integer>>placedPositions) {
        super(
            chunk.getPos(),
            chunk.getUpgradeData(),
            chunk.getSections(),
            new ProtoChunkTicks<>(),
            new ProtoChunkTicks<>(),
            ((ChunkAccessAccessor)((ChunkAccess) chunk)).getLevelHeight(),
            Janerator.getRegistry(Registries.BIOME),
            chunk.getBlendingData()
	    );

        this.placedPositions = placedPositions;
    }

    public boolean inSelection(BlockPos pos) {
        return this.placedPositions.contains(Arrays.asList(pos.getX(), pos.getZ()));
    }

    public static ChunkAccess getMeIfNecessary(ChunkAccess chunk, List<List<Integer>> placedPositions) {
        if (chunk instanceof ProtoChunk) {
            return new SelectiveProtoChunk((ProtoChunk) chunk, placedPositions);
        }
       
        return chunk;
    }

	@Nullable
    @Override
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        if (this.inSelection(pos)) {
            return super.setBlockState(pos, state, moved);
        }

        return null;
    }
}
