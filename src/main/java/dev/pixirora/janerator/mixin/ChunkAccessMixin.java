package dev.pixirora.janerator.mixin;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;

import dev.pixirora.janerator.worldgen.JaneratorChunk;
import dev.pixirora.janerator.worldgen.PlacementSelector;
import net.minecraft.world.level.chunk.ChunkAccess;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements JaneratorChunk {
    private PlacementSelector janerator$selector;
    private boolean janerator$selecting;

    @Override
    public void janerator$selectWith(PlacementSelector selector, boolean selectInSections) {
        this.janerator$selector = selector;
        this.janerator$selecting = true;

        // If the generation operation in question uses only high level APIs to
        // modify blocks (ie anything not directly accessing LevelChunkSection instances)
        // then we don't need to make the LevelChunkSections be selective
        if (selectInSections) {
            Arrays.stream(((ChunkAccess) (Object) this).getSections())
                .forEach(section -> section.janerator$setSelector(selector));
        }
    }

    @Override
    public void janerator$stopSelecting() {
        this.janerator$selecting = false;
        Arrays.stream(((ChunkAccess) (Object) this).getSections())
            .forEach(section -> section.janerator$stopSelecting());
    }

    @Override
    public boolean janerator$allowWrites(int x, int z) {
        return !this.janerator$selecting || this.janerator$selector.isWanted(x, z);
    }
}
