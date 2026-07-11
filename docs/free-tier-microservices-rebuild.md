# CodeHub Free-Tier Microservices Rebuild

This rebuild target keeps CodeHub deployable on free-tier infrastructure only:

- Render web services for compute.
- Render Key Value for Redis Streams event transport.
- Supabase Postgres for persistent data.
- GitHub Actions for CI on pull requests.

Verified on 2026-07-12 from official pricing/docs:

- Render pricing lists free web services and a free Render Key Value tier with 25 MB RAM.
- Supabase pricing lists a Free plan suitable for a portfolio-scale Postgres project.
- GitHub Actions is suitable for repository CI; keep secrets out of workflows and use repository/environment secrets.

## Target Topology

```text
Browser / API client
        |
        v
API Gateway  < validates JWT, routes requests
        |
        +--> Auth Service
        +--> Snippet Service
        +--> Notification + Admin Service

Auth Service -------- UserRegistered --------+
Snippet Service ----- SnippetCreated --------+--> Redis Streams --> Notification + Admin Service
Snippet Service ----- SnippetApproved -------+

All services use one Supabase Postgres instance but own separate tables.
```

## Services

### API Gateway

Responsibilities:

- Single public entry point.
- Static routing through environment variables.
- JWT validation at the edge.
- Correlation ID creation/propagation.

Free-tier decision:

- No Eureka or Consul. Four services is small enough for static URLs.

### Auth Service

Owns:

- `users`
- verification tokens
- credentials
- roles
- notification preferences tied to user identity

Endpoints:

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/verify?token=...`
- `GET /auth/me`

Publishes:

- `UserRegistered`

Security:

- BCrypt password hashing.
- JWT access token with role claims.
- In-app rate limiting on register/login/verification endpoints.

### Snippet Service

Owns:

- `snippets`
- `snippet_versions`
- `tags`
- `snippet_tags`
- `comments`
- `bookmarks`

Endpoints:

- `POST /snippets`
- `GET /snippets`
- `GET /snippets/{id}`
- `PUT /snippets/{id}`
- `DELETE /snippets/{id}`
- `POST /snippets/{id}/star`
- `POST /snippets/{id}/bookmark`
- `POST /snippets/{id}/comments`
- `POST /snippets/{id}/versions/{versionId}/revert`
- `GET /tags`
- `GET /users/{username}/profile`
- `GET /share/{shareToken}`

Publishes:

- `SnippetCreated`
- `SnippetApproved`

Security:

- Validates Auth Service JWT locally using the shared JWT secret.
- No service-to-service auth call required for each request.

### Notification + Admin Service

Owns:

- `admin_stats`
- event consumption checkpoints if needed

Consumes:

- `UserRegistered`
- `SnippetCreated`
- `SnippetApproved`

Responsibilities:

- Send verification emails asynchronously.
- Retry failed email sends.
- Admin dashboards.
- User management endpoints.
- Snippet approval statistics.

Security:

- Admin access by `ADMIN` role claim in JWT.
- No hardcoded email checks.

## Event Bus

Use Redis Streams through Render Key Value.

Streams:

- `codehub.events.user`
- `codehub.events.snippet`

Consumer group:

- `notification-admin-service`

Event payload rule:

- IDs and tiny metadata only.
- Do not put snippet code or large user data in events.

Example event:

```json
{
  "eventId": "uuid",
  "eventType": "UserRegistered",
  "occurredAt": "2026-07-12T00:00:00Z",
  "userId": 42,
  "email": "user@example.com"
}
```

## Supabase Database Discipline

Use one Supabase project, but each service only reads/writes its own tables.

Table ownership:

- Auth Service: `users`
- Snippet Service: `snippets`, `snippet_versions`, `tags`, `snippet_tags`, `comments`, `bookmarks`
- Notification + Admin Service: `admin_stats`

Rules:

- No cross-service SQL joins.
- Use REST calls or Redis events for cross-service communication.
- Use explicit column names in entities to avoid Hibernate naming drift.

## CSRF Decision

The microservices rebuild is API-first and JWT-secured.

- CSRF is disabled only for stateless JSON API endpoints.
- Browser clients send JWTs in `Authorization: Bearer ...`, not cookies.
- If cookie-based auth is reintroduced, CSRF must be enabled for browser forms.

## Render Deployment

Create four Render web services:

- `codehub-gateway`
- `codehub-auth`
- `codehub-snippet`
- `codehub-notification-admin`

Create one Render Key Value instance:

- `codehub-events`

Use the existing Supabase Postgres instance.

Known limitation:

- Free Render web services spin down after inactivity, so first request after idle can be slow.

## Environment Variables

Common:

```text
PORT
JWT_SECRET
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
REDIS_URL
CORRELATION_HEADER=X-Correlation-ID
```

Gateway:

```text
AUTH_SERVICE_URL
SNIPPET_SERVICE_URL
NOTIFICATION_ADMIN_SERVICE_URL
```

Auth:

```text
APP_BASE_URL
```

Notification + Admin:

```text
MAIL_USERNAME
MAIL_PASSWORD
MAIL_FROM
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
```

## Local Development

Use local Redis in Docker Compose.

Use either:

- Supabase connection env vars, or
- a local Postgres container with the same schemas.

## Migration Path From Current Monolith

1. Keep the current monolith live on Render.
2. Build Auth Service first and issue compatible JWTs.
3. Build Snippet Service with the same snippet features.
4. Move email verification to Notification + Admin Service through Redis Streams.
5. Add Gateway and route traffic service-by-service.
6. Freeze monolith writes after feature parity.
7. Cut over public Render URL to Gateway.

## Free-Tier Guardrails

- No Kafka.
- No Eureka/Consul.
- No Spring Cloud Config server.
- No Prometheus/Grafana/Zipkin self-hosting.
- No background workers beyond the four web services.
- Keep Redis event payloads small because the free Key Value tier is tiny.
