package net.pixaurora.janerator.graphing.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.config.GraphingConfigException;
import net.pixaurora.janerator.graphing.GraphingUtils;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public abstract class VariableDefinition extends Variable {
    protected String definitionStatement;
    protected List<? extends Variable> requiredVariables;
    protected List<Function> requiredFunctions;

    protected VariableDefinition(String name, String definitionStatement, List<? extends Variable> requiredVariables, List<Function> requiredFunctions) {
        super(name);

        this.definitionStatement = definitionStatement;
        this.requiredVariables = requiredVariables;
        this.requiredFunctions = requiredFunctions;
    }

    public static VariableDefinition fromString(Map<String, Function> functionTable, Map<String, Variable> variableTable, String definitionText) {
        List<String> parts = new ArrayList<>(List.of(definitionText.split("=")));

        if (parts.size() < 2) {
            throw new GraphingConfigException(String.format("Variable definition `%s` must have at least one equal sign (=)!", definitionText));
        }

        String name = parts.remove(0).strip();

        String expressionText = String.join("=", parts);
        Expression expression = new Expression(expressionText);

        List<Variable> requiredVariables = Stream.of(expression.getMissingUserDefinedArguments())
            .map(requiredName -> variableTable.get(requiredName))
            .map(variable -> Objects.nonNull(variable) ? variable : new MissingVariable(name))
            .toList();

        List<String> missingVariables = requiredVariables.stream()
            .filter(variable -> variable instanceof MissingVariable)
            .map(Variable::getName)
            .toList();
        if (missingVariables.size() > 0) {
            throw new GraphingConfigException(
                String.format("Variable definition for `%s` is using unknown variables: `%s`",name, String.join(", ", missingVariables))
            );
        }

        List<Function> requiredFunctions = GraphingUtils.getRequiredFunctions(functionTable, expression, name);

        if (requiredVariables.stream().allMatch(variable -> variable instanceof IndependentVariable)) {
            List<IndependentVariable> requiredIndependentVariables = requiredVariables.stream()
                .map(variable -> (IndependentVariable) variable)
                .toList();

            return new IndependentVariable(name, expressionText, requiredIndependentVariables, requiredFunctions);
        } else {
            return new DependentVariable(name, expressionText, requiredVariables, requiredFunctions);
        }
    }

    public abstract Instruction createInstruction(int[] accessIndexes, int setIndex);

    public List<? extends Variable> getRequiredVariables() {
        return this.requiredVariables;
    }

    private String getUniqueFunctionName() {
        return this.getUniqueFunctionName(this.name);
    }

    private String getUniqueFunctionName(String name) {
        if (
            this.getRequiredVariables().stream()
                .noneMatch(variable -> name.equals(variable.getName()))
        ) {
            return name;
        } else {
            return this.getUniqueFunctionName(name + 'a');
        }
    }

    private String[] getRequiredNames() {
        return this.requiredVariables.stream()
            .map(Variable::getName)
            .toList()
            .toArray(new String[]{});
    }

    private Function[] cloneFunctions() {
        return this.requiredFunctions.stream()
            .map(Function::cloneForThreadSafe)
            .toList()
            .toArray(new Function[]{});
    }

    public Function asFunction() {
        Function function = new Function(this.getUniqueFunctionName(), this.definitionStatement, this.getRequiredNames());
        function.addFunctions(this.cloneFunctions());

        return function;
    }
}
