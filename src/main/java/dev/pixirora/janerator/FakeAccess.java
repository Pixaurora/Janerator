package dev.pixirora.janerator;

import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import dev.pixirora.janerator.mixin.ChunkAccessAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickContainerAccess;

// This class doesn't actually work yet...
public class FakeAccess extends ChunkAccess {
    ChunkAccess realAccess;
    List<Integer[]> placedPositions;

    public FakeAccess(ChunkAccess chunkAccess, List<Integer[]> placedPositions) {
        super(
            chunkAccess.getPos(), 
            chunkAccess.getUpgradeData(),
            ((ChunkAccessAccessor) chunkAccess).getLevelHeight(),
            Janerator.getRegistry(Registries.BIOME),
            chunkAccess.getInhabitedTime(),
            chunkAccess.getSections(),
            chunkAccess.getBlendingData()
        );

        this.realAccess = chunkAccess;
        this.placedPositions = placedPositions;
    }

    @Nullable
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        return this.realAccess.setBlockState(pos, state, moved);
    }

	public void setBlockEntity(BlockEntity blockEntity) {
        this.realAccess.setBlockEntity(blockEntity);
    }

	public void addEntity(Entity entity) {
        this.realAccess.addEntity(entity);
    }

    public ChunkStatus getStatus() {
        return realAccess.getStatus();
    }

	public void removeBlockEntity(BlockPos pos) {
        this.realAccess.removeBlockEntity(pos);
    }

    @Nullable
	public CompoundTag getBlockEntityNbtForSaving(BlockPos pos) {
        return this.realAccess.getBlockEntityNbtForSaving(pos);
    }

	public Stream<BlockPos> getLights() {
        return this.realAccess.getLights();
    }

	public TickContainerAccess<Block> getBlockTicks() {
        return this.realAccess.getBlockTicks();
    }

	public TickContainerAccess<Fluid> getFluidTicks() {
        return this.realAccess.getFluidTicks();
    }

	public ChunkAccess.TicksToSave getTicksForSerialization() {
        return this.realAccess.getTicksForSerialization();
    }

    public BlockState getBlockState(BlockPos pos) {
        return this.realAccess.getBlockState(pos);
    }

	public FluidState getFluidState(BlockPos pos) {
        return this.realAccess.getFluidState(pos);
    }

    @Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
        return this.realAccess.getBlockEntity(pos);
    }
}
