package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import net.pixaurora.janerator.graphing.instruction.DynamicInstruction;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public class DependentVariable extends VariableDefinition {
    public DependentVariable(String name, List<? extends Variable> requiredVariables, String definitionStatement) {
        super(name, definitionStatement, requiredVariables);
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        return new DynamicInstruction(accessIndexes, setIndex, this.asFunction());
    }
}
