package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.graphing.instruction.Instruction;
import net.pixaurora.janerator.graphing.instruction.StaticInstruction;

public class IndependentVariable extends VariableDefinition {
    private double value;

    public IndependentVariable(String name, List<IndependentVariable> requiredVariables, String definitionStatement) {
        super(name, definitionStatement, requiredVariables);

        Function function = this.asFunction();

        if (requiredVariables.size() == 0) {
            this.value = function.calculate();
        } else {
            this.value = function.calculate(
                requiredVariables.stream()
                    .map(IndependentVariable::getValue)
                    .mapToDouble(Double::valueOf)
                    .toArray()
            );
        }
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        return new StaticInstruction(setIndex, this.value);
    }
}
