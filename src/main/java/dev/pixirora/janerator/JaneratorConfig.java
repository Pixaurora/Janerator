package dev.pixirora.janerator;

import java.util.List;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.loader.api.config.QuiltConfig;

public class JaneratorConfig extends WrappedConfig {
    public static final JaneratorConfig INSTANCE = QuiltConfig.create("janerator", "preset", JaneratorConfig.class);

    public final OverrideSelection override_selection = new OverrideSelection();

    public static String getOverrideFunction() {
        return (String) JaneratorConfig.INSTANCE.getValue(List.of("override_selection", "override_function")).value();
    }

    public static class OverrideSelection implements Section {
        public final String override_function = "f(x,z)=x>z";
    }
}
