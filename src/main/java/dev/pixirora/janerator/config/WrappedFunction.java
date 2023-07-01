package dev.pixirora.janerator.config;

import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Function;

public class WrappedFunction {
    private String name;
    private Function function;
    private List<String> requiredVariables;

    WrappedFunction(String name, String definition, List<String> requiredVariables) {
        this.name = name;
        this.requiredVariables = requiredVariables;

        this.function = new Function(name, definition, requiredVariables.toArray(new String[]{}));
    }

    WrappedFunction(Variable variable) {
        this(variable.name, variable.definition, variable.requiredVariables);
    }

    WrappedFunction(WrappedFunction other) {
        this.name = other.name;
        this.requiredVariables = other.requiredVariables;
        this.function = other.function.cloneForThreadSafe();
    }

    public List<String> getRequiredVariables() {
        return this.requiredVariables;
    }

    public String getName() {
        return this.name;
    }

    public double evaluate(Map<String, Double> variableMap) {
        if (this.requiredVariables.size() < 1) {
            return this.function.calculate();
        }

        return this.function.calculate(
            this.requiredVariables
                .stream()
                .map(variableMap::get)
                .mapToDouble(Double::valueOf)
                .toArray()
        );
    }
}
