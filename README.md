# TTT Registration

A Spring Boot application for table tennis tournament registration and report generation. This system manages player registrations, disciplines, and generates PDF reports for receipts and player lists.

## Features

- **Player Registration Management**: Handles player data and their enrolled disciplines.
- **PDF Report Generation**:
  - Individual receipts for players (Saturday and Sunday).
  - Player lists grouped by discipline.
  - Blank receipts.
- **REST API**: Endpoints to trigger report generation and download PDFs.
- **Docker Support**: Easy deployment and local development using Docker and Docker Compose.

## Prerequisites

- **Java**: JDK 21
- **Maven**: 3.9 or higher
- **Docker**: (Optional) For running the database or the full application.

## Getting Started

### Clone the repository
```bash
git clone <repository-url>
cd ttt-registration
```

### Database Setup
The application requires a MariaDB database. For local development, you can use the provided Docker Compose configuration.

```bash
# Start MariaDB
docker compose up -d
```
This will start a MariaDB instance on port 3306 with the database `ttvettlingen24` and user `ttt/ttt`. It automatically initializes the schema and some test users.

### Building the Application

```bash
mvn clean package
```

### Running the Application

You can run the application using Maven with different profiles:

**Local Development (connects to Docker MariaDB):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Production (requires MariaDB credentials via env vars):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=default
```

**Testing (uses H2 in-memory database):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

Once running, the application is accessible at `http://localhost:8080`.

## Docker

### Build Docker Image
```bash
docker build -t ttt-registration .
```

### Run Container
```bash
docker run -p 8080:8080 ttt-registration
```

## Architecture

This project follows a three-layer architecture:
1.  **Web/REST Layer** (`infrastructure/web`, `infrastructure/api`): Handles HTTP requests and returns PDF responses.
2.  **Application/Service Layer** (`application`): Contains business logic, specifically `ReportService` for generating PDFs.
3.  **Infrastructure/Database Layer** (`infrastructure/db`): Handles data access using Spring `JdbcClient` (raw JDBC, no JPA).

### Key Technologies
- **Kotlin**: Primary programming language.
- **Spring Boot 3**: Framework foundation.
- **Thymeleaf**: Template engine for generating HTML for reports.
- **Flying Saucer**: Generates PDFs from Thymeleaf-rendered HTML.
- **MariaDB**: Production database.
- **H2**: Test database.

## API Endpoints / Reports

The following endpoints generate and download PDF files:

| Endpoint | Description | Filename |
|----------|-------------|----------|
| `GET /sunday-report` | Generates receipts for Sunday players | `quittungen_sonntag.pdf` |
| `GET /saturday-report` | Generates receipts for Saturday players | `quittungen_samstag.pdf` |
| `GET /player-lists` | Generates lists of players per discipline | `spielerliste.pdf` |
| `GET /blank-receipt` | Generates a blank receipt | `blanko_quittung.pdf` |

## Testing

To run the test suite:

```bash
mvn test
```

Tests use an in-memory H2 database initialized with the same schema as the production database.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
