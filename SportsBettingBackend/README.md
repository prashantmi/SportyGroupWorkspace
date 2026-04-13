# Sports Betting Backend

Spring Boot backend that simulates sports event outcome handling with Kafka, H2, and RocketMQ.

## Prerequisites

- Java 17
- Maven 3.9+ or the included `./mvnw`
- Docker Compose for local Kafka and RocketMQ infrastructure

## Start local infrastructure

From the workspace root:

```bash
docker compose up -d
```

This starts:

- ZooKeeper on `localhost:2181`
- Kafka on `localhost:9092`, with the `event-outcomes` topic created during startup
- RocketMQ NameServer on `localhost:9876`
- RocketMQ Broker on `localhost:10911`, with the `bet-settlements` topic created during startup

## Run the service

```bash
cd SportsBettingBackend
./mvnw spring-boot:run
```

The application defaults already point to the Docker Compose brokers:

- `KAFKA_BOOTSTRAP_SERVERS=localhost:9092`
- `ROCKETMQ_NAME_SERVER=localhost:9876`

## Run tests

```bash
cd SportsBettingBackend
./mvnw test
```

## API

`POST /api/event-outcomes`

OpenAPI specification:

- `openapi.yaml`

Example request:

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H 'Content-Type: application/json' \
  -d '{
    "eventId": "event-100",
    "eventName": "Match Winner",
    "eventWinnerId": "winner-1"
  }'
```

Example response:

```json
{
  "eventId": "event-100",
  "status": "PUBLISHED",
  "topic": "event-outcomes"
}
```

## Seeded bets

The service starts with four in-memory H2 bets:

- `event-100`: bet `1001` on `winner-1` (`STANDARD`, 100.00)
- `event-100`: bet `1002` on `winner-2` (`BOOSTED`, 75.00)
- `event-100`: bet `1003` on `winner-1` (`PREMIUM`, 40.00)
- `event-200`: bet `1004` on `winner-3` (`STANDARD`, 20.00)

If you publish outcome `event-100` with `winner-1`, the service will:

1. publish the outcome to Kafka topic `event-outcomes`
2. consume it and find matching open bets in H2
3. publish one settlement message per bet to RocketMQ topic `bet-settlements`
4. consume each settlement message and mark those bets as `SETTLED`

## Configuration

Key defaults are defined in `src/main/resources/application.yml`:

- Kafka topic: `event-outcomes`
- RocketMQ topic: `bet-settlements`
- Payout ratios:
  - `STANDARD=1.0`
  - `BOOSTED=1.5`
  - `PREMIUM=2.0`

## Notes

- Persistence is H2 in-memory only; restarting the app resets the data.
- The H2 console is available at `http://localhost:8080/h2-console`.
- This is an assessment-grade MVP and intentionally omits production concerns like DLQs, retries, and distributed transaction handling.
- To stop the local infrastructure, run `docker compose down` from the workspace root.
