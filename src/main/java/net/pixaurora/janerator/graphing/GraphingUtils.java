package net.pixaurora.janerator.graphing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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
}
