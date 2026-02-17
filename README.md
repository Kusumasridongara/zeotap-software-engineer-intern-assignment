# Zeotap Software Engineer Intern Assignment

This repository contains solutions for two assignments:

---

## ✅ Assignment 1 – Durable Execution Engine

A durable workflow execution system that ensures task state persistence and recovery.

### Features
- Persistent task execution
- Retry mechanism
- Failure recovery
- State durability
- Modular design

**Location:**  
durable-engine/

---

## ✅ Assignment 2 – High Throughput Fan-Out Engine

A concurrent fan-out processing engine that reads records from CSV and distributes them to multiple sinks.

### Features
- CSV ingestion using producer-consumer model
- Multi-threaded processing (virtual threads)
- Multiple sinks:
  - REST API
  - gRPC
  - Message Queue
  - Wide Column DB
- Rate limiting per sink
- Retry mechanism (3 attempts)
- Dead Letter Queue (DLQ)
- Metrics system:
  - Total processed records
  - Success count per sink
  - Failure count per sink
  - Throughput calculation

**Location:**  
fanout-engine/

---

## How to Run

### Assignment 1
cd durable-engine
mvn clean install

### Assignment 2
cd fanout-engine
mvn clean install

---

## Tech Stack
- Java
- Maven
- Concurrency (ExecutorService, BlockingQueue, Virtual Threads)
- Clean modular architecture

---


