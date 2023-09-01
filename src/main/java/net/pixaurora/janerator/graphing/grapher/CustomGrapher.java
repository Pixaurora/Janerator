package net.pixaurora.janerator.graphing.grapher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.graphing.instruction.Instruction;

public class CustomGrapher extends ChunkGrapher {
    public static final Codec<CustomGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(CustomGrapher::getSettings)
        ).apply(instance, CustomGrapher::new)
    );

    private GraphFunctionDefinition settings;
    private ThreadLocal<GraphFunction> function;

    public CustomGrapher(GraphFunctionDefinition settings) {
        super();

        this.settings = settings;
        this.function = ThreadLocal.withInitial(() -> GraphFunction.fromDefinition(this.settings));
    }

    @Override
    public boolean isPointShaded(int x, int z)  {
        return this.function.get().evaluate(x, z) == 1.0;
    }

    @Override
    public GrapherType type() {
        return GrapherType.CUSTOM;
    }

    public GraphFunctionDefinition getSettings() {
        return settings;
    }

    public static class LocalGrapher {
        private List<Double> startingVariables;
        private List<Instruction> appliedSteps;
        private int returnIndex;

        public LocalGrapher(List<Double> startingVariables, List<Instruction> appliedSteps, int returnIndex) {
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
}