package dev.pixirora.janerator.overriding;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import dev.pixirora.janerator.JaneratorConfig;

public class OverrideLogic {
    private List<WrappedFunction> variableDefinitions;
    private WrappedFunction overrideFunction;

    public OverrideLogic() {
        this.variableDefinitions = JaneratorConfig.getLeadUpFunctions()
            .stream()
            .map(functionText -> new WrappedFunction(functionText))
            .toList();
        this.overrideFunction = new WrappedFunction(JaneratorConfig.getOverrideFunction());
    }

    public boolean shouldOverride(double x, double z) {
        Map<String, Double> variableMap = Maps.newHashMap(Map.of("x", x, "z", z));

        for (WrappedFunction variable : this.variableDefinitions) {
            variableMap.put(variable.getName(), variable.evaluate(variableMap));
        }

        return this.overrideFunction.evaluate(variableMap) == 1.0;
    }
}
