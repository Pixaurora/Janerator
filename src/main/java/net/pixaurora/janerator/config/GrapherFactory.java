package net.pixaurora.janerator.config;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.ConfiguredGraphLogic;

public class GrapherFactory {
    public static final Codec<GrapherFactory> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.listOf().fieldOf("variable_definitions").forGetter(GrapherFactory::getVariableDefinitions),
            Codec.STRING.fieldOf("return_statement").forGetter(GrapherFactory::getReturnStatement)
        )
        .apply(instance, GrapherFactory::new)
    );

    private List<String> variableDefinitions;
    private String returnStatement;

    private ConfiguredGraphLogic baseInstance;

    public GrapherFactory(List<String> variableDefinitions, String returnStatement) {
        this.variableDefinitions = variableDefinitions;
        this.returnStatement = returnStatement;

        this.baseInstance = new ConfiguredGraphLogic(variableDefinitions, returnStatement);
    }

    public ConfiguredGraphLogic createGraphLogic() {
        return new ConfiguredGraphLogic(this.baseInstance);
    }

    public List<String> getVariableDefinitions() {
        return variableDefinitions;
    }

    public String getReturnStatement() {
        return returnStatement;
    }
}
