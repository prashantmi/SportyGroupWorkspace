# Sports Betting Backend

Spring Boot backend that simulates sports event outcome handling with Kafka, H2, and RocketMQ. For this MVP bets are seeded into the in-memory H2 database at application startup so the settlement flow can be tested immediately.

## Prerequisites

- Java 17
- Docker Desktop or Docker Engine with Compose support
- Maven 3.9+ or the included `./mvnw`

## After checkout

From the workspace root:

```bash
git clone <repository-url>
cd SportyGroupWorkspace
```

Start the local messaging infrastructure from the workspace root. Use either `docker compose` or `docker-compose`, depending on what is available on your machine:

```bash
docker-compose up -d
```

This starts:

- ZooKeeper on `localhost:2181`
- Kafka on `localhost:9092`, with the `event-outcomes` topic created during startup
- RocketMQ NameServer on `localhost:9876`
- RocketMQ Broker on `localhost:10911`, with the `bet-settlements` topic created during startup

You can verify the containers are up with:

```bash
docker-compose ps
```

## Startup

Run the automated test suite first:

```bash
cd SportsBettingBackend
./mvnw test
```

Then start the backend service:

```bash
cd SportsBettingBackend
./mvnw spring-boot:run
```

The application defaults already point to the local Docker brokers:

- `KAFKA_BOOTSTRAP_SERVERS=localhost:9092`
- `ROCKETMQ_NAME_SERVER=localhost:9876`

The API will be available on `http://localhost:8080`.

When the application starts, H2 is initialized in memory and `BetDataSeeder` inserts demo open bets if the `bets` table is empty. There is no bet-placement API in this MVP, so these startup-seeded bets are the data used during local testing and settlement verification.

## Proper testing

### 1. Automated tests

Run:

```bash
cd SportsBettingBackend
./mvnw test
```

Expected result:

- all tests pass
- Maven finishes with `BUILD SUCCESS`

### 2. End-to-end smoke test

With the Docker infrastructure running and the Spring Boot app started, publish an event outcome:

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H 'Content-Type: application/json' \
  -d '{
    "eventId": "event-100",
    "eventName": "Match Winner",
    "eventWinnerId": "winner-1"
  }'
```

Expected API response:

```json
{
  "eventId": "event-100",
  "status": "PUBLISHED",
  "topic": "event-outcomes"
}
```

### 3. Verify settlement in H2

Open the H2 console:

- `http://localhost:8080/h2-console`

Use these login settings:

- JDBC URL: `jdbc:h2:mem:sportsbetting`
- Username: `sa`
- Password: leave blank

H2 login example:

![H2 Console Login](docs/images/h2-console-login.png)

Run:

```sql
SELECT id, user_id, event_id, bet_type, status, result, payout_amount, settled_at
FROM bets
WHERE event_id = 'event-100'
ORDER BY id;
```

Expected rows:

- `1001` -> `SETTLED`, `WIN`, `100.00`
- `1002` -> `SETTLED`, `LOSS`, `0.00`
- `1003` -> `SETTLED`, `WIN`, `80.00`

Expected query output example:

![H2 Console Query Results](docs/images/h2-console-query-results.png)

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
- Payout ratios: `STANDARD=1.0`, `BOOSTED=1.5`, `PREMIUM=2.0`
- Business assumption for this MVP: `BetType` is used as a proxy for relative risk/odds.
- `STANDARD` is treated as the highest-probability, lowest-return bet type.
- `BOOSTED` is treated as a medium-probability, higher-return bet type.
- `PREMIUM` is treated as the lowest-probability, highest-return bet type.
- Payout ratios apply only when a bet result is `WIN`.
- A losing bet always settles with payout `0.00` because the full stake is treated as lost.

## Notes

- Persistence is H2 in-memory only; restarting the app resets the data.
- The H2 console is available at `http://localhost:8080/h2-console`.
- This is an assessment-grade MVP and intentionally omits production concerns like DLQs, retries, and distributed transaction handling.
- To stop the local infrastructure, run `docker-compose down` from the workspace root. If your machine uses the plugin form, use `docker compose down`.
