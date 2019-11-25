package com.lab5com.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class FutureHelper {
    private FutureHelper() {}

    public static void waitExecution(Future... futures) {
        for (Future f : futures) {
            waitExecution(f);
        }
    }

    public static <T> T waitExecution(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void waitExecution(List<Future> futures) {
        waitExecution(futures.toArray(new Future[futures.size()]));
    }
}