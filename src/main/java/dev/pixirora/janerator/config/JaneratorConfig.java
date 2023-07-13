package dev.pixirora.janerator.config;

import java.util.List;
import java.util.Map;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.QuiltConfig;

@Processor("setSerializer")
public class JaneratorConfig extends WrappedConfig {
    public static final JaneratorConfig INSTANCE = QuiltConfig.create("janerator", "preset", JaneratorConfig.class);

    public final FunctionToGraph graphed_function = new FunctionToGraph();

    public final Generators alternate_generator_presets = new Generators(
        Map.of(
            "overworld_flat_preset", "minecraft:bedrock,63*minecraft:deepslate,60*minecraft:stone,2*minecraft:dirt,minecraft:grass_block;minecraft:mushroom_fields",
            "nether_flat_preset", "1*minecraft:bedrock,30*minecraft:netherrack,1*minecraft:warped_nylium;minecraft:deep_dark",
            "end_flat_preset", "minecraft:bedrock,59*minecraft:stone,2*minecraft:dirt,minecraft:grass_block;minecraft:deep_dark"
        )
    );

    public static class FunctionToGraph implements Section {
        public final ValueList<String> variables = ValueList.create(
            "Pointless string?",
            "phi = (1 + sqrt(5)) / 2",
            "log_phi = ln(phi)",
            "dist_squared = x^2 + z^2",
            "angle = ln(dist_squared) / log_phi"
        );
        public final String inequality = "(z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0";
    }

    public void setSerializer(Config.Builder builder) {
		builder.format("json5");
	}

    @SuppressWarnings("unchecked")
    public static List<String> getOverrideVariableDefinitions() {
        return (List<String>) JaneratorConfig.INSTANCE.getValue(List.of("graphed_function", "variables")).value();
    }

    public static String getOverrideReturnStatement() {
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("graphed_function", "inequality")).value();
    }

    public static Generators getGenerators() {
        return (Generators) JaneratorConfig.INSTANCE.getValue(List.of("alternate_generator_presets")).value();
    }
}
