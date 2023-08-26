package net.pixaurora.janerator.graphing.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.GraphingUtils;
import net.pixaurora.janerator.graphing.PointGrapher;
import net.pixaurora.janerator.graphing.instruction.Instruction;
import net.pixaurora.janerator.graphing.variable.IndependentVariable;
import net.pixaurora.janerator.graphing.variable.InputVariable;
import net.pixaurora.janerator.graphing.variable.Variable;
import net.pixaurora.janerator.graphing.variable.VariableDefinition;

public class CustomGrapherSettings implements GrapherSettings {
    public static final Codec<CustomGrapherSettings> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("function_definitions").forGetter(CustomGrapherSettings::getRawFunctions),
            Codec.STRING.listOf().fieldOf("variable_definitions").forGetter(CustomGrapherSettings::getRawVariables),
            Codec.STRING.fieldOf("return_statement").forGetter(CustomGrapherSettings::getRawReturnStatement)
        )
        .apply(instance, CustomGrapherSettings::new)
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

    public CustomGrapherSettings(List<String> functionDefinitions, List<String> variableDefinitions, String returnStatement) {
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

    @Override
    public PointGrapher createGrapher() {
        AtomicInteger count = new AtomicInteger();
        Map<String, Integer> indexTable = new HashMap<>(
            Map.of(
                "x", count.getAndIncrement(),
                "z", count.getAndIncrement()
            )
        );

        for (VariableDefinition variable : this.variables) {
            indexTable.computeIfAbsent(variable.getName(), name -> count.getAndIncrement());
        }

        List<Instruction> runtimeInstructions = new ArrayList<>();
        List<Double> startingVariables = new ArrayList<>(Collections.nCopies(count.get(), 0.0));

        List<String> definedNames = new ArrayList<>(List.of("x, y"));

        for (VariableDefinition variable : this.variables) {
            int setIndex = indexTable.get(variable.getName());
            int[] accessIndexes = variable.getRequiredVariables().stream()
                .map(Variable::getName)
                .map(indexTable::get)
                .mapToInt(Integer::valueOf)
                .toArray();

            Instruction instruction = variable.createInstruction(accessIndexes, setIndex);
            boolean firstDefinition = ! definedNames.contains(variable.getName());

            if (variable instanceof IndependentVariable && firstDefinition) {
                instruction.execute(startingVariables);
            } else {
                runtimeInstructions.add(instruction);
            }

            definedNames.add(variable.getName());
        }

        return new PointGrapher(startingVariables, runtimeInstructions, indexTable.get("returnValue"));
    }

    @Override
    public GrapherSettingsType type() {
        return GrapherSettingsType.CUSTOM;
    }

    public List<String> getRawVariables() {
        return this.rawVariables;
    }

    public List<String> getRawFunctions() {
        return this.rawFunctions;
    }

    public String getRawReturnStatement() {
        return this.rawReturnStatement;
    }

    public List<VariableDefinition> getVariables() {
        return this.variables;
    }
}
