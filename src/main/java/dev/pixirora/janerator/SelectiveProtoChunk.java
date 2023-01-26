package dev.pixirora.janerator;

import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import dev.pixirora.janerator.mixin.ChunkAccessAccessor;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickContainerAccess;

public class SelectiveProtoChunk extends ProtoChunk {
    private PlacementVerifier placementVerifier;
	private final ProtoChunk wrapped;

    private ChunkStatus firstStatus; 

	public SelectiveProtoChunk(ProtoChunk chunk, PlacementVerifier placementVerifier) {
		super(
			chunk.getPos(), UpgradeData.EMPTY, ((ChunkAccessAccessor) chunk).getLevelHeight(), Janerator.getRegistry(Registries.BIOME), chunk.getBlendingData()
		);
		this.wrapped = chunk;
		this.placementVerifier = placementVerifier;

        this.firstStatus = chunk.getStatus();
	}

    public boolean allowWrites(BlockPos pos) {
        return this.placementVerifier.isWanted(pos.getX(), pos.getZ());
    }

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.wrapped.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.wrapped.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.wrapped.getFluidState(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.wrapped.getMaxLightLevel();
	}

	@Override
	public LevelChunkSection getSection(int section) {
		return super.getSection(section);
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return this.allowWrites(pos) ? this.wrapped.setBlockState(pos, state, moved) : null;
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.wrapped.setBlockEntity(blockEntity);
	}

	@Override
	public void addEntity(Entity entity) {
		this.wrapped.addEntity(entity);
	}

	@Override
	public void setStatus(ChunkStatus status) {
		super.setStatus(status);
        wrapped.setStatus(status);
	}

	@Override
	public LevelChunkSection[] getSections() {
		return this.wrapped.getSections();
	}

	@Override
	public void setHeightmap(Heightmap.Types type, long[] heightmap) {
        this.wrapped.setHeightmap(type, heightmap);
	}

	private Heightmap.Types fixType(Heightmap.Types type) {
		if (type == Heightmap.Types.WORLD_SURFACE_WG) {
			return Heightmap.Types.WORLD_SURFACE;
		} else {
			return type == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : type;
		}
	}

	@Override
	public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types type) {
		return this.wrapped.getOrCreateHeightmapUnprimed(type);
	}

	@Override
	public int getHeight(Heightmap.Types type, int x, int z) {
		return this.wrapped.getHeight(this.fixType(type), x, z);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int i, int j, int k) {
		return this.wrapped.getNoiseBiome(i, j, k);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Nullable
	@Override
	public StructureStart getStartForStructure(Structure structure) {
		return this.wrapped.getStartForStructure(structure);
	}

	@Override
	public void setStartForStructure(Structure structure, StructureStart start) {
        this.wrapped.setStartForStructure(structure, start);
	}

	@Override
	public Map<Structure, StructureStart> getAllStarts() {
		return this.wrapped.getAllStarts();
	}

	@Override
	public void setAllStarts(Map<Structure, StructureStart> structureStarts) {
        this.wrapped.setAllStarts(structureStarts);
	}

	@Override
	public LongSet getReferencesForStructure(Structure structure) {
		return this.wrapped.getReferencesForStructure(structure);
	}

	@Override
	public void addReferenceForStructure(Structure structure, long reference) {
        this.wrapped.addReferenceForStructure(structure, reference);
	}

	@Override
	public Map<Structure, LongSet> getAllReferences() {
		return this.wrapped.getAllReferences();
	}

	@Override
	public void setAllReferences(Map<Structure, LongSet> structureReferences) {
        this.wrapped.setAllReferences(structureReferences);
	}

	@Override
	public void setUnsaved(boolean needsSaving) {
		this.wrapped.setUnsaved(needsSaving);
	}

	@Override
	public boolean isUnsaved() {
		return false;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.wrapped.getStatus();
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
        this.wrapped.removeBlockEntity(pos);
	}

	@Override
	public void markPosForPostprocessing(BlockPos pos) {
        this.wrapped.markPosForPostprocessing(pos);
	}

	@Override
	public void setBlockEntityNbt(CompoundTag nbt) {
        this.wrapped.setBlockEntityNbt(nbt);
	}

	@Nullable
	@Override
	public CompoundTag getBlockEntityNbt(BlockPos pos) {
		return this.wrapped.getBlockEntityNbt(pos);
	}

	@Nullable
	@Override
	public CompoundTag getBlockEntityNbtForSaving(BlockPos pos) {
		return this.wrapped.getBlockEntityNbtForSaving(pos);
	}

	@Override
	public Stream<BlockPos> getLights() {
		return this.wrapped.getLights();
	}

	@Override
	public TickContainerAccess<Block> getBlockTicks() {
		return this.wrapped.getBlockTicks();
	}

	@Override
	public TickContainerAccess<Fluid> getFluidTicks() {
		return this.wrapped.getFluidTicks();
	}

	@Override
	public ChunkAccess.TicksToSave getTicksForSerialization() {
		return this.wrapped.getTicksForSerialization();
	}

	@Nullable
	@Override
	public BlendingData getBlendingData() {
		return this.wrapped.getBlendingData();
	}

	@Override
	public void setBlendingData(BlendingData blendingData) {
		this.wrapped.setBlendingData(blendingData);
	}

	@Override
	public CarvingMask getCarvingMask(GenerationStep.Carving carver) {
		return super.getCarvingMask(carver);
	}

	@Override
	public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving carver) {
		return super.getOrCreateCarvingMask(carver);
	}

	public ProtoChunk getWrapped() {
		return this.wrapped;
	}

	@Override
	public boolean isLightCorrect() {
		return this.wrapped.isLightCorrect();
	}

	@Override
	public void setLightCorrect(boolean lightCorrect) {
		this.wrapped.setLightCorrect(lightCorrect);
	}

	@Override
	public void fillBiomesFromNoise(BiomeResolver biomeSupplier, Climate.Sampler sampler) {
		this.wrapped.fillBiomesFromNoise(biomeSupplier, sampler);
	}
}