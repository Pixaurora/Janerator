package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.graphing.instruction.DynamicInstruction;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public class DependentVariable extends VariableDefinition {
    public DependentVariable(String name, String definitionStatement, List<? extends Variable> requiredVariables, List<Function> requiredFunctions) {
        super(name, definitionStatement, requiredVariables, requiredFunctions);
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        return new DynamicInstruction(accessIndexes, setIndex, this.asFunction());
    }
}
