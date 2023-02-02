package dev.pixirora.janerator.mixin.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;

import dev.pixirora.janerator.Janerator;
import dev.pixirora.janerator.wrapped.WrappedProtoChunk;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void beforeLoadLevel(CallbackInfo callbackInfo) throws IOException {
        Path path = Paths.get("world");

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }

        Janerator.LOGGER.info("World deleted.");
    }

    private Set<String> getDescriptors(Method[] methods) {
        return List.of(methods).stream()
            .filter(method -> {
                int modifiers = method.getModifiers();
                return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
            })
            .map(method -> method.getName())
            .collect(Collectors.toSet());
    }

    @Inject(method = "loadLevel()V", at = @At("HEAD"))
    private void findUnimplementedMethods(CallbackInfo callbackInfo) {
        Class<?> wrappedClass = WrappedProtoChunk.class;

        Set<String> wrappedMethods = getDescriptors(wrappedClass.getDeclaredMethods());
        Set<String> allMethods = getDescriptors(wrappedClass.getMethods());

        Set<String> unimplementedMethods = Sets.difference(
            Sets.difference(allMethods, wrappedMethods),
            getDescriptors(Object.class.getMethods())
        );

        if (unimplementedMethods.isEmpty()) {
            return;
        }

        Janerator.LOGGER.info("Unimplemented methods found! Here they are:");

        for (String methodDescription : unimplementedMethods) {
            Janerator.LOGGER.info(methodDescription);
        }
    }
}
