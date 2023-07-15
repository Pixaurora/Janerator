package net.pixaurora.janerator.graphing;

import java.util.Arrays;
import java.util.List;

import org.mariuszgromada.math.mxparser.Expression;

import net.pixaurora.janerator.config.GraphingConfigException;

public class Variable {
    public String name;
    public String definition;
    public List<String> requiredVariables;

    private Expression expression;

    public Variable(String userDefinition) {
        List<String> parts = Arrays.asList(userDefinition.split("="));
        if (parts.size() != 2) {
            throw new GraphingConfigException(String.format("Function definition `%s` must have 1 = sign.", userDefinition));
        }

        this.name = parts.get(0).strip();
        this.definition = parts.get(1);

        this.expression = new Expression(definition);
        this.requiredVariables = Arrays.asList(this.expression.getMissingUserDefinedArguments());
    }

    public List<String> getMissingVariablesToEvaluate(List<String> knownVariables) {
        return this.requiredVariables
            .stream()
            .filter(variable -> ! knownVariables.contains(variable))
            .toList();
    }

    public void validateNeedsOnly(List<String> variableNames) {
        List<String> missingVariables = this.getMissingVariablesToEvaluate(variableNames);
        if (missingVariables.size() > 0) {
            throw new GraphingConfigException(
                String.format(
                    "Variable definition for %s is using unknown variables %s",
                    this.name,
                    String.join(", ", missingVariables)
                )
            );
        }
    }

    public boolean isCompletelyIndependent() {
        return this.requiredVariables.size() == 0;
    }

    public double evaluate() {
        return this.expression.calculate();
    }
}
