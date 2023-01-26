package dev.pixirora.janerator.mixin.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.pixirora.janerator.Janerator;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void beforeLoadLevel(CallbackInfo callbackInfo) throws IOException {
        String[] paths = new String[]{
            "world/poi",
            "world/region",
            "world/stats",
            "world/DIM1",
            "world/DIM-1",
            "world/entities",
            "world/data",
            "world/advancements",
            "world/playerdata"
        };

        for (String pathString : paths) {
            try{
                Path path = Paths.get(pathString);
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                }
            } catch (NoSuchFileException e) {
                continue;
            }

        }
        Janerator.LOGGER.info("World deleted.");
    }
}