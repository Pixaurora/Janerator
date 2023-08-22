package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import net.pixaurora.janerator.config.GraphingConfigException;
import net.pixaurora.janerator.config.ParserEvaluationException;
import net.pixaurora.janerator.threading.JaneratorThreadFactory;

public class GraphingUtils {
    public static Executor threadPool = Executors.newFixedThreadPool(16, new JaneratorThreadFactory());

    public static <T> T completeFuture(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<Coordinate> getIndices(List<T>  items, T value) {
        return IntStream.range(0, items.size())
            .filter(index -> items.get(index).equals(value))
            .boxed()
            .map(Coordinate::fromListIndex)
            .toList();
    }

    public static List<Function> getRequiredFunctions(Map<String, Function> functionTable, Expression expression, String name) {
        List<Function> requiredFunctions = new ArrayList<>();
        List<String> missingFunctions = new ArrayList<>();

        for (String requiredName : expression.getMissingUserDefinedFunctions()) {
            if (functionTable.containsKey(requiredName)) {
                requiredFunctions.add(functionTable.get(requiredName));
            } else {
                missingFunctions.add(requiredName);
            }
        }

        if (missingFunctions.size() > 1) {
            throw new GraphingConfigException(
                String.format("Variable definition for `%s` is using known functions: `%s`", name, String.join(", ", missingFunctions))
            );
        }

        return requiredFunctions;
    }

    public static void validate(Function function) {
        String NO_ERROR_MESSAGE = "No errors detected";

        int argumentCount = function.getArgumentsNumber();

        if (argumentCount == 0) {
            function.calculate();
        } else {
            double[] args = Collections.nCopies(argumentCount, 10.0).stream().mapToDouble(Double::valueOf).toArray();
            function.calculate(args);
        }

        String errorMessage = function.getErrorMessage();

        if (! errorMessage.contains(NO_ERROR_MESSAGE)) {
            throw new ParserEvaluationException(function, errorMessage);
        }
    }
}
