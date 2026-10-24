package com.lianglliu.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class AsyncExecutor {

    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private AsyncExecutor() {
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR);
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR);
    }

    public static <T> T fetch(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncExecutionException("Task interrupted", e);
        } catch (ExecutionException e) {
            throw new AsyncExecutionException("Task execution failed", e.getCause());
        }
    }

    public static class AsyncExecutionException extends RuntimeException {
        public AsyncExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
