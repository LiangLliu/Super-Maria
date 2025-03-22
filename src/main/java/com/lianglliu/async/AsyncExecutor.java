package com.lianglliu.async;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class AsyncExecutor implements AutoCloseable {

    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private static volatile ExecutorService asyncExecutor = DEFAULT_EXECUTOR;

    private AsyncExecutor() {
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, asyncExecutor);
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, asyncExecutor);
    }

    public static <T> T fetch(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            throw new AsyncExecutionException("Task interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw new AsyncExecutionException("Task execution failed", cause);
        }
    }

    @SafeVarargs
    public static <T> List<T> mergeFutureLists(CompletableFuture<List<T>>... completableFutureList) {
        if (Objects.isNull(completableFutureList)) {
            return Collections.emptyList();
        }
        return CompletableFuture.allOf(completableFutureList)
                .thenApply(v -> Arrays.stream(completableFutureList)
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList()
                )
                .join();
    }

    public static synchronized void setCustomExecutor(ExecutorService executor) {
        Objects.requireNonNull(executor, "Executor cannot be null");
        if (asyncExecutor != DEFAULT_EXECUTOR && asyncExecutor != executor) {
            shutdownExecutor(asyncExecutor);
        }
        asyncExecutor = executor;
    }

    @Override
    public void close() {
        shutdown();
    }

    public static void shutdown() {
        if (asyncExecutor != DEFAULT_EXECUTOR) {
            shutdownExecutor(asyncExecutor);
            asyncExecutor = DEFAULT_EXECUTOR;
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class AsyncExecutionException extends RuntimeException {
        public AsyncExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
