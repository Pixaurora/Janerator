package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.List;

import net.pixaurora.janerator.graphing.instruction.Instruction;

public class PointGrapher {
    private List<Double> startingVariables;
    private List<Instruction> appliedSteps;
    private int returnIndex;

    public PointGrapher(List<Double> startingVariables, List<Instruction> appliedSteps, int returnIndex) {
        this.startingVariables = startingVariables;
        this.appliedSteps = appliedSteps;
        this.returnIndex = returnIndex;
    }

    public boolean isShaded(double x, double z) {
        List<Double> variables = new ArrayList<>(this.startingVariables);
        variables.set(0, x);
        variables.set(1, z);

        for (Instruction step : this.appliedSteps) {
            step.execute(variables);
        }

        return variables.get(this.returnIndex) == 1.0;
    }
}
