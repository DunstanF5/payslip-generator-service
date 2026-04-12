# Payslip Generator Microservice - Learning Plan

## Overview
This learning plan teaches Spring Boot, Microservices, and Docker by building a Payslip Generator service similar to Jemini's STP-service architecture.

---

## Phase 1: Spring Boot Basics ✅ COMPLETED
**Goal**: Set up a basic Spring Boot REST API

- [x] Step 1.1: Create Spring Boot project with Spring Initializr
- [x] Step 1.2: Create a simple REST controller
- [x] Step 1.3: Create service layer
- [x] Step 1.4: Add request/response DTOs
- [x] Step 1.5: Add API key authentication filter

---

## Phase 2: Advanced Spring Boot Features ✅ COMPLETED
**Goal**: Add async processing and job management

- [x] Step 2.1: Create async processor with @Async
- [x] Step 2.2: Implement job status tracking (in-memory)
- [x] Step 2.3: Add correlation ID for request tracing
- [x] Step 2.4: Configure structured JSON logging

---

## Phase 3: PDF Generation ✅ COMPLETED
**Goal**: Generate PDF payslips

- [x] Step 3.1: Add OpenPDF/iText dependency
- [x] Step 3.2: Create PDF generator service
- [x] Step 3.3: Design payslip template
- [x] Step 3.4: Store generated PDFs (in-memory)
- [x] Step 3.5: Add download endpoint

---

## Phase 4: Testing ✅ COMPLETED
**Goal**: Write unit tests

- [x] Step 4.1: Add JUnit 5 and Mockito dependencies
- [x] Step 4.2: Write controller tests
- [x] Step 4.3: Write service tests
- [x] Step 4.4: Write PDF generator tests

---

## Phase 5: Docker ✅ COMPLETED
**Goal**: Containerise the application

- [x] Step 5.1: Create Dockerfile
- [x] Step 5.2: Create docker-compose.yml
- [x] Step 5.3: Configure environment variables
- [x] Step 5.4: Build and run Docker image
- [x] Step 5.5: Test API from Docker container

---

## Phase 6: Jemini Client Integration ✅ COMPLETED
**Goal**: Call the microservice from Jemini application

- [x] Step 6.1: Create PayslipServiceClient interface
- [x] Step 6.2: Create RestPayslipClient implementation
- [x] Step 6.3: Create PayslipClientConfig configuration class
- [x] Step 6.4: Create applicationContext-payslip-client.xml
- [x] Step 6.5: Create PayslipAppService service layer
- [x] Step 6.6: Import context in baseApplicationContext.xml
- [x] Step 6.7: Add UI button in Employee Details page
- [x] Step 6.8: Test end-to-end flow

---

## Phase 7: Database Persistence (NEW)
**Goal**: Store payslip records in a database using JPA/Hibernate

### Step 7.1: Add Database Dependencies
Add Spring Data JPA and PostgreSQL driver to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Step 7.2: Configure Database Connection
Create `application.yml` with database settings:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payslip_db
    username: payslip_user
    password: payslip_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Step 7.3: Create JPA Entity
Create `PayslipRecord` entity to store payslip metadata:
- id (UUID)
- jobId
- employeeId
- employeeName
- status (PENDING, PROCESSING, COMPLETED, FAILED)
- createdAt
- completedAt
- pdfData (byte[]) or pdfPath (String)

### Step 7.4: Create Repository
Create `PayslipRecordRepository` extending `JpaRepository`

### Step 7.5: Update Service Layer
Modify `PayslipService` to save/retrieve from database instead of in-memory HashMap

### Step 7.6: Add PostgreSQL to Docker Compose
Update `docker-compose.yml` to include PostgreSQL container

### Step 7.7: Test Database Persistence
Verify records are saved and retrieved correctly

---

## Phase 8: Message Queues (NEW)
**Goal**: Use RabbitMQ for async job processing (like Jemini's ActiveMQ pattern)

### Step 8.1: Add RabbitMQ Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### Step 8.2: Configure RabbitMQ Connection
Add RabbitMQ settings to `application.yml`

### Step 8.3: Create Message Producer
Create `PayslipMessageSender` to send job requests to queue

### Step 8.4: Create Message Consumer
Create `PayslipMessageListener` with `@RabbitListener` to process jobs

### Step 8.5: Add RabbitMQ to Docker Compose
Add RabbitMQ container to `docker-compose.yml`

### Step 8.6: Update Controller
Modify controller to send message to queue instead of calling async processor directly

### Step 8.7: Test Message Flow
Verify messages are sent, received, and processed correctly

---

## Phase 9: Health Checks & Actuator (NEW)
**Goal**: Add production-ready health monitoring

### Step 9.1: Add Actuator Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 9.2: Configure Actuator Endpoints
Enable health, info, metrics endpoints in `application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### Step 9.3: Add Custom Health Indicators
Create custom health check for:
- Database connectivity
- RabbitMQ connectivity
- Disk space for PDF storage

### Step 9.4: Add Application Info
Configure `/actuator/info` with build version and description

### Step 9.5: Test Health Endpoints
Verify all health checks work correctly

---

## Phase 10: Configuration Profiles (NEW)
**Goal**: Manage different environments (dev, docker, prod)

### Step 10.1: Create Profile-Specific Configs
Create separate configuration files:
- `application.yml` (common settings)
- `application-dev.yml` (local development)
- `application-docker.yml` (Docker environment)
- `application-prod.yml` (production settings)

### Step 10.2: Configure Profile-Specific Settings
Different database URLs, API keys, logging levels per profile

### Step 10.3: Activate Profiles
Set active profile via:
- Environment variable: `SPRING_PROFILES_ACTIVE=docker`
- Command line: `--spring.profiles.active=prod`

### Step 10.4: Update Docker Compose
Pass profile as environment variable to container

### Step 10.5: Test Profile Switching
Verify correct config loads for each profile

---

## Phase 11: Retry Logic & Circuit Breakers (NEW)
**Goal**: Handle failures gracefully using Resilience4j

### Step 11.1: Add Resilience4j Dependencies
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

### Step 11.2: Configure Retry Policy
Add retry configuration for transient failures:
```yaml
resilience4j:
  retry:
    instances:
      pdfGeneration:
        maxAttempts: 3
        waitDuration: 1s
        retryExceptions:
          - java.io.IOException
```

### Step 11.3: Add @Retry Annotation
Apply retry logic to PDF generation and external calls

### Step 11.4: Configure Circuit Breaker
Add circuit breaker for external service calls:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      externalApi:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
```

### Step 11.5: Add Fallback Methods
Create fallback responses when circuit is open

### Step 11.6: Test Failure Scenarios
Simulate failures and verify retry/circuit breaker behavior

---

## Phase 12: External API Integration (NEW)
**Goal**: Call external APIs (simulating ATO-like integration)

### Step 12.1: Create External API Client Interface
Define interface for external service calls

### Step 12.2: Implement REST Client
Use `RestTemplate` or `WebClient` to call external APIs

### Step 12.3: Create Mock External Service
Build a simple mock API to simulate government service

### Step 12.4: Add External Service to Docker Compose
Run mock service as separate container

### Step 12.5: Apply Retry and Circuit Breaker
Use Resilience4j patterns from Phase 11

### Step 12.6: Handle API Responses
Parse responses, handle errors, update job status

---

## Phase 13: OAuth2 Authentication (NEW)
**Goal**: Implement token-based authentication for external APIs

### Step 13.1: Understand OAuth2 Flow
Learn Client Credentials Grant flow (service-to-service)

### Step 13.2: Add OAuth2 Client Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### Step 13.3: Configure OAuth2 Client
Add OAuth2 settings for external API:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          external-api:
            client-id: your-client-id
            client-secret: your-client-secret
            authorization-grant-type: client_credentials
        provider:
          external-api:
            token-uri: https://auth.example.com/oauth/token
```

### Step 13.4: Create OAuth2 Token Service
Implement service to obtain and cache access tokens

### Step 13.5: Add Token to External API Calls
Include Bearer token in Authorization header

### Step 13.6: Handle Token Expiry
Implement token refresh logic

---

## Phase 14: Encryption & Security (NEW)
**Goal**: Secure sensitive payroll data

### Step 14.1: Add Encryption Dependencies
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

### Step 14.2: Encrypt Sensitive Fields
Encrypt employee salary data before storing in database

### Step 14.3: Secure Configuration
Use environment variables or secrets manager for credentials

### Step 14.4: Add HTTPS Support
Configure SSL/TLS for production

### Step 14.5: Implement Data Masking
Mask sensitive data in logs (salary, tax details)

### Step 14.6: Security Best Practices
- Never log sensitive data
- Use parameterised queries (JPA handles this)
- Validate all inputs

---

## Phase 15: Metrics & Monitoring (NEW)
**Goal**: Add observability with Prometheus and Micrometer

### Step 15.1: Add Micrometer Dependencies
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Step 15.2: Enable Prometheus Endpoint
Configure `/actuator/prometheus` endpoint

### Step 15.3: Add Custom Metrics
Create custom metrics:
- `payslip_generation_total` (counter)
- `payslip_generation_duration_seconds` (timer)
- `payslip_jobs_in_progress` (gauge)

### Step 15.4: Add Prometheus to Docker Compose
Include Prometheus container with scrape config

### Step 15.5: Add Grafana Dashboard (Optional)
Create dashboard to visualise metrics

### Step 15.6: Test Metrics Collection
Verify metrics are collected and displayed

---

## Phase 16: File Storage (NEW)
**Goal**: Store PDFs in external storage (like Azure Blob)

### Step 16.1: Choose Storage Option
Options:
- Local filesystem (simple)
- MinIO (S3-compatible, good for Docker)
- Azure Blob Storage (production)

### Step 16.2: Add Storage Dependencies
For MinIO/S3:
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

### Step 16.3: Create Storage Service Interface
Define interface for file operations:
- `uploadPdf(jobId, pdfBytes)`
- `downloadPdf(jobId)`
- `deletePdf(jobId)`

### Step 16.4: Implement Storage Service
Create implementation using MinIO client

### Step 16.5: Add MinIO to Docker Compose
Include MinIO container for local development

### Step 16.6: Update Payslip Service
Store PDFs in MinIO instead of memory/database

### Step 16.7: Test File Storage
Verify upload, download, and delete operations

---

## Summary

| Phase | Topic | Status |
|-------|-------|--------|
| 1 | Spring Boot Basics | ✅ Completed |
| 2 | Advanced Spring Boot | ✅ Completed |
| 3 | PDF Generation | ✅ Completed |
| 4 | Testing | ✅ Completed |
| 5 | Docker | ✅ Completed |
| 6 | Jemini Integration | ✅ Completed |
| 7 | Database Persistence | ⬜ Not Started |
| 8 | Message Queues | ⬜ Not Started |
| 9 | Health Checks | ⬜ Not Started |
| 10 | Configuration Profiles | ⬜ Not Started |
| 11 | Retry & Circuit Breakers | ⬜ Not Started |
| 12 | External API Integration | ⬜ Not Started |
| 13 | OAuth2 Authentication | ⬜ Not Started |
| 14 | Encryption & Security | ⬜ Not Started |
| 15 | Metrics & Monitoring | ⬜ Not Started |
| 16 | File Storage | ⬜ Not Started |

---

## Recommended Order

Start with these phases in order as they build on each other:
1. **Phase 7** (Database) - Foundation for persistence
2. **Phase 9** (Health Checks) - Quick win, very useful
3. **Phase 10** (Profiles) - Essential for multi-environment
4. **Phase 8** (Message Queues) - Core async pattern
5. **Phase 11** (Retry/Circuit Breaker) - Resilience
6. **Phase 16** (File Storage) - Replace in-memory PDFs
7. **Phase 12** (External API) - Integration patterns
8. **Phase 13** (OAuth2) - Security for external APIs
9. **Phase 14** (Encryption) - Data security
10. **Phase 15** (Metrics) - Observability

---

*Last Updated: 2026-04-06*