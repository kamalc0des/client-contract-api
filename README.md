# Client Contract API

A **Spring Boot 3** application built with **Java 21**.  
It provides a RESTful API to manage **Clients** (Persons and Companies) and their **Contracts**.  
The project demonstrates clean architecture, validation, and stateless JWT authentication.

---

## 1. Project Setup & Initialization

### Dependencies
Main frameworks and libraries:
- **Spring Boot 3.3+** — application framework  
- **Spring Web** — REST controllers  
- **Spring Data JPA** — ORM with H2 database  
- **Spring Security + JWT** — authentication & authorization  
- **Jakarta Validation** — input validation  
- **Lombok** — boilerplate reduction  
- **JUnit 5 + AssertJ + Mockito** — testing stack  

### Run locally
#### Requirements
- Java 21+  
- Maven 3.9+  
- Optional: `curl` and `jq` (for API testing)

#### Start the app
```bash
mvn clean spring-boot:run
# or build and run the jar
mvn -DskipTests package && java -jar target/client-contract-api-*.jar
```

#### Configuration
| Parameter | Default | Description |
|------------|----------|-------------|
| Port | `8080` | API server |
| Database | `H2 file` (`./data/api_factory_db`) | persists between runs |
| H2 console | `/h2-console` | JDBC URL: `jdbc:h2:file:./data/api_factory_db` |
| Default users | `superAdmin:admin123`, `user:user123` | in-memory |
| JWT secret | `super-secret-key-change-me` | configurable via env var |

---

## 2. Application Architecture

Please find all details about the application architecture bellow.

| Package | Role |
|----------|------|
| `controller` | REST endpoints — map HTTP to service calls |
| `service` | Business logic — validation, rules, and interactions |
| `repository` | Database layer using Spring Data JPA |
| `model` | Entities (`Client`, `Person`, `Company`, `Contract`) with inheritance and relationships |
| `dto` | Request/Response transfer objects, hiding internal fields |
| `config` | Spring Boot configuration (Security, H2, Backup) |
| `security` | JWT generation and filter |
| `resources` | Contains `application.yml` (DB + JWT config) |
| `test` | Integration & service tests validating end-to-end behavior |

**Entity design**
- `Client` → abstract superclass  
  - `Person` adds `birthDate`  
  - `Company` adds `companyId` (pattern: `^[a-z]{3}-\d{3}$`)
- `Contract` links to a `Client`, with:
  - `startDate`, `endDate` (ISO 8601)  
  - `costAmount` (`BigDecimal`, must be > 0)  
  - `updateDate` auto-managed via lifecycle hooks

---

## 3. Using the API

### H2 Database access
The application uses an embedded H2 database stored locally at : ./data/api_factory_db.
You can access it manually via the H2 Console while the application is running.

**Run the app:**
```bash
mvn spring-boot:run
```

**Console URL**
http://localhost:8080/h2-console

**Connection Settings**

| Property | Value | 
|---------|--------|
|JDBC URL	| jdbc:h2:file:./data/api_factory_db|
|Username	| sa|
|Password	| password|
|Driver Class	| org.h2.Driver|


Copy/Paste the above connection info to connect.

Tables like client, contract, person, and company are visible.

### Authentication
Obtain a JWT token (mandatory to execute request):
```bash
curl -s -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"superAdmin","password":"admin123"}'
```
Use it in every request:
```
-H "Authorization: Bearer <JWT_TOKEN>"
```

### Client Endpoints
| Action | Method | URL | Description |
|---------|--------|-----|-------------|
| Create Person | POST | `/api/clients` | Requires `name`, `email`, `phone`, `birthDate` |
| Create Company | POST | `/api/clients` | Requires `companyId` matching `aaa-123` |
| List all | GET | `/api/clients` | Returns all clients |
| Get by ID | GET | `/api/clients/{id}` | Retrieve one client |
| Update | PUT | `/api/clients/{id}` | All fields except `birthDate` / `companyId` |
| Delete | DELETE | `/api/clients/{id}` | Closes active contracts (sets `endDate = now`) |

**Date format**: ISO 8601 — `yyyy-MM-dd'T'HH:mm:ss`

Example — create a Person (don't forget to change the $TOKEN):
```bash
curl -X POST http://localhost:8080/api/clients   -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"   -d '{"name":"John Doe","email":"john.doe@email.com","phone":"+33698765432",
       "type":"PERSON","birthDate":"1992-05-15"}'
```

---

### Contract Endpoints
| Action | Method | URL | Description |
|---------|--------|-----|-------------|
| Create | POST | `/api/contracts` | Optional `startDate` and `endDate` |
| List | GET | `/api/contracts` | Returns all contracts |
| Update cost | PUT | `/api/contracts/{id}/cost?newAmount=500.00` | Automatically updates `updateDate` |
| Active by client | GET | `/api/contracts/client/{clientId}?updateDate=<date>` | Returns only active |
| Total sum | GET | `/api/contracts/client/{clientId}/total` | Returns total of active contracts |

Example — create and update (don't forget to change the $TOKEN, $PERSON_ID, $CONTRACT_ID):
```bash
NOW=$(date +"%Y-%m-%dT%H:%M:%S")
curl -X POST http://localhost:8080/api/contracts \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"clientId\":\"$PERSON_ID\",\"startDate\":\"$NOW\",\"costAmount\":300.00}"


curl -X PUT "http://localhost:8080/api/contracts/${CONTRACT_ID}/cost?newAmount=500.00" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

---

## 4. Proof That the API Works

### Automated Tests
Run:
```bash
mvn test
```
It executes:
- `ClientServiceTest` — ensures CRUD operations, contract closing on deletion  
- `ContractServiceTest` — validates defaults, update date logic, and active contract filtering  

### Scripted End-to-End Test
A complete Bash script `test_api.sh` is included to simulate API usage:
```bash
chmod +x test_api.sh && ./test_api.sh
```
It performs:
1. Login (JWT acquisition)  
2. Create Person & Company clients  
3. Create, update, and query contracts  
4. Validate totals and active filters  
5. Cleanup (delete created entities)  

---

## 5. Architecture description

The system follows a **layered architecture** separating REST exposure, business logic, and persistence.

- **Controllers** manage HTTP requests and DTO mapping.
- **Services** implement the main business rules such as contract lifecycle, cost updates, and client deletion behavior.
- **Repositories** abstract persistence using Spring Data JPA.

**Entities** follow an inheritance model (`Client → Person, Company`) to ensure clarity and type safety.

**Contracts** handle time-based logic with `@PrePersist` and `@PreUpdate`, ensuring consistent `startDate`, `endDate`, and `updateDate` values.

**Security** is stateless, relying on JWT tokens for authentication and request authorization.

Validation annotations guarantee input integrity across the API (dates, emails, phone numbers, and cost amounts).

Tests cover both service and integration layers, while the provided `test_api.sh` script automates full **end-to-end API validation**.

Everything runs locally **using an embedded H2 database**, easily replaceable by any SQL database through `application.yml`.