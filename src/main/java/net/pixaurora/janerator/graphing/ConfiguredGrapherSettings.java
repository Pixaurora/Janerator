package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Codec.STRING.listOf().fieldOf("variable_definitions").forGetter(ConfiguredGrapherSettings::getRawDefinitions),
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

    private List<String> rawDefinitions;
    private String rawReturnStatement;

    private List<VariableDefinition> definitions;

    public ConfiguredGrapherSettings(List<String> variableDefinitions, String returnStatement) {
        this.rawDefinitions = variableDefinitions;
        this.rawReturnStatement = returnStatement;

        this.definitions = new ArrayList<>(this.rawDefinitions.size() + 1);

        Map<String, Variable> variableTable = new HashMap<>(
            Map.of(
                "x", new InputVariable("x"),
                "z", new InputVariable("z")
            )
        );

        for (String definitionText : variableDefinitions) {
            VariableDefinition definition = VariableDefinition.fromString(variableTable, definitionText);

            definition.validate();

            this.definitions.add(definition);
            variableTable.put(definition.getName(), definition);
        }

        this.definitions.add(VariableDefinition.fromString(variableTable, "returnValue = " + returnStatement));
    }

    public List<String> getRawDefinitions() {
        return this.rawDefinitions;
    }

    public String getRawReturnStatement() {
        return this.rawReturnStatement;
    }

    public List<VariableDefinition> getDefinitions() {
        return this.definitions;
    }
}
