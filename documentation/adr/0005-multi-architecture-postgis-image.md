# ADR 0005 — Multi-architecture PostGIS image

- Status: accepted
- Date: 2026-11-14
- Issue: #5

## Context

The database is PostgreSQL with the PostGIS extension (scoping decision). The local environment (Docker Compose)
and the integration tests (Testcontainers) both start a PostGIS container. The developer works on an Apple
Silicon computer (arm64), while GitHub Actions runners are amd64. The official `postgis/postgis` images are only
published for amd64: on arm64 they run under emulation, slowly and unreliably, or not at all.

## Decision

- Both Docker Compose and Testcontainers use **`imresamu/postgis:17-3.6-alpine`**: PostgreSQL 17 with PostGIS 3.6,
  published for amd64 and arm64 by a maintainer of the official docker-postgis project, built from the same
  sources.
- Testcontainers declares it as a compatible substitute for the `postgres` image, so Spring Boot's
  `@ServiceConnection` configures the datasource without extra code.
- The tag is pinned to a PostgreSQL and PostGIS version; it is updated deliberately, together with the production
  database version.

## Consequences

- Same database image on the developer's computer and in CI, running natively on both architectures.
- The image comes from an individual maintainer rather than the official organisation; this dependency is
  accepted because the images are built from the official sources, and it is reviewed when PostgreSQL is upgraded.
- PostgreSQL 17 must also be available at the production host; this is checked when the recette environment is
  deployed (T06).

## Alternatives considered

- **Official `postgis/postgis` image**: amd64 only, unusable natively on the developer's computer.
- **Building a PostGIS image in the project**: full control, but an image to build, publish and keep up to date.
- **PostgreSQL without PostGIS in development**: diverges from production and hides spatial queries from tests.
