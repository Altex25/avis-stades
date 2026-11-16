# ADR 0003 — Garage for local S3 object storage

- Status: accepted
- Date: 2026-11-14
- Issue: #5

## Context

Review photos are stored in an S3-compatible object store, accessed through signed URLs (scoping decision). In
production the store is provided by the host. On the developer's computer and in tests, the application needs an
S3-compatible service that starts with Docker Compose, uses little memory and can be configured by a script.
MinIO was the usual choice. In 2025 its community edition lost its administration console, and official
community builds and images were no longer published, which makes it a poor base for a project meant to be
maintained by a third party.

## Decision

- Local development uses **Garage** (`dxflrs/garage`, version pinned in `infra/docker-compose.yml`), a lightweight
  S3-compatible store designed for self-hosting and maintained by Deuxfleurs, a French association.
- The `garage-init` container configures it on start through its administration API: storage layout, access key
  and photo bucket. It is idempotent.
- The application only uses the S3 API (`app.storage.s3.*` properties, path-style access), never anything
  specific to Garage, so the production store can be any S3-compatible service.

## Consequences

- One command starts the whole local environment; Garage needs little memory next to PostgreSQL and Mailpit.
- Garage implements the subset of S3 that the platform needs (objects, buckets, signed URLs) but not every S3
  feature; anything beyond that subset must be checked against both Garage and the production store.
- Garage has no web console; the bucket and key are created by script, which is also how they are documented.

## Alternatives considered

- **MinIO**: widely known, but its community edition is no longer a stable base (see Context).
- **LocalStack**: emulates many AWS services; heavier, and more than an S3 store is not needed.
- **Real cloud bucket for development**: costs, credentials on every computer, and no offline work.
