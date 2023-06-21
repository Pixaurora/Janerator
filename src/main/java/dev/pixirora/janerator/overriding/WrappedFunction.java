package dev.pixirora.janerator.overriding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Function;

import com.google.common.primitives.Doubles;

public class WrappedFunction {
    private Function function;
    private List<String> requiredVariables;

    WrappedFunction(String functionDefinition) {
        this.function = new Function(functionDefinition);
        this.requiredVariables = new ArrayList<>();

        for (int i = 0; i < this.function.getArgumentsNumber(); i++) {
            this.requiredVariables.add(this.function.getArgument(i).getArgumentName());
        }
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
