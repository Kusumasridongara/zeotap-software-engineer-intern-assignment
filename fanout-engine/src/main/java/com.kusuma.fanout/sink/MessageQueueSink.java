package com.kusuma.fanout.sink;

import com.kusuma.fanout.metrics.MetricsCollector;
import com.kusuma.fanout.model.Record;
import com.kusuma.fanout.throttle.SimpleRateLimiter;
import com.kusuma.fanout.transform.Transformer;

public class MessageQueueSink implements Sink {

    private final SimpleRateLimiter rateLimiter;
    private final MetricsCollector metrics;
    private final Transformer transformer;

    public MessageQueueSink(int rateLimitPerSecond,
                            MetricsCollector metrics,
                            Transformer transformer) {
        this.rateLimiter = new SimpleRateLimiter(rateLimitPerSecond);
        this.metrics = metrics;
        this.transformer = transformer;
    }

    @Override
    public String getName() {
        return "MQ";
    }

    @Override
    public void send(Record record) throws Exception {

        rateLimiter.acquire();

        String transformed = transformer.transform(record);

        System.out.println("[MQ] Published: " + transformed);
        metrics.incrementSuccess(getName());
    }
}
