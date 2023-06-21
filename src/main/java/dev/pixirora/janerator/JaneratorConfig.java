package dev.pixirora.janerator;

import java.util.List;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.QuiltConfig;

public class JaneratorConfig extends WrappedConfig {
    public static final JaneratorConfig INSTANCE = QuiltConfig.create("janerator", "preset", JaneratorConfig.class);

    public final OverrideSelection override_selection = new OverrideSelection();

    public static String getOverrideFunction() {
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("override_selection", "override_function")).value();
    }

    public static List<String> getLeadUpFunctions() {
        return (List<String>) JaneratorConfig.INSTANCE.getValue(List.of("override_selection", "variables_in_override_function")).value();
    }

    public static class OverrideSelection implements Section {
        public final ValueList<String> variables_in_override_function = ValueList.create(
            "Pointless string?",
            "phi() = (1 + sqrt(5)) / 2",
            "log_phi(phi) = ln(phi)",
            "dist_squared(x,z) = x^2 + z^2",
            "angle(log_phi, dist_squared) = ln(dist_squared) / log_phi"
        );
        public final String override_function = "f(x, z, angle) = (z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0";
    }
}
