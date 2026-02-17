package com.kusuma.fanout.config;

public class AppConfig {

    public String inputFilePath;
    public int queueCapacity;

    public SinkConfig restSink;
    public SinkConfig grpcSink;
    public SinkConfig messageQueueSink;
    public SinkConfig wideColumnDbSink;

    public static class SinkConfig {
        public boolean enabled;
        public int rateLimitPerSecond;
    }
}
