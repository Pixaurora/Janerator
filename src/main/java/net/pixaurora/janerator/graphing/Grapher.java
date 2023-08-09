package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import net.pixaurora.janerator.graphing.instruction.Instruction;
import net.pixaurora.janerator.graphing.variable.IndependentVariable;
import net.pixaurora.janerator.graphing.variable.InputVariable;
import net.pixaurora.janerator.graphing.variable.Variable;

public class Grapher {
    private List<Double> startingVariables;
    private List<Instruction> intermediaryInstructions;
    private int returnIndex;

    public Grapher(List<Double> startingVariables, List<Instruction> instructionsToApply, int returnIndex) {
        this.startingVariables = startingVariables;
        this.intermediaryInstructions = instructionsToApply;
        this.returnIndex = returnIndex;
    }

    public static Grapher fromConfig(ConfiguredGrapherSettings config) {
        AtomicInteger count = new AtomicInteger();
        Map<String, Integer> nameToIndex = new HashMap<>(
            Map.of(
                "x", count.getAndIncrement(),
                "z", count.getAndIncrement()
            )
        );

        List<Variable> allVariables = config.getVariables();
        List<Variable> variableDefinitions = config.getVariables().stream()
            .filter(variable -> ! (variable instanceof InputVariable))
            .toList();

        for (Variable variable : variableDefinitions) {
            nameToIndex.computeIfAbsent(variable.getName(), name -> count.getAndIncrement());
        }

        List<Instruction> instructions = new ArrayList<>();
        List<Double> startingVariables = new ArrayList<>(
            IntStream.range(0, count.get())
                .mapToDouble(value -> 0.0)
                .boxed()
                .toList()
        );

        for (Variable variable : variableDefinitions) {
            int setIndex = nameToIndex.get(variable.getName());
            int[] accessIndexes = variable.getRequiredVariables().stream()
                .map(Variable::getName)
                .map(nameToIndex::get)
                .mapToInt(Integer::valueOf)
                .toArray();

            Instruction instruction = variable.createInstruction(accessIndexes, setIndex);

            boolean definedOnce = allVariables.stream()
                .filter(other -> variable.getName() == other.getName())
                .count() == 1;

            if (
                variable instanceof IndependentVariable && definedOnce
            ) {
                instruction.execute(startingVariables);
            } else {
                instructions.add(instruction);
            }
        }

        return new Grapher(startingVariables, instructions, nameToIndex.get("returnValue"));
    }

    public boolean isShaded(double x, double z) {
        List<Double> variables = new ArrayList<>(this.startingVariables);
        variables.set(0, x);
        variables.set(1, z);

        for (Instruction instruction : this.intermediaryInstructions) {
            instruction.execute(variables);
        }

        return variables.get(this.returnIndex) == 1.0;
    }
}
