# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5.8 application for table tennis tournament registration and report generation. Written in Kotlin, uses JDBC for data access, Thymeleaf for templating, and Flying Saucer for PDF generation.

## Prerequisites

- Java 21
- Maven 3.9 or higher

## Common Commands

### Build and Test
```bash
# Build project
mvn clean package

# Run tests
mvn test

# Run single test class
mvn test -Dtest=GetReportScenarioTest

# Run single test method
mvn test -Dtest=GetReportScenarioTest#"Assert sunday report created and loaded"

# Skip tests during build
mvn clean package -DskipTests
```

### Running the Application
```bash
# Run with Maven
mvn spring-boot:run

# Run with production profile (requires MariaDB)
mvn spring-boot:run -Dspring-boot.run.profiles=default

# Run with test profile (uses H2 in-memory database)
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Docker
```bash
# Build Docker image
docker build -t ttt-registration .

# Run container
docker run -p 8080:8080 ttt-registration

# Build and push to DockerHub (automated via GitHub Actions)
```

## Architecture

### Three-Layer Architecture
```
Web/REST Controllers (infrastructure/web, infrastructure/rest)
    ↓
Services (infrastructure/report)
    ↓
Repositories (infrastructure/db)
    ↓
Database (MariaDB/H2)
```

### Key Packages
- `infrastructure/db/` - Data access layer using Spring JdbcClient (not JPA)
- `infrastructure/rest/` - REST API endpoints for report generation
- `infrastructure/web/` - Thymeleaf web controllers
- `infrastructure/report/` - Business logic for PDF generation

### Data Access Pattern
This project uses **raw JDBC with JdbcClient**, not JPA/Hibernate:
- Manual SQL queries in `PlayerRepository`
- Custom ResultSet mapping to Kotlin data classes
- In-memory aggregation of many-to-many relationships (Player ↔ Discipline)

### Database Profiles
- **Production**: MariaDB (`application.yaml`) - requires credentials via environment variables
- **Test**: H2 in-memory (`application-test.yaml`) - auto-initialized with `db/create-tables.sql`

## Report Generation Flow

1. REST endpoint triggers report generation (`/sunday-report`, `/saturday-report`, `/player-lists`)
2. `ReportService` queries `PlayerRepository` for player data
3. For each player/discipline, Thymeleaf renders HTML from templates:
   - `receipt.html` - Individual player receipts with pricing
   - `list.html` - Player lists grouped by discipline
4. Flying Saucer converts HTML to multi-page PDF
5. PDF written to `${user.home}` directory with hardcoded filename

**Important**: PDF output location is hardcoded to user home directory, not configurable.

## Domain Models

### Player (data class)
- Represents tournament participant
- Contains mutable `disciplines: List<Discipline>`
- Aggregated from multiple database rows (one per discipline enrollment)

### Discipline (data class)
- Represents tournament event type
- Contains pricing and payment status (`paid: 0 or 1`)
- Filtered by Type_ID: `< 20` = Saturday, `> 20` = Sunday

### Many-to-Many Relationship
```
Player (1) ←→ (M) typeperplayer (M) ←→ (1) Discipline
```

Aggregation happens in-memory via HashMap deduplication in repository layer.

## Testing

### Test Configuration
Tests use `@ActiveProfiles("test")` to activate H2TestDatabase configuration:
- H2 in-memory database in MySQL compatibility mode
- Schema loaded from `resources/db/create-tables.sql`
- Automatic cleanup after test context shutdown

### Test Types
- **Integration Tests**: Full Spring Boot context with TestRestTemplate
  - `GetReportScenarioTest` - REST API endpoints
  - `GetIndexTest` - Web controller (Java test)
- **Unit Tests**: Not present (repository methods test against real database)

### Running Tests
Tests require H2 database initialization. Always run via Maven to ensure proper Spring context loading.

## Kotlin-Specific Details

### Spring Kotlin Configuration
- `kotlin-maven-allopen` plugin enables Spring proxying of final classes
- Source directory: `src/main/kotlin` (not `src/main/java`)
- Kotlin stdlib and reflection dependencies included

### Kotlin Features Used
- Data classes for domain models (`Player`, `Discipline`)
- String interpolation in templates
- Named parameters and default values
- Extension functions (`Player.addDisciplines()`, `Player.name()`)

## PDF Generation Details

### Template System
- Thymeleaf templates in `resources/templates/`
- New `TemplateEngine` instance created per render (no caching)
- Base64-encoded images embedded directly in templates (tournament logo, checkmark icons)

### Template Variables
**receipt.html**:
- `base64Image` - Tournament logo
- `checkIcon` - Payment status indicator
- `player` - Player object with name, club, disciplines
- `sum` - Total price across all disciplines
- `allPaid` - Boolean for payment completion

**list.html**:
- `disciplineName` - Name of discipline
- `players` - List of players in that discipline
- Pre-prints 20 empty rows for manual additions

### Hardcoded Values
- Tournament name: "52. Albgauturnier" (in templates)
- PDF output directory: `System.getProperty("user.home")`
- Filenames: `quittungen_sonntag.pdf`, `quittungen_samstag.pdf`, `spielerlisten.pdf`

## Important Patterns

### ResultSet Mapping
Repository methods use custom `mapToPlayer()` function to transform JDBC ResultSet into domain objects. Each row contains duplicate player data with different discipline - deduplication happens post-query.

### Service Transaction Boundaries
Services are `@Service` annotated but don't use `@Transactional`. Read-only operations don't require transactions in current implementation.

### REST Endpoint Design
All endpoints return `void` - side effect is PDF file creation on filesystem. No streaming, no response body, just HTTP 200 on success.

## Docker Multi-Stage Build

### Build Stage
- Base: `maven:3.9-eclipse-temurin-21-alpine`
- Downloads dependencies (cached layer)
- Builds and explodes JAR into layers

### Runtime Stage
- Base: `eclipse-temurin:21-jre-alpine` (smaller image)
- Non-root user: `spring:spring`
- Exposes port 8080
- Entry point: `com.tt.tournament.TttRegistrationApplicationKt`

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/build-and-docker.yml`):
1. **build-and-test**: Maven build and test suite
2. **build-docker-image**: Multi-platform Docker build (amd64, arm64) and push to DockerHub

**Secrets Required**:
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

**Trigger Branches**: `main`, `develop` (plus PRs to main)

## Database Schema Notes

### Key Tables
- `player` - Player master data (Play_ID, Play_FirstName, Play_LastName, Play_Club_ID)
- `club` - Club information (Club_ID, Club_Name, Club_AdresseOrt)
- `type` - Discipline/event types (Type_ID, Type_Name, Type_StartGebuehr)
- `typeperplayer` - Many-to-many enrollment (typl_play_id, typl_type_id, typl_paid)

### SQL Query Pattern
Queries use old-style comma joins with WHERE clauses rather than explicit JOINs:
```sql
FROM typeperplayer tp, player P, type t, club c
WHERE tp.typl_play_id = P.Play_ID
AND t.Type_ID = tp.typl_type_id
AND c.Club_ID = P.Play_Club_ID
```

### Saturday vs Sunday Logic
- Saturday disciplines: `Type_ID < 20`
- Sunday disciplines: `Type_ID > 20`

This is a business rule encoded in repository queries, not database constraints.
