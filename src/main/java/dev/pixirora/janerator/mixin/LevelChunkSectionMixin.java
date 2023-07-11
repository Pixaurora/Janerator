package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.pixirora.janerator.worldgen.JaneratorSection;
import dev.pixirora.janerator.worldgen.PlacementSelection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements JaneratorSection {
    private PlacementSelection janerator$selection;
    private boolean janerator$selecting = false;

    @Override
    public void janerator$setSelection(PlacementSelection selection) {
        this.janerator$selection = selection;
        this.janerator$selecting = true;
    }

    @Override
    public void janerator$stopSelecting() {
        this.janerator$selecting = false;
    }

    @Override
    public boolean janerator$allowWrites(int x, int z) {
        return ! this.janerator$selecting || this.janerator$selection.contains(x, z);
    }

    @Inject(
        method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
        at = @At(value="HEAD"),
        cancellable = true
    )
    public void janerator$selectBlocks(int x, int y, int z, BlockState blockState, boolean lock, CallbackInfoReturnable<BlockState> cir) {
        if (this.janerator$disallowWrites(x, z)) {
            cir.setReturnValue(((LevelChunkSection) (Object) this).getBlockState(x, y, z));
        }
    }
}
