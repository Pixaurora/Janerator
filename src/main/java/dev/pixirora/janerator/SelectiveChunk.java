package dev.pixirora.janerator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

public class SelectiveChunk extends WrappedProtoChunk {
    private PlacementVerifier placementVerifier;

	public SelectiveChunk(ProtoChunk chunk, PlacementVerifier placementVerifier) {
		super(chunk);
		this.placementVerifier = placementVerifier;
	}

    @Override
    public boolean allowWrites(BlockPos pos) {
        return this.placementVerifier.isWanted(pos.getX(), pos.getZ());
    }

    @Override
    public WrappedChunkSection wrapSection(LevelChunkSection section) {
        return new WrappedChunkSection(section, this.placementVerifier);
    }
}
