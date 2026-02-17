package com.kusuma.fanout.sink;

import com.kusuma.fanout.metrics.MetricsCollector;
import com.kusuma.fanout.model.Record;
import com.kusuma.fanout.throttle.SimpleRateLimiter;
import com.kusuma.fanout.transform.Transformer;

import java.util.Random;

public class WideColumnDbSink implements Sink {

    private final SimpleRateLimiter rateLimiter;
    private final MetricsCollector metrics;
    private final Transformer transformer;
    private final Random random = new Random();

    public WideColumnDbSink(int rateLimitPerSecond,
                            MetricsCollector metrics,
                            Transformer transformer) {
        this.rateLimiter = new SimpleRateLimiter(rateLimitPerSecond);
        this.metrics = metrics;
        this.transformer = transformer;
    }

    @Override
    public String getName() {
        return "WideColumnDB";
    }

    @Override
    public void send(Record record) throws Exception {

        rateLimiter.acquire();

        String transformed = transformer.transform(record);

        if (random.nextInt(10) < 1) {
            metrics.incrementFailure(getName());
            throw new RuntimeException("DB Write Failed");
        }

        System.out.println("[WideColumnDB] Upserted: " + transformed);
        metrics.incrementSuccess(getName());
    }
}
