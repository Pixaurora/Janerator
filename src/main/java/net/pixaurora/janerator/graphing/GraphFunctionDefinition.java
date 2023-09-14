package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.variable.InputVariable;
import net.pixaurora.janerator.graphing.variable.Variable;
import net.pixaurora.janerator.graphing.variable.VariableDefinition;

public class GraphFunctionDefinition {
    private static final Codec<GraphFunctionDefinition> createCodec(String... inputs) {
        return RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.STRING.listOf().fieldOf("functions").orElse(List.of()).forGetter(GraphFunctionDefinition::getRawFunctions),
                Codec.STRING.listOf().fieldOf("variables").orElse(List.of()).forGetter(GraphFunctionDefinition::getRawVariables),
                Codec.STRING.fieldOf("returns").forGetter(GraphFunctionDefinition::getRawReturnStatement)
            )
            .apply(
                instance,
                (functions, variables, return_statement) -> new GraphFunctionDefinition(List.of(inputs), functions, variables, return_statement)
            )
        );
    }

    public static final Codec<GraphFunctionDefinition> BIVARIATE_CODEC = createCodec("x", "z");
    public static final Codec<GraphFunctionDefinition> UNIVARIATE_CODEC = createCodec("v");

    static {
        License.iConfirmNonCommercialUse("Rina Shaw <rina@pixaurora.net>");

        mXparser.disableImpliedMultiplicationMode(); // Implied multiplication breaks searching for missing user-defined arguments
        mXparser.disableAlmostIntRounding();
        mXparser.disableCanonicalRounding();
        mXparser.disableUlpRounding();
    }

    private List<String> rawInputs;
    private List<String> rawFunctions;
    private List<String> rawVariables;
    private String rawReturnStatement;

    private List<VariableDefinition> variables;

    public GraphFunctionDefinition(List<String> inputVariables, List<String> functionDefinitions, List<String> variableDefinitions, String returnStatement) {
        this.rawInputs = inputVariables;
        this.rawFunctions = functionDefinitions;
        this.rawVariables = variableDefinitions;
        this.rawReturnStatement = returnStatement;

        this.variables = new ArrayList<>(this.rawVariables.size() + 1);

        Map<String, Function> functionTable = new HashMap<>();

        Map<String, Variable> variableTable = new HashMap<>(
            inputVariables.stream()
                .collect(Collectors.toMap(name -> name, InputVariable::new))
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

    public List<String> getRawInputs() {
        return rawInputs;
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
