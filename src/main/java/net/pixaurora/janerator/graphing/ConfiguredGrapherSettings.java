package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.variable.InputVariable;
import net.pixaurora.janerator.graphing.variable.Variable;

public class ConfiguredGrapherSettings {
    public static final Codec<ConfiguredGrapherSettings> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("variable_definitions").forGetter(ConfiguredGrapherSettings::getVariableDefinitions),
            Codec.STRING.fieldOf("return_statement").forGetter(ConfiguredGrapherSettings::getReturnStatement)
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

    private List<String> variableDefinitions;
    private String returnStatement;

    private List<Variable> variables;

    public ConfiguredGrapherSettings(List<String> variableDefinitions, String returnStatement) {
        this.variableDefinitions = variableDefinitions;
        this.returnStatement = returnStatement;

        this.variables = new ArrayList<>(this.variableDefinitions.size() + 1);
        this.variables.add(new InputVariable("x"));
        this.variables.add(new InputVariable("z"));

        Map<String, Variable> variableTable = new HashMap<>(
            this.variables.stream()
                .collect(Collectors.toMap(Variable::getName, variable -> variable))
        );

        for (String definition : variableDefinitions) {
            Variable nextVariable = Variable.fromStringDefinition(variableTable, definition);

            this.variables.add(nextVariable);
            variableTable.put(nextVariable.getName(), nextVariable);
        }

        this.variables.add(Variable.fromStringDefinition(variableTable, "returnValue = " + returnStatement));
    }

    public List<String> getVariableDefinitions() {
        return this.variableDefinitions;
    }

    public String getReturnStatement() {
        return this.returnStatement;
    }

    public List<Variable> getVariables() {
        return this.variables;
    }
}
