# Implementation Plan: Custom Index Listing & Pricing Suite

This document outlines the step-by-step implementation strategy for the Custom Index Listing & Pricing Suite, based on the [Technical Blueprint](./custom-index-blueprint.md).

## Phase 1: Foundation & Shared Infrastructure
**Goal:** Establish the multi-module project structure and common libraries.

1.  **Project Initialization:**
    *   Create a Maven/Gradle multi-module project with Java 21 [[memory:8371213]].
    *   Setup root `pom.xml` / `build.gradle` with BOMs for Spring Boot (LTS), Spring Cloud, and Spring Cloud Stream.
    *   Configure Lombok and MapStruct for boilerplate reduction [[memory:8371207]].
2.  **Shared Module (`cib-shared-infra`):**
    *   Implement common Error Model (problem-json).
    *   Define the Event Envelope (traceId, idempotencyKey, payload).
    *   Setup Messaging Abstraction using Spring Cloud Stream with Kafka (testing) / Solace (enterprise) profiles.
    *   Implement a **Pluggable Caching Abstraction Layer** (using Spring Cache or custom interfaces) to allow switching between Redis, Hazelcast, or In-memory implementations without changing business logic.
    *   Integrate Protobuf for inter-service message serialization and gRPC support where latency is critical [[memory:6883576]].
    *   Add common logging and OTel tracing configuration.
3.  **Local Development Environment:**
    *   Create a `docker-compose.yml` with:
        *   MSSQL / Postgres (containers) [[memory:8058548]].
    *   Apache Kafka (for testing event streaming).
    *   Redis (for caching).
4.  **End-to-End Testing:**
    *   Create comprehensive test scripts (`scripts/e2e-event-driven.sh`) to verify the full lifecycle:
        - Create -> Add Constituents -> List (REST) -> [Auto Listing -> Auto Pricing -> Auto Publishing] (Events).
        - Verify asynchronous state transitions and event propagation via Kafka topics.

## Phase 2: Core Domain & Basket Management
**Goal:** Implement the primary lifecycle management service.

1.  **Database Migration:**
    *   Setup Flyway/Liquibase scripts for `basket`, `constituent`, `provider_status`, and `audit_event` tables.
2.  **Basket Management Service (`cib-basket-service`):**
    *   Implement JPA Entities and Repositories.
    *   Expose OpenAPI-compliant REST endpoints for CRUD and lifecycle transitions (Draft -> Validated -> Approved).
    *   Implement state machine logic for the Basket Lifecycle.
3.  **Lifecycle Events:**
    *   Implement event publishers for `basket.listed`, `basket.ready_for_pricing`, etc.
    *   Implement Audit logging via JPA Listeners or AOP.

## Phase 3: Integration Services (Data Adapters)
**Goal:** Provide the necessary validation and pricing data sources.

1.  **Reference Data Service (`cib-ref-data-service`):**
    *   Implement instrument validation logic (eligibility, existence).
    *   Setup Redis cache for golden-source data.
2.  **Market Data Service (`cib-market-data-service`):**
    *   Implement adapters for price/FX fetching.
    *   Enforce staleness/SLA checks.
    *   Setup caching strategy.

## Phase 4: Workflow Services (Listing, Pricing, Publishing)
**Goal:** Implement the automated workflow components.

1.  **Listing Service (`cib-listing-service`):**
    *   Implement fan-out logic for multiple providers (Bloomberg, Refinitiv, etc.).
    *   Handle `provider.listing.requested` events.
2.  **Pricing Service (`cib-pricing-service`):**
    *   Implement pricing engines per asset class.
    *   Integrate **Data Quality Checks**:
        *   Staleness and variance detection.
        *   Zero/negative price guards.
        *   FX rate availability validation.
    *   Consume `basket.ready_for_pricing` and emit `basket.priced`.
3.  **Publishing Service (`cib-publishing-service`):**
    *   Implement provider-specific publishing (FIX, EMA).
    *   Implement **Publishing Integrity Checks**:
        *   Verify all constituents are priced.
        *   Final NAV sanity checks before outbound delivery.
    *   Ensure ordered/idempotent delivery.

## Phase 5: Observability, Security & Hardening
**Goal:** Ensure production readiness.

1.  **Security:**
    *   Integrate Spring Security with OIDC/JWT.
    *   Implement Role-Based Access Control (RBAC) for API endpoints.
2.  **Observability:**
    *   Configure Micrometer for custom metrics.
    *   Setup Prometheus scraping and Grafana dashboards.
    *   Implement Health Checks (`/health/liveness`, `/health/readiness`).
3.  **Resiliency:**
    *   Configure Dead Letter Queues (DLQ) and retry mechanisms in Spring Cloud Stream.
    *   Implement Circuit Breakers (Resilience4j) for external API calls.

## Phase 6: Deployment & CI/CD
**Goal:** Automate builds and deployments.

1.  **Containerization:**
    *   Configure Jib or Buildpacks for generating RHEL-based images.
2.  **CI/CD Pipeline:**
    *   Setup build pipelines (GitHub Actions/GitLab CI) for automated testing and image pushing.
3.  **Infrastructure as Code:**
    *   (Optional) Helm charts or K8s manifests for orchestration.

---
**Status Tracking:**
- [x] Phase 1: Foundation & Shared Infra
- [x] Phase 2: Core Domain & Basket Management
- [x] Phase 3: Integration Services
- [x] Phase 4: Workflow Services
- [x] Phase 5: Observability & Security
- [x] Phase 6: Deployment & CI/CD
