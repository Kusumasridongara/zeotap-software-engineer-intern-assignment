package com.kusuma.fanout.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {

    private final AtomicLong totalProcessed = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();

    private final Map<String, AtomicLong> successCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failureCount = new ConcurrentHashMap<>();


    public void incrementTotalProcessed() {
        totalProcessed.incrementAndGet();
    }

    public void incrementSuccess(String sinkName) {
        successCount
                .computeIfAbsent(sinkName, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    public void incrementFailure(String sinkName) {
        failureCount
                .computeIfAbsent(sinkName, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    public void printMetrics() {

        long currentTotal = totalProcessed.get();
        long elapsedSeconds =
                (System.currentTimeMillis() - startTime) / 1000;

        double throughput =
                elapsedSeconds == 0 ? 0 :
                        (double) currentTotal / elapsedSeconds;

        System.out.println("\n====== METRICS ======");
        System.out.println("Total Processed: " + currentTotal);
        System.out.println("Throughput (records/sec): "
                + String.format("%.2f", throughput));

        System.out.println("\nSuccess Count Per Sink:");
        successCount.forEach((k, v) ->
                System.out.println(k + " -> " + v.get()));

        System.out.println("\nFailure Count Per Sink:");
        failureCount.forEach((k, v) ->
                System.out.println(k + " -> " + v.get()));
    }
}
