package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.List;

import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import net.pixaurora.janerator.config.JaneratorConfig;

public class ConfiguredGraphLogic {
    private List<Double> independentVariables;
    private int variableCount;

    private List<WrappedFunction> variableDefinitions;
    private WrappedFunction overrideFunction;

    static {
        License.iConfirmNonCommercialUse("Rina Shaw <rina@pixaurora.net>");

        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
        mXparser.disableAlmostIntRounding();
        mXparser.disableCanonicalRounding();
        mXparser.disableUlpRounding();
    }

    public ConfiguredGraphLogic() {
        this.independentVariables = new ArrayList<>();
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

            if (variable.isCompletelyIndependent()) {
                this.independentVariables.add(variable.evaluate());
                continue;
            }

            List<String> missingDependentVariables = variable.getMissingVariablesToEvaluate(independentVariableNames);
            if (missingDependentVariables.size() == 0) {
                WrappedFunction variableDefinition = new WrappedFunction(variable, independentVariableNames);

                this.independentVariables.add(variableDefinition.evaluate(this.independentVariables));
                independentVariableNames.add(variable.name);
            } else {
                this.variableDefinitions.add(new WrappedFunction(variable, allKnownVariableNames));
            }
        }

        Variable returnValue = new Variable(String.format("shouldOverride = %s", JaneratorConfig.getOverrideReturnStatement()));
        returnValue.validateNeedsOnly(allKnownVariableNames);

        this.overrideFunction = new WrappedFunction(returnValue, allKnownVariableNames);

        this.variableCount = allKnownVariableNames.size();
    }

    public ConfiguredGraphLogic(ConfiguredGraphLogic other) {
        this.independentVariables = other.independentVariables;
        this.variableCount = other.variableCount;

        this.variableDefinitions = other.variableDefinitions
            .stream()
            .map(variable -> new WrappedFunction(variable))
            .toList();
        this.overrideFunction = new WrappedFunction(other.overrideFunction);
    }

    public boolean isShaded(double x, double z) {
        List<Double> variables = new ArrayList<>(this.variableCount);
        variables.add(x);
        variables.add(z);
        variables.addAll(this.independentVariables);

        for (WrappedFunction variable : this.variableDefinitions) {
            variables.add(variable.evaluate(variables));
        }

        return this.overrideFunction.evaluate(variables) == 1.0;
    }
}
