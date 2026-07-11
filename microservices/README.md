# CodeHub Microservices Rebuild

This directory is reserved for the free-tier microservices rebuild of CodeHub.

Current production deployment is still the Spring Boot monolith at the repository root. Keep it stable while building this rewrite service by service.

Target services:

- `gateway`
- `auth-service`
- `snippet-service`
- `notification-admin-service`

Supporting local/deployment files:

- `../docs/free-tier-microservices-rebuild.md`
- `../docker-compose.microservices.yml`
- `../render.microservices.yaml`

Recommended implementation order:

1. Auth Service
2. Snippet Service
3. Notification + Admin Service
4. API Gateway
5. Cutover from monolith to Gateway after feature parity

Do not add paid infrastructure dependencies. The target is Render free web services, Render free Key Value, Supabase Free, and GitHub Actions.
