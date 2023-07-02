package dev.pixirora.janerator.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class OverrideLogic {
    public static OverrideLogic INSTANCE;

    private Map<String, Double> independentVariables;
    private List<WrappedFunction> variableDefinitions;
    private WrappedFunction overrideFunction;

    static {
        License.iConfirmNonCommercialUse("Rina Shaw <rina@pixirora.dev>");

        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
        mXparser.disableAlmostIntRounding();
        mXparser.disableCanonicalRounding();
        mXparser.disableUlpRounding();

        OverrideLogic.INSTANCE = new OverrideLogic();
    }

    public OverrideLogic() {
        this.independentVariables = new HashMap<>();
        this.variableDefinitions = new ArrayList<>();

        List<Variable> variables = JaneratorConfig.getOverrideVariableDefinitions()
            .stream()
            .map(Variable::new)
            .toList();

        List<String> allKnownVariableNames = new ArrayList<>(List.of("x", "z"));
        List<String> independentVariableNames = new ArrayList<>();
        for (Variable variable : variables) {
            variable.validateNeedsOnly(allKnownVariableNames);
            allKnownVariableNames.add(variable.name);

            WrappedFunction variableDefinition = new WrappedFunction(variable);

            List<String> missingDependentVariables = variable.getMissingVariablesToEvaluate(independentVariableNames);
            if (missingDependentVariables.size() == 0) {
                this.independentVariables.put(variable.name, variableDefinition.evaluate(independentVariables));
                independentVariableNames.add(variable.name);
            } else {
                this.variableDefinitions.add(variableDefinition);
            }
        }

        Variable returnValue = new Variable(String.format("shouldOverride = %s", JaneratorConfig.getOverrideReturnStatement()));
        returnValue.validateNeedsOnly(allKnownVariableNames);

        this.overrideFunction = new WrappedFunction(returnValue);
    }

    public OverrideLogic(OverrideLogic other) {
        this.independentVariables = other.independentVariables;
        this.variableDefinitions = other.variableDefinitions
            .stream()
            .map(variable -> new WrappedFunction(variable))
            .toList();

        this.overrideFunction = new WrappedFunction(other.overrideFunction);
    }

    public synchronized boolean shouldOverride(double x, double z) {
        Map<String, Double> variableMap = new HashMap<>(Map.of("x", x, "z", z));
        variableMap.putAll(this.independentVariables);

        for (WrappedFunction variable : this.variableDefinitions) {
            variableMap.put(variable.getName(), variable.evaluate(variableMap));
        }

        return this.overrideFunction.evaluate(variableMap) == 1.0;
    }

    public boolean shouldOverride(ChunkPos chunkPos) {
        return this.shouldOverride(chunkPos.x*16, chunkPos.z*16);
    }

    public boolean shouldOverride(BlockPos pos) {
        return this.shouldOverride(pos.getX(), pos.getZ());
    }
}
