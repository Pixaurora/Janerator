package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.graphing.instruction.Instruction;
import net.pixaurora.janerator.graphing.instruction.StaticInstruction;

public class IndependentVariable implements Variable {
    private String name;
    private List<Variable> requiredVariables;

    private double value;

    public IndependentVariable(String name, List<IndependentVariable> requiredIndependentVariables, String definitionStatement) {
        this.name = name;
        this.requiredVariables = requiredIndependentVariables.stream()
            .map(variable -> (Variable) variable)
            .toList();

        Function function = new Function(
            this.getUniqueFunctionName(name),
            definitionStatement,
            this.getRequiredNames()
        );

        if (requiredIndependentVariables.size() == 0) {
            this.value = function.calculate();
        } else {
            this.value = function.calculate(
                requiredIndependentVariables.stream()
                    .map(IndependentVariable::getValue)
                    .mapToDouble(Double::valueOf)
                    .toArray()
                );
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Variable> getRequiredVariables() {
        return this.requiredVariables;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        return new StaticInstruction(setIndex, this.value);
    }
}
