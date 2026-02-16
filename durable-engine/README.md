# Durable Execution Engine

## Overview

This project implements a Native Durable Execution Engine inspired by systems like Temporal, Cadence, and Azure Durable Functions.

A durable workflow can:
- Survive crashes
- Resume execution from the exact failure point
- Avoid re-executing completed side effects

Workflow state is persisted using SQLite, enabling reliable crash recovery.

---

## Project Structure

```
durable-engine
 ├── src/main/java
 │    ├── engine
 │    │     ├── DatabaseManager.java
 │    │     └── DurableContext.java
 │    └── examples/onboarding
 │          ├── EmployeeOnboardingWorkflow.java
 │          └── Main.java
 ├── pom.xml
 └── README.md
```

---

## Core Concept: Step Primitive

The engine provides the following API:

```java
<T> T step(String stepId, Callable<T> fn)
```

This allows developers to write normal Java code while automatically making it durable.

Example:

```java
ctx.step("create_record", () -> {
    return "Record Created";
});
```

---

## Persistence Layer

SQLite is used as the durable storage backend.

### Table Schema

```
steps (
    workflow_id TEXT,
    step_key TEXT,
    status TEXT,
    output TEXT,
    PRIMARY KEY (workflow_id, step_key)
)
```

### Stored Fields

- workflow_id → Unique workflow execution
- step_key → Step name combined with sequence number
- status → IN_PROGRESS or COMPLETED
- output → JSON serialized result

---

## Logical Sequence Tracking

To support loops and conditional logic, the engine uses:

```
AtomicInteger sequence
```

Each step generates:

```
step_key = stepId + "_" + sequenceNumber
```

Example:

```
create_record_1
provision_laptop_2
provision_access_3
```

This ensures uniqueness even if step names repeat.

---

## Crash Recovery Mechanism

Before executing a step:

1. Check database:
    - If COMPLETED → return cached result
    - If IN_PROGRESS → treat as failed and re-run

2. Insert record with IN_PROGRESS

3. Execute user function

4. Update record to COMPLETED

This guarantees:
- No duplicate execution of completed steps
- Safe recovery after crashes
- Protection against zombie steps

---

## Zombie Step Handling

Problem:
If a crash happens after a side effect but before the database commit, the step may re-execute incorrectly.

Solution:
- Step first writes IN_PROGRESS
- Only after successful execution is it updated to COMPLETED
- On restart, IN_PROGRESS steps are safely re-executed

This prevents duplicate side effects.

---

## Concurrency Support

Parallel steps are supported using:

- CompletableFuture
- ExecutorService

Example:
- Provision Laptop
- Provision Access

Both execute concurrently.

---

## Thread Safety

The `step()` method is declared as:

```java
public synchronized <T> T step(...)
```

This ensures safe database writes during parallel execution.

---

## Example Workflow: Employee Onboarding

Steps:

1. Create Record (Sequential)
2. Provision Laptop (Parallel)
3. Provision Access (Parallel)
4. Send Welcome Email (Sequential)

---

## CLI Usage

### Normal Run

Run:

```
examples.onboarding.Main
```

Workflow executes normally.

---

### Simulate Crash

Add program argument:

```
crash
```

Example:

Run Configuration → Program Arguments → crash

The workflow will intentionally crash mid-step.

Re-running without crash will resume from failure point.

---

## Build Instructions

From project root:

```
mvn clean install
```

Run using:

```
mvn exec:java -Dexec.mainClass="examples.onboarding.Main"
```

---

## Design Decisions

- SQLite chosen for lightweight persistence
- Jackson used for JSON serialization
- Generics used for type-safe step execution
- Atomic counter for logical step ordering
- Synchronized method for thread safety
- IN_PROGRESS state for zombie protection

---

## Future Improvements

- Stronger type-safe deserialization
- Pluggable storage backend (PostgreSQL, MySQL)
- Distributed lock support
- Idempotency tokens for external APIs
- Automatic sequence ID generation

---

## AI Prompts

All AI prompts used during development are documented in `Prompts.txt`.
