package dev.pixirora.janerator.config;

import java.util.List;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.QuiltConfig;

public class JaneratorConfig extends WrappedConfig {
    public static final JaneratorConfig INSTANCE = QuiltConfig.create("janerator", "preset", JaneratorConfig.class);

    public final OverrideSelectionFunction override_selection_function = new OverrideSelectionFunction();

    public static List<String> getOverrideVariableDefinitions() {
        return (List<String>) JaneratorConfig.INSTANCE.getValue(List.of("override_selection_function", "variables")).value();
    }

    public static String getOverrideReturnStatement() {
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("override_selection_function", "return_statement")).value();
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
}
