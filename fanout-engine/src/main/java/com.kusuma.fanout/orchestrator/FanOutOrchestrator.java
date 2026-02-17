package com.kusuma.fanout.orchestrator;

import com.kusuma.fanout.config.AppConfig;
import com.kusuma.fanout.config.ConfigLoader;
import com.kusuma.fanout.ingestion.CSVStreamer;
import com.kusuma.fanout.metrics.MetricsCollector;
import com.kusuma.fanout.model.Record;
import com.kusuma.fanout.retry.DeadLetterQueue;
import com.kusuma.fanout.sink.*;
import com.kusuma.fanout.transform.*;

import java.util.List;
import java.util.concurrent.*;

public class FanOutOrchestrator {

    private final MetricsCollector metrics = new MetricsCollector();
    private final DeadLetterQueue dlq = new DeadLetterQueue();
    private List<Sink> sinks;

    public void start() {

        AppConfig config = ConfigLoader.load();

        Transformer jsonTransformer = new JsonTransformer();
        Transformer protoTransformer = new ProtobufTransformer();
        Transformer xmlTransformer = new XmlTransformer();
        Transformer mapTransformer = new MapTransformer();

        sinks = List.of(
                config.restSink.enabled
                        ? new RestApiSink(config.restSink.rateLimitPerSecond, metrics, jsonTransformer)
                        : null,

                config.grpcSink.enabled
                        ? new GrpcSink(config.grpcSink.rateLimitPerSecond, metrics, protoTransformer)
                        : null,

                config.messageQueueSink.enabled
                        ? new MessageQueueSink(config.messageQueueSink.rateLimitPerSecond, metrics, xmlTransformer)
                        : null,

                config.wideColumnDbSink.enabled
                        ? new WideColumnDbSink(config.wideColumnDbSink.rateLimitPerSecond, metrics, mapTransformer)
                        : null
        ).stream().filter(s -> s != null).toList();

        BlockingQueue<Record> queue =
                new ArrayBlockingQueue<>(config.queueCapacity);

        ExecutorService producerExecutor =
                Executors.newSingleThreadExecutor();

        ExecutorService consumerExecutor =
                Executors.newVirtualThreadPerTaskExecutor();

        producerExecutor.submit(
                new CSVStreamer(config.inputFilePath, queue)
        );

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            consumerExecutor.submit(() -> {
                while (true) {
                    try {
                        Record record = queue.take();
                        processRecord(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            metrics.printMetrics();
            System.out.println("DLQ Size: " + dlq.size());
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void processRecord(Record record) {

        for (Sink sink : sinks) {

            metrics.incrementTotalProcessed(); // 🔥 moved here

            int maxRetries = 3;
            int attempt = 0;
            boolean success = false;

            while (attempt < maxRetries) {

                try {
                    sink.send(record);
                    success = true;
                    break;
                } catch (Exception e) {
                    attempt++;
                    System.out.println("[RETRY] Attempt " + attempt +
                            " for record: " + record.getId() +
                            " on sink: " + sink.getName());
                }
            }

            if (!success) {
                dlq.add(record);
                metrics.incrementFailure(sink.getName());
            }
        }
    }
}
