# Order Matching Engine (Event‑Driven, Deterministic)

## Overview

This project is a **production‑grade, event‑driven order matching engine** implemented in **pure Java**. It demonstrates how real trading systems achieve **deterministic matching**, **price‑time priority**, and **crash recovery** using **immutable events** and **single‑threaded execution**, without relying on heavy frameworks.

The goal of this project is not UI or frameworks, but **correctness, determinism, and system design discipline**.

---

## Key Design Goals

* Deterministic order matching (no race conditions)
* Strict **price‑time priority**
* Support for **partial fills**
* Event‑driven ingestion
* Append‑only persistence
* Full **replay‑based recovery** after crash
* Clean separation of concerns

---

## High‑Level Architecture

**Flow:**

Client / API
→ `OrderPlacedEvent`
→ Event Queue (thread‑safe)
→ Single‑threaded Dispatcher
→ Matching Engine
→ Trades
→ `TradeExecutedEvent` (persisted)

### Why Single‑Threaded Matching?

Even with a thread‑safe queue, concurrent matching would:

* Break price‑time priority
* Introduce race conditions
* Make replay impossible

All state mutation happens in **exactly one thread**, guaranteeing determinism without locks.

---

## Core Components

### 1. OrderBook

**Responsibility:**

* Stores *unmatched* orders only
* Maintains price‑time priority

**Data Structures:**

* BUY side: `TreeMap<Double, Deque<Order>>` (highest price first)
* SELL side: `TreeMap<Double, Deque<Order>>` (lowest price first)

**Invariants:**

* FIFO at same price level
* No empty price levels
* No quantity mutation inside OrderBook

> OrderBook is a **derived in‑memory projection**, never persisted.

---

### 2. MatchingEngine

**Responsibility:**

* Matches incoming orders against resting orders
* Handles partial and full fills
* Generates immutable trades

**Matching Rules:**

* BUY matches SELL if `buy.price ≥ sell.price`
* SELL matches BUY if `sell.price ≤ buy.price`
* Trade price = **resting order price**

Remaining quantity (if any) is added back to the OrderBook.

---

### 3. Event Model

The system is **event‑driven**, not state‑driven.

#### Domain Events

* `OrderPlacedEvent`
* `TradeExecutedEvent`

**Event Rules:**

* Events are **immutable**
* Events represent **facts that already happened**
* Events are the **only persisted source of truth**

---

### 4. Event Queue & Dispatcher

* `EventQueue`: thread‑safe ingress (`BlockingQueue`)
* `EventDispatcher`: single‑threaded engine loop

API threads never touch the matching engine directly.

> Queue guarantees order of events, dispatcher guarantees order of state mutation.

---

### 5. Event Store (Persistence & Replay)

**Implementation:**

* Append‑only file‑based event store
* One JSON event per line
* Jackson for serialization

**Recovery Model:**

1. Start with empty OrderBook
2. Replay all persisted events in order
3. Deterministically rebuild state

> **State = pure function of past events**

The OrderBook is never persisted directly.

---

## Testing Strategy

### Unit Tests

* OrderBook tests

  * Price priority
  * FIFO at same price
  * Cleanup invariants

* MatchingEngine tests

  * Full fills
  * Partial fills
  * Correct trade pricing
  * Resting behavior

Tests encode **market rules**, not implementation details.

---

## What This Project Demonstrates

* Correct use of **event sourcing principles**
* Deterministic systems without locks
* Clean domain modeling
* Test‑driven core logic
* Production‑style system boundaries

This project intentionally avoids:

* Premature microservices
* Distributed locks
* Over‑engineering

Correctness comes before scale.

---

## Possible Extensions

* REST API (Spring Boot)
* WebSocket market data feed
* Performance benchmarks (JMH)
* Snapshotting for faster replay
* Multi‑symbol matching engines

---

## How to Run

```bash
mvn test
```

(Tests validate core correctness.)

---

## Final Note

This project is designed to show **how trading systems actually work internally**, focusing on determinism, fairness, and recovery — not frameworks.

If you can explain this system clearly, you understand far more than just Java syntax.
