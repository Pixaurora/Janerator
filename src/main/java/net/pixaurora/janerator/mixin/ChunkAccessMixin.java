package net.pixaurora.janerator.mixin;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.pixaurora.janerator.worldgen.JaneratorChunk;
import net.pixaurora.janerator.worldgen.PlacementSelection;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements JaneratorChunk {
    private PlacementSelection janerator$selection;
    private boolean janerator$selecting;

    @Override
    public ChunkAccess janerator$withSelection(PlacementSelection selection, boolean selectInSections) {
        this.janerator$selection = selection;
        this.janerator$selecting = true;

        // If the generation operation in question uses only high level APIs to
        // modify blocks (ie anything not directly accessing LevelChunkSection instances)
        // then we don't need to make the LevelChunkSections be selective
        if (selectInSections) {
            Arrays.stream(((ChunkAccess) (Object) this).getSections())
                .forEach(section -> section.janerator$setSelection(selection));
        }

        return (ChunkAccess) (Object) this;
    }

    @Override
    public void janerator$stopSelecting() {
        this.janerator$selecting = false;
        Arrays.stream(((ChunkAccess) (Object) this).getSections())
            .forEach(section -> section.janerator$stopSelecting());
    }

    @Override
    public boolean janerator$allowWrites(int x, int z) {
        return !this.janerator$selecting || this.janerator$selection.contains(x, z);
    }
}
