package dev.pixirora.janerator.config;

import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.mXparser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OverrideLogic {
    private Map<String, Double> independentVariables;
    private List<WrappedFunction> variableDefinitions;
    private WrappedFunction overrideFunction;

    static {
        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
    }

    public OverrideLogic() {
        this.independentVariables = Maps.newHashMap();
        this.variableDefinitions = Lists.newArrayList();

        List<Variable> variables = JaneratorConfig.getOverrideVariableDefinitions()
        .stream()
        .map(Variable::new)
        .toList();

        List<String> allKnownVariableNames = Lists.newArrayList("x", "z");
        List<String> independentVariableNames = Lists.newArrayList();
        for (Variable variable : variables) {
            variable.validateNeedsOnly(allKnownVariableNames);
            allKnownVariableNames.add(variable.name);

            // 0 input functions can't be used with mXparser currently, so a special case is needed.
            if (variable.isCompletelyIndependent()) {
                independentVariables.put(variable.name, variable.calculatedValue());
                continue;
            }

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

    public boolean shouldOverride(double x, double z) {
        Map<String, Double> variableMap = Maps.newHashMap(Map.of("x", x, "z", z));
        variableMap.putAll(this.independentVariables);

        for (WrappedFunction variable : this.variableDefinitions) {
            variableMap.put(variable.getName(), variable.evaluate(variableMap));
        }

        return this.overrideFunction.evaluate(variableMap) == 1.0;
    }
}
