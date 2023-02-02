package dev.pixirora.janerator.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.pixirora.janerator.JaneratorSection;
import dev.pixirora.janerator.worldgen.PlacementVerifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements JaneratorSection {
    @Nullable private PlacementVerifier verifier = null;

    public void janerator$setVerifier(PlacementVerifier verifier) {
        this.verifier = verifier;
    }

    @Inject(
        method="setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
        at=@At(value="HEAD"),
        cancellable=true
    )
    public void janerator$usePlacementVerifier(int x, int y, int z, BlockState blockState, boolean lock, CallbackInfoReturnable<BlockState> cir) {
        if (this.verifier != null && !this.verifier.isWanted(x, z)) {
            cir.setReturnValue(((LevelChunkSection) (Object) this).getBlockState(x, y, z));
        }
    }
}
