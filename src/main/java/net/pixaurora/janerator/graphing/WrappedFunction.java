package net.pixaurora.janerator.graphing;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

public class WrappedFunction {
    private String name;
    private Function function;
    private List<Integer> variableIndices;

    WrappedFunction(Variable variable, List<String> variableOrder) {
        this.name = variable.name;
        this.variableIndices = variable.requiredVariables
            .stream()
            .map(variableOrder::indexOf)
            .toList();

        this.function = new Function(this.name, variable.definition, variable.requiredVariables.toArray(new String[]{}));
    }

    WrappedFunction(WrappedFunction other) {
        this.name = other.name;
        this.variableIndices = other.variableIndices;
        this.function = other.function.cloneForThreadSafe();
    }

    public String getName() {
        return this.name;
    }

    public double evaluate(List<Double> variables) {
        return this.function.calculate(
            this.variableIndices
                .stream()
                .map(variables::get)
                .mapToDouble(Double::valueOf)
                .toArray()
        );
    }
}
