package net.pixaurora.janerator.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.quiltmc.loader.api.QuiltLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.pixaurora.janerator.Janerator;
import net.pixaurora.janerator.RegistryCache;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.graphing.grapher.CustomGrapher;
import net.pixaurora.janerator.worldgen.FeatureFilter;

public class ConfigFileManager {
    private final Path savePath;

    private final RegistryOps<JsonElement> registryOps;
    private final Gson serializer;

    public ConfigFileManager() {
        this.savePath = QuiltLoader.getConfigDir().resolve(Path.of("janerator", "preset.json"));

        this.registryOps = RegistryOps.create(JsonOps.INSTANCE, RegistryCache.INSTANCE.getRegistry());
        this.serializer = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public JaneratorConfig createConfig() {
        try {
            return this.load();
        } catch (IOException exception) {
            JaneratorConfig config = createDefault();

            boolean configWritten = this.configLocationWritable() && this.save(config);

            if (! configWritten) {
                Janerator.LOGGER.warn("Default config was not written!");
            }

            return config;
        }
    }

    private boolean configLocationWritable() {
        Path saveDirectory = this.savePath.getParent();

        try {
            Files.createDirectories(saveDirectory);

            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public JaneratorConfig load() throws IOException {
        JsonElement configData = GsonHelper.parse(Files.readString(savePath), false);

        return JaneratorConfig.CODEC
            .decode(this.registryOps, configData)
            .getOrThrow(false, Janerator.LOGGER::error)
            .getFirst();
    }

    public boolean save(JaneratorConfig config) {
        JsonElement result = JaneratorConfig.CODEC.encodeStart(this.registryOps, config).getOrThrow(false, Janerator.LOGGER::error);

        try {
            Files.writeString(this.savePath, this.serializer.toJson(result));

            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private JaneratorConfig createDefault() {
        return new JaneratorConfig(
            List.of(
                new GraphProperties(
                    Level.OVERWORLD,
                    new CustomGrapher(
                        new GraphFunctionDefinition(
                            List.of("x", "z"),
                            List.of(),
                            List.of(
                                "phi = (1 + sqrt(5)) / 2",
                                "log_phi = ln(phi)",
                                "dist_squared = x^2 + z^2",
                                "angle = ln(dist_squared) / log_phi"
                            ),
                            "(z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0"
                        )
                    ),
                    DefaultGenerators.createShadedOverworldGenerator(),
                    DefaultGenerators.createOutlineOverworldGenerator()
                )
            ),
            FeatureFilter.defaultInstance()
        );
    }
}
