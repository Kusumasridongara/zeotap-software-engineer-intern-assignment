# High Throughput Fan-Out Engine

## Overview

This project implements a High Throughput Fan-Out Engine in Java.

It reads records from a CSV file and distributes them to multiple sinks:

- REST API Sink
- gRPC Sink
- Message Queue Sink
- Wide Column Database Sink

The system supports:

- Multi-threaded processing
- Rate limiting per sink
- Retry mechanism
- Dead Letter Queue (DLQ)
- Metrics collection
- Transformation layer
- Config-driven enable/disable of sinks

---

## Architecture

Flow:

CSV File  
↓  
Blocking Queue  
↓  
FanOutOrchestrator  
↓  
Transformers  
↓  
Sinks (REST / gRPC / MQ / WideColumnDB)  
↓  
Metrics + Retry + DLQ

---

## Features Implemented

### 1. Multi-threaded Processing
Uses ExecutorService to process records concurrently.

### 2. Config Driven Design
All configuration is read from:

src/main/resources/config.json

You can:
- Enable or disable sinks
- Change rate limits
- Change queue capacity
- Change input file

### 3. Rate Limiting
Each sink uses SimpleRateLimiter to control throughput.

### 4. Retry Mechanism
Each record:
- Retries up to 3 times on failure
- If still fails → moved to Dead Letter Queue

### 5. Dead Letter Queue (DLQ)
Failed records after max retries are stored in DLQ.

DLQ size is printed in metrics.

### 6. Metrics System
Tracks:

- Total processed
- Success count per sink
- Failure count per sink
- Throughput (records per second)
- DLQ size

### 7. Transformation Layer

Before sending to sinks, records are transformed:

REST → JSON  
gRPC → Simulated Protobuf  
MQ → XML  
WideColumnDB → Map

This makes the system extensible and production-ready.

---

## Project Structure

fanout-engine/
│
├── data/
│   └── sample.csv
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/kusuma/fanout/
│       │       ├── config/
│       │       ├── ingestion/
│       │       ├── metrics/
│       │       ├── model/
│       │       ├── orchestrator/
│       │       ├── retry/
│       │       ├── sink/
│       │       ├── throttle/
│       │       ├── transform/
│       │       └── Main.java
│       │
│       └── resources/
│           └── config.json
│
└── README.md

---

## How To Run

### Step 1
Open project in IntelliJ.

### Step 2
Build → Rebuild Project

### Step 3
Run Main.java

You will see:

- Records being processed
- Retry logs
- Sink outputs
- Final metrics summary

---

## Sample Metrics Output

====== METRICS ======
Total Processed: 5
Throughput (records/sec): 1.00

Success Count Per Sink:
REST -> 5
MQ -> 5
WideColumnDB -> 5
gRPC -> 5

Failure Count Per Sink:
REST -> 1

DLQ Size: 0

---

## How To Modify Configuration

Edit:

src/main/resources/config.json

Example:

{
"inputFilePath": "fanout-engine/data/sample.csv",
"queueCapacity": 1000,
"restSink": { "enabled": true, "rateLimitPerSecond": 50 },
"grpcSink": { "enabled": true, "rateLimitPerSecond": 100 },
"messageQueueSink": { "enabled": true, "rateLimitPerSecond": 200 },
"wideColumnDbSink": { "enabled": true, "rateLimitPerSecond": 1000 }
}

---

## Design Principles Used

- Single Responsibility Principle
- Open/Closed Principle
- Interface-based design (Sink, Transformer)
- Config-driven architecture
- Thread-safe metrics
- Retry with fault tolerance

---

## Technologies Used

- Java
- ExecutorService
- BlockingQueue
- ConcurrentHashMap
- AtomicLong
- Maven

---

## Assignment Coverage

This project implements:

✔ High throughput design  
✔ Fan-out pattern  
✔ Config driven architecture  
✔ Retry + DLQ  
✔ Metrics collection  
✔ Transformation layer  
✔ Multi-threading  
✔ Rate limiting

---


