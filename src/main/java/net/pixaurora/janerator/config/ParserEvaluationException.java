package net.pixaurora.janerator.config;

import org.mariuszgromada.math.mxparser.Function;

public class ParserEvaluationException extends RuntimeException {
    public ParserEvaluationException(Function function, String errorMessage) {
        super(
            String.format("Error in definition for `%s`: \n```%s\n```", function.getFunctionName(), errorMessage)
        );
    }

}
