package net.pixaurora.janerator.graphing.instruction;

import java.util.List;

public interface Instruction {
    public void execute(List<Double> variables);
}
