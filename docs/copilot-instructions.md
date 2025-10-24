# Copilot / AI agent instructions — poc-back-front (backend-java-reactive)

This file gives targeted, repository-specific guidance so an AI coding agent can be productive quickly in the `beauty-salon-app/backend-java-reactive` module.

Keep instructions concise, factual and only reference discoverable patterns.

- Big picture
  - This module is a Spring Boot 3.5.4 reactive backend (Java 21) using WebFlux + Undertow and Spring Data Cassandra (reactive). Main package: `com.beautysalon.reactive`.
  - Primary components: `controller/` (WebFlux controllers, endpoints under `/api/...`), `service/` (business logic returning Mono/Flux), `repository/` (Spring Data Cassandra reactive repositories), `model/` (Java Records), `config/` (Cassandra, WebFlux, OpenAPI), and `exception/` (global handlers).
  - Main class: `src/main/java/com/beautysalon/reactive/BeautySalonReactiveApplication.java` — annoted with `@EnableReactiveCassandraRepositories`.

- Key integration points
  - Cassandra (reactive) — config: `src/main/java/.../config/CassandraConfig.java` and `src/main/resources/application*.yml`.
  - API docs: SpringDoc OpenAPI (`springdoc-openapi-starter-webflux-ui`) — see `OpenApiConfig.java` and the `springdoc.*` properties in resources.
  - Actuator: `/actuator/health`, `/actuator/metrics` — management endpoints are explicitly exposed in `application.yml` (management base path `/actuator` and `management.server.port` is configured).
  - Docker / compose: multi-stage `Dockerfile` in this module; top-level `docker-compose` in the project can bring up `cassandra` + `backend-java-reactive` with the exact command in the README.

- How to run (verified from repository)
  - Development (local):
    - cd into `backend-java-reactive` and run the Maven wrapper: `./mvnw spring-boot:run`.
    - To run with Cassandra, use the project docker-compose as in the module README: `docker-compose up -d cassandra backend-java-reactive` (runs the module image and Cassandra service).
  - Docker image: module includes a multi-stage `Dockerfile`; the image exposes the app and uses an actuator healthcheck against the management port.

- Testing and CI specifics
  - Unit tests: `./mvnw test` (tests use WebTestClient, StepVerifier and Mockito). `application-test.yml` disables Cassandra auto-configuration so unit tests run isolated (no local Cassandra required).
  - Coverage: `./mvnw test jacoco:report` (JaCoCo configured in `pom.xml`).
  - Integration tests: Testcontainers for Cassandra is included as a test dependency; some integration tests may rely on Testcontainers or the `docker-compose` environment — inspect `src/test/java` for per-test annotations.

- Project-specific conventions and patterns (concrete)
  - Models are implemented as Java `record`s under `model/`. Prefer records when adding small immutable DTO/domain objects.
  - Reactive return types: controllers and services use `Mono<T>` / `Flux<T>` everywhere. Avoid blocking calls; use Reactor operators (`map`, `flatMap`, `filter`, `switchIfEmpty`, etc.).
  - Repository naming: several tests reference repository methods like `findAllByOrderByCreatedAtDesc()` — follow Spring Data method naming conventions for derived queries.
  - Tests: controller tests use `WebTestClient`; service tests use `StepVerifier` to validate flux/mono sequences. Mocks use Mockito strict style.
  - Profiles: `application-test.yml` is used for tests (disables Cassandra autoconfigure). Docker profile overrides in `application-docker.yml` use environment variables like `SPRING_CASSANDRA_CONTACT_POINTS`.

- Files to inspect for context and examples
  - `README.md` (module) — run/test snippets and API summary.
  - `pom.xml` — dependencies (WebFlux, Cassandra reactive, Undertow, testcontainers, jacoco, springdoc).
  - `Dockerfile` — multi-stage build and healthcheck details (actuator health on management port).
  - `src/main/java/com/beautysalon/reactive/config/CassandraConfig.java` and `WebFluxConfig.java` — how Cassandra and CORS/WebFlux are configured.
  - `src/test/java` — canonical test patterns (WebTestClient, StepVerifier, Mockito usage).

- Typical agent tasks and safe actions
  - Implement small controller/service/repository features by following existing patterns in `controller/*`, `service/*`, `repository/*` and model records in `model/*`.
  - When adding endpoints, mirror naming, package structure, and register routes under `/api/{resource}`.
  - For changes that affect DB schema/config, update `CassandraConfig.java` and the related `application-*.yml` files.
  - Run `./mvnw test` after edits. Use `application-test.yml` behavior to keep unit tests isolated from Cassandra.

- Quick troubleshooting hints
  - If tests fail due to Cassandra connectivity, ensure you are running tests (which use `application-test.yml`) not the app; integration tests may need Testcontainers or the `docker-compose` Cassandra instance.
  - Check actuator `/actuator/health` (management port) when container healthcheck fails (Dockerfile healthcheck pings the management port).

If any section above is unclear or you want the same guidance in Portuguese, tell me which part to expand and I'll iterate.
