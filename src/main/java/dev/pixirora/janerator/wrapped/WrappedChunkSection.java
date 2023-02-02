package dev.pixirora.janerator.wrapped;

import dev.pixirora.janerator.mixin.LevelChunkSectionAccessor;
import dev.pixirora.janerator.worldgen.PlacementVerifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class WrappedChunkSection extends LevelChunkSection {
    private PlacementVerifier placementVerifier;
    private LevelChunkSection wrapped;

    public WrappedChunkSection(LevelChunkSection section, PlacementVerifier placementVerifier) {
        super(
            section.bottomBlockY() >> 4,
            section.getStates(),
            section.getBiomes()
        );

        this.placementVerifier = placementVerifier;
        this.wrapped = section;
    }

    public void read(FriendlyByteBuf buf) {
        super.read(buf);

        LevelChunkSectionAccessor wrappedAccessor = (LevelChunkSectionAccessor) this.wrapped;
        wrappedAccessor.setBiomes(this.getBiomes());
        wrappedAccessor.setNonEmptyBlockCount(buf.readShort());
    }

    public void recalcBlockCounts() {
        super.recalcBlockCounts();
        if (wrapped != null) {
            this.wrapped.recalcBlockCounts();
        }
    }

    public void fillBiomesFromNoise(BiomeResolver biomeSupplier, Climate.Sampler sampler, int x, int z) {
        super.fillBiomesFromNoise(biomeSupplier, sampler, x, z);
        ((LevelChunkSectionAccessor) this.wrapped).setBiomes(this.getBiomes());
    }

    public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
		if (placementVerifier.isWanted(x, z)) {
            return super.setBlockState(x, y, z, state, lock);
        }

        return getBlockState(x, y, z);
	}
}
