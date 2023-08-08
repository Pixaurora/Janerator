package net.pixaurora.janerator.graphing.instruction;

import java.util.List;

public class StaticInstruction implements Instruction {
    private int setIndex;
    private double value;

    public StaticInstruction(int setIndex, double value) {
        this.setIndex = setIndex;
        this.value = value;
    }

    @Override
    public void execute(List<Double> variables) {
        variables.set(this.setIndex, this.value);
    }
}
