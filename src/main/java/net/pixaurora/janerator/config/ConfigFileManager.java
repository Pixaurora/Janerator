package net.pixaurora.janerator.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.quiltmc.loader.api.QuiltLoader;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.pixaurora.janerator.Janerator;
import net.pixaurora.janerator.RegistryCache;

public class ConfigFileManager {
    private static boolean configLocationWritable(Path savePath) {
        Path saveDirectory = savePath.getParent();
        try {
            Files.createDirectories(saveDirectory);

            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private static RegistryOps<JsonElement> getRegistryOps() {
        return RegistryOps.create(JsonOps.INSTANCE, RegistryCache.INSTANCE.getRegistry());
    }

    public static JaneratorConfig createInstance() {
        Path savePath = QuiltLoader.getConfigDir().resolve(Path.of("janerator", "preset.json"));

        try {
            return load(savePath);
        } catch (IOException exception) {
            JaneratorConfig config = createDefault();

            if (configLocationWritable(savePath)) {
                save(savePath, config);
            } else {
                Janerator.LOGGER.warn("Config could not be written!");
            }

            return config;
        }
    }

    public static JaneratorConfig load(Path savePath) throws IOException {
        JsonElement configData = GsonHelper.parse(Files.readString(savePath), false);

        return JaneratorConfig.CODEC
            .decode(getRegistryOps(), configData)
            .getOrThrow(false, Janerator.LOGGER::error)
            .getFirst();
    }

    public static boolean save(Path savePath, JaneratorConfig config) {
        JsonElement result = JaneratorConfig.CODEC.encodeStart(getRegistryOps(), config).getOrThrow(false, Janerator.LOGGER::error);

        try {
            Path saveDirectory = savePath.getParent();
            Files.createDirectories(saveDirectory);
            Files.writeString(savePath, result.toString());

            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private static JaneratorConfig createDefault() {
        return new JaneratorConfig(
            List.of(
                new GraphProperties(
                    Level.OVERWORLD,
                    new GrapherFactory(
                        List.of(
                            "phi = (1 + sqrt(5)) / 2",
                            "log_phi = ln(phi)",
                            "dist_squared = x^2 + z^2",
                            "angle = ln(dist_squared) / log_phi"
                        ),
                        "(z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0"
                    ),
                    DefaultFlatPresets.createShadedOverworldGenerator(),
                    DefaultFlatPresets.createOutlineOverworldGenerator()
                )
            )
        );
    }
}
