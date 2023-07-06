package dev.pixirora.janerator.graphing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import dev.pixirora.janerator.Janerator;
import dev.pixirora.janerator.threading.JaneratorThreadFactory;
import net.minecraft.core.BlockPos;

public class Graphing {
    private static final ConfiguredGraphLogic logic = new ConfiguredGraphLogic();

    public static Executor graphingThreadPool = Executors.newFixedThreadPool(16, new JaneratorThreadFactory());

    public static CompletableFuture<Boolean> scheduleGraphing(int x, int z) {
        return CompletableFuture.supplyAsync(new Graphing.GraphingTask(x, z), Graphing.graphingThreadPool);
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
        return Graphing.logic.isShaded(x, z);
    }

    public static boolean isOverridden(BlockPos pos) {
        return Graphing.isOverridden(pos.getX(), pos.getZ());
    }

    public static class GraphingTask implements Supplier<Boolean> {
        private int x;
        private int z;

        private static ThreadLocal<ConfiguredGraphLogic> graphLogic = ThreadLocal.withInitial(() -> new ConfiguredGraphLogic(Graphing.logic));

        public GraphingTask(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public Boolean get() {
            return GraphingTask.graphLogic.get().isShaded(this.x, this.z);
        }
    }
}
