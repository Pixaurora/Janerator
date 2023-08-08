package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import net.pixaurora.janerator.graphing.instruction.Instruction;

public class MissingVariable implements Variable {
    private String name;

    public MissingVariable(String name) {
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
        throw new UnsupportedOperationException("Missing variables cannot create instructions!");
    }
}
