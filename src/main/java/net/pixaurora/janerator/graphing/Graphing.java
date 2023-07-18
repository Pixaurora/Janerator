package net.pixaurora.janerator.graphing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.minecraft.core.BlockPos;
import net.pixaurora.janerator.Janerator;
import net.pixaurora.janerator.threading.JaneratorThreadFactory;

public class Graphing {
    private static final ConfiguredGraphLogic baseGrapher = new ConfiguredGraphLogic();
    private static ThreadLocal<ConfiguredGraphLogic> grapher = ThreadLocal.withInitial(() -> new ConfiguredGraphLogic(Graphing.baseGrapher));

    public static Executor threadPool = Executors.newFixedThreadPool(16, new JaneratorThreadFactory());

    public static CompletableFuture<Boolean> scheduleGraphing(int x, int z) {
        return CompletableFuture.supplyAsync(() -> Graphing.grapher.get().isShaded(x, z), Graphing.threadPool);
    }

    public static CompletableFuture<Boolean> scheduleGraphing(BlockPos pos) {
        return Graphing.scheduleGraphing(pos.getX(), pos.getZ());
    }

    public static Boolean completeGraphing(CompletableFuture<Boolean> booleanFuture) {
        boolean shaded = false;

        try {
            shaded = booleanFuture.get();
        } catch (InterruptedException exception) {
            Janerator.LOGGER.error("Caught InterruptedException during override future.");
        } catch (ExecutionException exception) {
            Janerator.LOGGER.error("Caught ExecutionException during override future.");
        }

        return shaded;
    }

    public synchronized static boolean isOverridden(int x, int z) {
        return Graphing.baseGrapher.isShaded(x, z);
    }

    public static boolean isOverridden(BlockPos pos) {
        return Graphing.isOverridden(pos.getX(), pos.getZ());
    }
}
