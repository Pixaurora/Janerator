package net.pixaurora.janerator.graphing.variable;

import java.util.List;
import java.util.Map;

import net.pixaurora.janerator.config.GraphingConfigException;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public interface Variable {
    public static Variable fromStringDefinition(Map<String, Variable> variableTable, String definitionText) {
        Definition definition = new Definition(definitionText);

        String name = definition.getName().strip();
        List<Variable> requiredVariables = definition.getRequiredVariables(variableTable);
        String expressionText = definition.getExpressionText();

        List<String> missingNames = requiredVariables.stream()
            .filter(variable -> variable instanceof MissingVariable)
            .map(Variable::getName)
            .toList();
        if (missingNames.size() > 0) {
            throw new GraphingConfigException(
                String.format(
                    "Variable definition for `%s` is using unknown variables `%s`",
                    definition.getName(),
                    String.join(", ", missingNames)
                )
            );
        }

        if (
            requiredVariables.stream()
                .allMatch(variable -> variable instanceof IndependentVariable)
        ) {
            List<IndependentVariable> requiredIndependentVariables = requiredVariables.stream()
                .map(variable -> (IndependentVariable) variable)
                .toList();

            return new IndependentVariable(name, requiredIndependentVariables, expressionText);
        } else {
            return new DependentVariable(name, requiredVariables, expressionText);
        }
    }

    public String getName();

    public List<Variable> getRequiredVariables();

    public default String getUniqueFunctionName() {
        return this.getUniqueFunctionName(this.getName());
    }

    public default String getUniqueFunctionName(String name) {
        if (
            this.getRequiredVariables().stream()
                .noneMatch(variable -> name.equals(variable.getName()))
        ) {
            return name;
        } else {
            return this.getUniqueFunctionName(name + 'a');
        }
    }

    public default String[] getRequiredNames() {
        return this.getRequiredVariables()
            .stream()
            .map(Variable::getName)
            .toList()
            .toArray(new String[]{});
    }

    public Instruction createInstruction(int[] accessIndexes, int setIndex);
}
