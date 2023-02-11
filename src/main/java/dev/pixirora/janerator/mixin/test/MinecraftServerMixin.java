package dev.pixirora.janerator.mixin.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private void janerator$beforeLoadLevel(CallbackInfo callbackInfo) throws IOException {
        Path path = Paths.get("world");

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                .filter(item -> !item.endsWith("playerdata"))
                .filter(item -> !item.endsWith("carpet.conf"))
                .filter(item -> !item.endsWith("level.dat"))
                .map(Path::toFile)
                .forEach(File::delete);
        }

        Janerator.LOGGER.info("World deleted.");
    }
}
