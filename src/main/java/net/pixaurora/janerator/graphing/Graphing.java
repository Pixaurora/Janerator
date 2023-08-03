package net.pixaurora.janerator.graphing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.pixaurora.janerator.Janerator;
import net.pixaurora.janerator.config.GraphProperties;
import net.pixaurora.janerator.graph.Coordinate;
import net.pixaurora.janerator.threading.JaneratorThreadFactory;

public class Graphing {
    public static Executor threadPool = Executors.newFixedThreadPool(16, new JaneratorThreadFactory());

    public static CompletableFuture<Boolean> scheduleGraphing(GraphProperties graphProperties, int x, int z) {
        return CompletableFuture.supplyAsync(() -> graphProperties.getLocalGrapher().isShaded(x, z), Graphing.threadPool);
    }

    public static CompletableFuture<Boolean> scheduleGraphing(GraphProperties graphProperties, BlockPos pos) {
        return Graphing.scheduleGraphing(graphProperties, pos.getX(), pos.getZ());
    }

    public static Boolean completeGraphing(CompletableFuture<Boolean> booleanFuture) {
        boolean shaded = false;

        try {
            shaded = booleanFuture.get();
        } catch (InterruptedException exception) {
            Janerator.LOGGER.error("Caught InterruptedException during override future.");
        } catch (ExecutionException exception) {
            Janerator.LOGGER.error("Caught ExecutionException during override future.", exception);
        }

        return shaded;
    }

    public static boolean isOverridden(GraphProperties graphProperties, int x, int z) {
        return completeGraphing(scheduleGraphing(graphProperties, x, z));
    }

    public static boolean isOverridden(GraphProperties graphProperties, BlockPos pos) {
        return Graphing.isOverridden(graphProperties, pos.getX(), pos.getZ());
    }

    public static <T> List<Coordinate> getIndices(List<T>  items, T shade) {
        return IntStream.range(0, items.size())
            .filter(index -> items.get(index) == shade)
            .boxed()
            .map(Coordinate::fromListIndex)
            .toList();
    }
}
