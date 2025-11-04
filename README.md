## Revolut Investments Share Calculator

This project provides a tool to calculate the results of buy/sell share transactions based on a **CSV file exported directly from the Revolut app**

---

### Prerequisites

- **JDK**
- **Apache Maven**
- **Docker**
- **Python 3** (Required for the database migration tool)
- Configure the required environment variables (see .env.example)

---

### Build

Use Maven to clean, compile and package the application

```bash
# Full and clean build
mvn clean package

# Or only recompiling changed source files (for dev mode)
mvn compile
```

Use Docker Compose for consistent execution environment

```bash
# Dev mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Prod mode
docker-compose up
```

This project uses a simple python-based migration script.
Migration script is executed via simple batch/bash wrappers

```bash
# Executes all pending database schema migrations
./run_migration.sh up

# Reverts the last executed database schema migration
./run_migration.sh down
```
