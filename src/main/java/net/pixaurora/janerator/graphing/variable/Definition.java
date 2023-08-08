package net.pixaurora.janerator.graphing.variable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Expression;

import net.pixaurora.janerator.config.GraphingConfigException;

public class Definition {
    private String name;
    private List<String> requiredNames;

    private Expression expression;

    public Definition(String definitionText) {
        List<String> parts = Arrays.asList(definitionText.split("="));
        if (parts.size() != 2) {
            throw new GraphingConfigException(String.format("Function definition `%s` must have exactly one equal sign (=)", definitionText));
        }

        this.name = parts.get(0);
        this.expression = new Expression(parts.get(1));

        this.requiredNames = List.of(this.expression.getMissingUserDefinedArguments());
    }

    public String getName() {
        return this.name;
    }

    public List<String> getRequiredNames() {
        return this.requiredNames;
    }

    public String getExpressionText() {
        return this.expression.getExpressionString();
    }

    public List<Variable> getRequiredVariables(Map<String, Variable> variableTable) {
        return this.requiredNames.stream()
            .map(name -> variableTable.getOrDefault(name, new MissingVariable(name)))
            .toList();
    }
}
