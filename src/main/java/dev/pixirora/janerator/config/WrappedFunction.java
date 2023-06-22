package dev.pixirora.janerator.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mXparser;

import com.google.common.primitives.Doubles;

import dev.pixirora.janerator.Janerator;

public class WrappedFunction {
    private Function function;
    private String name;
    private List<String> requiredVariables;

    static {
        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
    }

    WrappedFunction(String fullDefinition) {
        List<String> parts = Arrays.asList(fullDefinition.split("="));
        if (parts.size() != 2) {
            throw new OverridingConfigException("Function definition `" + fullDefinition + "` must have 1 = sign.");
        }

        this.name = parts.get(0);
        String behaviorDefinition = parts.get(1);

        Expression expr = new Expression(behaviorDefinition);
        if (!expr.checkSyntax()) {
            Janerator.LOGGER.info("OOPS! " + expr.getErrorMessage());
            this.requiredVariables = Arrays.asList(expr.getMissingUserDefinedArguments());
            Janerator.LOGGER.info("Solution to OOPSIE: " + String.join(",", requiredVariables));
        } else {
            this.requiredVariables = List.of("x"); // We do this because mXparser doesn't allow for functions with no inputs
        }

        String functionDefinition = this.name + "(" + String.join(",", this.requiredVariables) + ") = " + behaviorDefinition;

        this.function = new Function(functionDefinition);
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
