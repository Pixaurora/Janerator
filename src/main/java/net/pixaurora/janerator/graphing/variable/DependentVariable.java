package net.pixaurora.janerator.graphing.variable;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.graphing.instruction.DynamicInstruction;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public class DependentVariable implements Variable {
    private String name;

    private List<Variable> requiredVariables;
    private String definitionStatement;

    public DependentVariable(String name, List<Variable> usedVariables, String definitionStatement) {
        this.name = name;

        this.requiredVariables = usedVariables;
        this.definitionStatement = definitionStatement;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Variable> getRequiredVariables() {
        return this.requiredVariables;
    }

    public Function asFunction() {
        return new Function(this.name, this.definitionStatement, this.getRequiredNames());
    }

    @Override
    public Instruction createInstruction(int[] accessIndexes, int setIndex) {
        return new DynamicInstruction(accessIndexes, setIndex, this.asFunction());
    }
}
