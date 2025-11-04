## Revolut investments

Calculation of the buy/sell shares results through the Revolut app. The output is based on a csv-file that is exported from the app

### Prerequisites

Before you begin, ensure you have the following installed on your system

* **JDK**
* **Apache Maven**
* **Docker**

---

## How to Build 

```bash
# Clean, compile, and package
mvn clean package

# Run in development mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Run in production mode
docker-compose up
```
