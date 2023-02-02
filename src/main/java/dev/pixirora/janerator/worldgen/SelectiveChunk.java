package dev.pixirora.janerator.worldgen;

import dev.pixirora.janerator.wrapped.WrappedProtoChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

public class SelectiveChunk extends WrappedProtoChunk {
    private PlacementVerifier verifier;

	public SelectiveChunk(ProtoChunk chunk, PlacementVerifier placementVerifier) {
		super(chunk);
		this.verifier = placementVerifier;
        this.wrapSections();
	}

    @Override
    public boolean allowWrites(BlockPos pos) {
        return this.verifier.isWanted(pos.getX(), pos.getZ());
    }

    @Override
    public void wrapSection(LevelChunkSection section) {
        section.janerator$setVerifier(verifier);
    }
}
