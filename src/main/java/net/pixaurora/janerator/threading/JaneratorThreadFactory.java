package net.pixaurora.janerator.threading;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JaneratorThreadFactory implements ThreadFactory {
    private final AtomicInteger count;
    private final ThreadFactory parent;

    public JaneratorThreadFactory() {
        this.count = new AtomicInteger(-1);
        this.parent = Executors.defaultThreadFactory();
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = parent.newThread(runnable);

        int id = count.incrementAndGet();
        thread.setName("janerator-" + Integer.toString(id));

        return thread;
    }
}
