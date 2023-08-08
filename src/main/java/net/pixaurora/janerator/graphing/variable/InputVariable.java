package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import net.pixaurora.janerator.graphing.instruction.Instruction;

public class InputVariable implements Variable {
    private String name;

    public InputVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Variable> getRequiredVariables() {
        return List.of();
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        throw new UnsupportedOperationException("Input variables cannot have instructions associated with them!");
    }
}
