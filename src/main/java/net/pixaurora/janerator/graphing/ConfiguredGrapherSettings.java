package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.variable.InputVariable;
import net.pixaurora.janerator.graphing.variable.Variable;
import net.pixaurora.janerator.graphing.variable.VariableDefinition;

public class ConfiguredGrapherSettings {
    public static final Codec<ConfiguredGrapherSettings> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("function_definitions").forGetter(ConfiguredGrapherSettings::getRawFunctions),
            Codec.STRING.listOf().fieldOf("variable_definitions").forGetter(ConfiguredGrapherSettings::getRawVariables),
            Codec.STRING.fieldOf("return_statement").forGetter(ConfiguredGrapherSettings::getRawReturnStatement)
        )
        .apply(instance, ConfiguredGrapherSettings::new)
    );

    static {
        License.iConfirmNonCommercialUse("Rina Shaw <rina@pixaurora.net>");

        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
        mXparser.disableAlmostIntRounding();
        mXparser.disableCanonicalRounding();
        mXparser.disableUlpRounding();
    }

    private List<String> rawFunctions;
    private List<String> rawVariables;
    private String rawReturnStatement;

    private List<VariableDefinition> variables;

    public ConfiguredGrapherSettings(List<String> functionDefinitions, List<String> variableDefinitions, String returnStatement) {
        this.rawFunctions = functionDefinitions;
        this.rawVariables = variableDefinitions;
        this.rawReturnStatement = returnStatement;

        this.variables = new ArrayList<>(this.rawVariables.size() + 1);

        Map<String, Function> functionTable = new HashMap<>();

        Map<String, Variable> variableTable = new HashMap<>(
            Map.of(
                "x", new InputVariable("x"),
                "z", new InputVariable("z")
            )
        );

        for (String definitionText : this.rawFunctions) {
            Function function = new Function(definitionText);
            Expression asExpression = new Expression(function.getFunctionExpressionString());

            function.addFunctions(GraphingUtils.getRequiredFunctions(functionTable, asExpression, function.getFunctionName()).toArray(new Function[]{}));

            GraphingUtils.validate(function);

            functionTable.put(function.getFunctionName(), function);
        }

        for (String definitionText : this.rawVariables) {
            VariableDefinition definition = VariableDefinition.fromString(functionTable, variableTable, definitionText);

            GraphingUtils.validate(definition.asFunction());

            this.variables.add(definition);
            variableTable.put(definition.getName(), definition);
        }

        this.variables.add(VariableDefinition.fromString(functionTable, variableTable, "returnValue = " + returnStatement));
    }

    public List<String> getRawVariables() {
        return this.rawVariables;
    }

    public List<String> getRawFunctions() {
        return rawFunctions;
    }

    public String getRawReturnStatement() {
        return this.rawReturnStatement;
    }

    public List<VariableDefinition> getVariables() {
        return this.variables;
    }
}
