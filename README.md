#Stock Trading & Trading Engine

![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7.x-red?style=for-the-badge&logo=redis)

A low-latency, high-concurrency order matching and trade execution engine engineered in Java 21 and Spring Boot 3, Redis and Docker. 
Built to process limit order books in-memory with sub-millisecond execution, while asynchronously persisting trade settlements via non-blocking batch worker pools 
and streams live prices to clients over WebSocket.

---

## Features

- **In-memory order matching engine**: limit order book with symbol-level locking, so orders on different symbols are matched in parallel without contention.
- **Asynchronous batch processing**: a multi-threaded worker reads trades from non-blocking queues and flushes them to the database in batches.
- **Fast DB writes**: Spring `JdbcTemplate` with atomic UPSERTs instead of ORM for bulk inserts.
- **Live price updates**: WebSocket integration with **Redis Pub/Sub** pushes price changes to connected clients in real time.
- **Clean error handling**: centralized Spring exception handling that returns consistent error responses from the REST APIs.
- **Load tested**: verified with k6 under heavy concurrent traffic.

## Performance

| Metric | Result |
|---|---|
| Concurrent BUY/SELL orders processed | 29,000+ |
| Order matching time | sub-10 ms |
| Load test throughput (k6) | 8,600+ RPS |
| Load test success rate | 100% (0% failures) across 600,000+ HTTP requests |
| Trades flushed per batch | 100+ (0 queue spillover under peak load) |
| Bulk write latency (p50) | sub-15 ms across 40,000+ trade records |

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot, REST APIs, WebSocket |
| Database access | Spring JDBC (`JdbcTemplate`) |
| Cache / Messaging | Redis (Pub/Sub) |
| Containerization | Docker |
| Load testing | k6 |

## How It Works

1. A client sends a BUY or SELL order through the REST API.
2. The order enters the in-memory order book for that symbol (locked per symbol, not globally).
3. The matching engine pairs compatible orders and produces trades.
4. Executed trades are pushed to a queue, and a batch worker writes them to the database (100+ trades per batch).
5. Price updates are published to Redis Pub/Sub and delivered to clients over WebSocket.

## Getting Started

### Prerequisites

- Java 17+ (or the version your project uses)
- PostgreSQL 16+
- Redis Server
- k6 (only for load testing)

### Run with Docker

```bash
git clone https://github.com/yashjadhav8903-git/stock-trading-engine.git
cd stock-trading-engine
```

### Run locally

```bash
mvn clean install
mvn spring-boot:run
```

Make sure Redis and the database are running and the connection details in `application.properties` are set.

### Run load tests

```bash
k6 run <your-k6-script>.js
```

## Author

**Yash Jadhav**
Java Backend Developer, Pune
Email: yashjadhav8903@gmail.com


---

## Architectural Overview

```text
               +-------------------------------------------------+
               |             Concurrent HTTP Requests            |
               +-------------------------------------------------+
                                       |
                                       v
               +-------------------------------------------------+
               |       Embedded Tomcat (600 Worker Threads)      |
               +-------------------------------------------------+
                                       |
                                       v
               +-------------------------------------------------+
               |            In-Memory Order Matching             |
               |       (Symbol Lock + Reentrant Locking)         |
               +-------------------------------------------------+
                                       |
                                       v
               +-------------------------------------------------+
               |  Asynchronous Batch Worker (LinkedBlockingQueue)|
               +-------------------------------------------------+
                                       |
                                       v
               +-------------------------------------------------+
               |   Spring JdbcTemplate Raw UPSERTs (HikariCP)    |
               +-------------------------------------------------+
                                       |
                                       v
               +-------------------------------------------------+
               |              PostgreSQL Database                |
               +-------------------------------------------------+
