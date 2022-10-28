package ua.pkk.wetravel.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void execute(Runnable task){
        executorService.execute(task);
    }
}
