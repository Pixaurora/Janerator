package net.pixaurora.janerator.config;

import net.pixaurora.janerator.graphing.variable.VariableDefinition;

public class ParserEvaluationException extends RuntimeException {
    public ParserEvaluationException(VariableDefinition variable, String errorMessage) {
        super(
            String.format("Error in definition for `%s`: \n```%s\n```", variable.getName(), errorMessage)
        );
    }

}
