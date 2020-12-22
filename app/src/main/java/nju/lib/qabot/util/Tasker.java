package nju.lib.qabot.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Tasker {

    ThreadPoolExecutor executor;

    public Tasker() {
        this(Runtime.getRuntime().availableProcessors(), 10);
    }
    public Tasker(int poolSize, long keepAliveTime) {
        BlockingQueue<Runnable> Q = new ArrayBlockingQueue<>(poolSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize * 2, keepAliveTime, TimeUnit.SECONDS, Q);
        executor.prestartAllCoreThreads();
    }

    public void todo(Runnable task) {
        if (task != null) executor.execute(task);
    }

}
