package dev.pixirora.janerator.config;

import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Function;

import com.google.common.primitives.Doubles;

public class WrappedFunction {
    private Function function;
    private String name;
    private List<String> requiredVariables;


    WrappedFunction(String name, List<String> requiredVariables, String behaviorDefinition) {
        this.requiredVariables = requiredVariables;
        this.name = name;

        String functionDefinition = String.format("%s(%s) = %s", this.name, String.join(",", this.requiredVariables), behaviorDefinition);
        this.function = new Function(functionDefinition);
    }

    WrappedFunction(Variable variable) {
        this(variable.name, variable.requiredVariables, variable.definition);
    }

    public List<String> getRequiredVariables() {
        return this.requiredVariables;
    }

    public String getName() {
        return this.function.getFunctionName();
    }

    public double evaluate(Map<String, Double> variableMap) {
        return this.function.calculate(
            Doubles.toArray(
                this.requiredVariables
                    .stream()
                    .map(variableMap::get)
                    .toList()
            )
        );
    }
}
