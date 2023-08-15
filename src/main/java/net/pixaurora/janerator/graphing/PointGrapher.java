package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.pixaurora.janerator.graphing.instruction.Instruction;
import net.pixaurora.janerator.graphing.variable.IndependentVariable;
import net.pixaurora.janerator.graphing.variable.Variable;
import net.pixaurora.janerator.graphing.variable.VariableDefinition;

public class PointGrapher {
    private List<Double> startingVariables;
    private List<Instruction> appliedSteps;
    private int returnIndex;

    public PointGrapher(List<Double> startingVariables, List<Instruction> appliedSteps, int returnIndex) {
        this.startingVariables = startingVariables;
        this.appliedSteps = appliedSteps;
        this.returnIndex = returnIndex;
    }
    public static PointGrapher fromConfig(ConfiguredGrapherSettings config) {
        AtomicInteger count = new AtomicInteger();
        Map<String, Integer> indexTable = new HashMap<>(
            Map.of(
                "x", count.getAndIncrement(),
                "z", count.getAndIncrement()
            )
        );

        List<VariableDefinition> definitions = config.getDefinitions();

        for (VariableDefinition definition : definitions) {
            indexTable.computeIfAbsent(definition.getName(), name -> count.getAndIncrement());
        }

        List<Instruction> runtimeInstructions = new ArrayList<>();
        List<Double> startingVariables = new ArrayList<>(Collections.nCopies(count.get(), 0.0));

        List<String> definedNames = new ArrayList<>(List.of("x, y"));

        for (VariableDefinition definition : definitions) {
            int setIndex = indexTable.get(definition.getName());
            int[] accessIndexes = definition.getRequiredVariables().stream()
                .map(Variable::getName)
                .map(indexTable::get)
                .mapToInt(Integer::valueOf)
                .toArray();

            Instruction instruction = definition.createInstruction(accessIndexes, setIndex);
            boolean firstDefinition = ! definedNames.contains(definition.getName());

            if (definition instanceof IndependentVariable && firstDefinition) {
                instruction.execute(startingVariables);
            } else {
                runtimeInstructions.add(instruction);
            }

            definedNames.add(definition.getName());
        }

        return new PointGrapher(startingVariables, runtimeInstructions, indexTable.get("returnValue"));
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
