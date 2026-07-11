# CodeHub Production And Microservices Roadmap

This roadmap keeps the current Render-friendly monolith stable while moving CodeHub toward a production-grade microservices architecture.

## Phase 1: Harden The Monolith

- Move privileged admin identity into configuration with `APP_ADMIN_EMAIL`.
- Add account lockout after repeated failed login attempts.
- Add rate limiting to login, registration, verification resend, and email-check endpoints.
- Add audit fields, database indexes, and soft deletes for snippets.
- Add centralized exception handling, correlation IDs, and structured request logging.
- Add Spring Boot Actuator endpoints for health, metrics, and Prometheus scraping.
- Add retry logic for verification email delivery.
- Keep server-side Thymeleaf sessions for the web UI; introduce JWT for API clients in a later phase.

## Phase 2: API Boundary Inside The Monolith

- Add `/api/auth/**` endpoints that issue and refresh JWTs.
- Add `/api/snippets/**` endpoints that use JWT stateless authentication.
- Keep existing Thymeleaf pages on form login until the frontend is split.
- Add service interfaces and DTOs matching future service contracts.
- Add integration tests around auth, snippet ownership, email verification, and admin restrictions.

## Phase 3: Extract Notification Service

- Publish `UserRegistered` events from the monolith.
- Consume events in a new Notification Service.
- Send verification email asynchronously.
- Use Kafka locally with Docker Compose.
- Add retry/dead-letter handling for email failures.

## Phase 4: Extract Auth Service

- Move users, credentials, verification tokens, roles, lockout state, and JWT issuance into Auth Service.
- Give Auth Service its own database.
- Replace direct user table access in the monolith with Auth Service calls or token claims.
- Add API Gateway token validation.

## Phase 5: Extract Snippet Service

- Move snippets, tags, versions, collections, comments, bookmarks, and visibility into Snippet Service.
- Use database-per-service.
- Replace direct joins with API calls and domain events.
- Add Redis caching for frequent reads and search results.

## Phase 6: Extract Admin/Analytics Service

- Build dashboards from event streams and service APIs.
- Add user statistics, snippet approval workflow, activity metrics, and moderation tools.
- Keep admin access role-based and backed by Auth Service claims.

## Target Services

- Auth Service: registration, login, JWT issuance/refresh, email verification, roles, account lockout.
- Snippet Service: CRUD, search, tags, collections, visibility, comments, bookmarks, versioning.
- Notification Service: email and alerts from events.
- Admin/Analytics Service: dashboards, approvals, statistics.
- API Gateway: routing, token validation, rate limiting, request correlation.

## Supporting Infrastructure

- Spring Cloud Gateway for API routing.
- Eureka or Consul for service discovery.
- Spring Cloud Config for centralized configuration.
- Kafka for events.
- Redis for caching and distributed rate limits.
- Resilience4j for circuit breakers and retries.
- OpenTelemetry plus Zipkin/Tempo for tracing.
- Prometheus and Grafana for metrics.
- Docker Compose for local development, Kubernetes later when traffic justifies it.
