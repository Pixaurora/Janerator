package dev.pixirora.janerator.config;

import java.util.List;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.QuiltConfig;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@Processor("setSerializer")
public class JaneratorConfig extends WrappedConfig {
    public static final JaneratorConfig INSTANCE = QuiltConfig.create("janerator", "preset", JaneratorConfig.class);

    public final OverrideSelectionFunction override_selection_function = new OverrideSelectionFunction();
    public final GeneratorPresets alternate_generator_presets = new GeneratorPresets();

    public void setSerializer(Config.Builder builder) {
		builder.format("json5");
	}

    @SuppressWarnings("unchecked")
    public static List<String> getOverrideVariableDefinitions() {
        return (List<String>) JaneratorConfig.INSTANCE.getValue(List.of("override_selection_function", "variables")).value();
    }

    public static String getOverrideReturnStatement() {
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("override_selection_function", "return_statement")).value();
    }

    public static String getGeneratorPreset(ResourceKey<Level> dimension) {
        String field = String.format("%s_flat_preset", Generators.configFields.get(dimension));
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("alternate_generator_presets", field)).value();
    }

    public static class OverrideSelectionFunction implements Section {
        public final ValueList<String> variables = ValueList.create(
            "Pointless string?",
            "phi = (1 + sqrt(5)) / 2",
            "log_phi = ln(phi)",
            "dist_squared = x^2 + z^2",
            "angle = ln(dist_squared) / log_phi"
        );
        public final String return_statement = "(z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0";
    }

    public static class GeneratorPresets implements Section {
        public final String overworld_flat_preset = "minecraft:bedrock,63*minecraft:deepslate,60*minecraft:stone,2*minecraft:dirt,minecraft:grass_block;minecraft:mushroom_fields";
        public final String nether_flat_preset = "1*minecraft:bedrock,30*minecraft:netherrack,1*minecraft:warped_nylium;minecraft:deep_dark";
        public final String end_flat_preset = "minecraft:bedrock,59*minecraft:stone,2*minecraft:dirt,minecraft:grass_block;minecraft:deep_dark";
    }
}
