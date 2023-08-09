package net.pixaurora.janerator.graphing.instruction;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

public class DynamicInstruction implements Instruction {
    private final int[] accessIndexes;
    private final int setIndex;

    private final Function executedFunction;

    public DynamicInstruction(int[] accessIndexes, int setIndex, Function executedFunction) {
        this.accessIndexes = accessIndexes;
        this.setIndex = setIndex;

        this.executedFunction = executedFunction;
    }

    public void execute(List<Double> variables) {
        double[] args = new double[this.accessIndexes.length];

        for (int i = 0; i < this.accessIndexes.length; i++) {
            args[i] = variables.get(this.accessIndexes[i]);
        }

        variables.set(this.setIndex, this.executedFunction.calculate(args));
    }
}
