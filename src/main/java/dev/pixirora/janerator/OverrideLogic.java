package dev.pixirora.janerator;

import java.util.List;

import org.mariuszgromada.math.mxparser.Function;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class OverrideLogic {
    private List<Function> leadUpFunctions;
    private Function overrideFunction;

    public OverrideLogic() {
        this.leadUpFunctions = JaneratorConfig.getLeadUpFunctions()
            .stream()
            .map(functionText -> new Function(functionText))
            .toList();
        this.overrideFunction = new Function(JaneratorConfig.getOverrideFunction());
    }

    public boolean shouldOverride(double x, double z) {
        List<Double> functionArgs = Lists.newArrayList(x, z);
        for (Function leadUpFunction : leadUpFunctions) {
            functionArgs.add(
                leadUpFunction.calculate(Doubles.toArray(functionArgs))
            );
        }

        return overrideFunction.calculate(Doubles.toArray(functionArgs)) == 1.0;
    }
}
