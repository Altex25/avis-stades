# ADR 0006 — Hosting on Clever Cloud, with continuous deployment to recette

- Status: accepted
- Date: 2026-11-16
- Issue: #6

## Context

The scoping phase chose a French platform as a service: the association has no system administrator, data must
stay in France or the European Union, backups and scaling must be handled by the host, and the budget is small.
The platform is a single Spring Boot jar that serves the PWA (ADR 0002) and needs PostgreSQL 17 with PostGIS
(ADR 0005). Every merge on `develop` must reach the recette environment (ADR 0001) without manual steps.

## Decision

- **Host**: Clever Cloud, Paris region.
- **Application**: Java with Maven build, `nano` instance (512 MiB) at runtime, with a dedicated `S` build
  instance, because the Maven and Angular build does not fit in 512 MiB. Its environment variables:

  | Variable | Value | Purpose |
  | --- | --- | --- |
  | `APP_FOLDER` | `backend` | Maven module of the monorepo |
  | `CC_JAVA_VERSION` | `25` | Java version of the project |
  | `CC_MAVEN_PROFILES` | `frontend` | builds the PWA into the jar (ADR 0002) |
  | `CC_MAVEN_BUILD_GOAL` | `package -DskipTests` | tests already ran in CI; they need Docker |
  | `CC_RUN_COMMAND` | `java -XX:MaxRAMPercentage=70 -jar ${APP_HOME}/backend/target/avis-stades.jar` | starts the jar, not Maven |
  | `CC_HEALTH_CHECK_PATH` | `/actuator/health/readiness` | deployment succeeds only when the application is ready |
  | `SPRING_PROFILES_ACTIVE` | `recette` | reads the `POSTGRESQL_ADDON_*` variables |

- **Database**: PostgreSQL 17 add-on, plan XXS Tiny, linked to the application, which injects the connection
  variables. PostGIS is one of the default extensions; the first Flyway migration enables it. The connection
  requires TLS (`sslmode=require`).
- **Continuous deployment** (`.github/workflows/deploy-recette.yml`): when the CI workflow succeeds on a push to
  `develop`, the job checks out that commit, pushes it with Clever Tools, waits for the readiness probe, then runs
  the Playwright smoke test against the public recette URL. A commit that is no longer the tip of `develop` is
  not deployed, so a late CI run cannot roll recette back.
- **Credentials**: `CLEVER_TOKEN` and `CLEVER_SECRET` are secrets of the GitHub environment `recette`, which only
  accepts deployments from `develop`; `CLEVER_APP_ID` and `RECETTE_URL` are variables of that environment. The
  secrets are given to the single step that calls Clever Tools, never to the steps that install or run npm
  packages. Clever Tools is pinned with a lockfile in `.github/deploy`, installed without install scripts, and
  updated by Dependabot. These tokens give access to the whole Clever Cloud account: production (T07) goes to a
  separate organisation, with its own deployment credentials.
- **Sobriety** (`.github/workflows/recette-power.yml`): recette is stopped every evening at 19:00 UTC and started
  again by any deployment or by running the workflow manually with `start`.

## Consequences

- No server to administer; deployment, restart and rollback go through Clever Cloud and GitHub.
- Clever Cloud rebuilds the jar from the deployed commit instead of reusing the CI artifact: same sources and
  pinned versions, not the same binary as the one tested and analysed in CI. The smoke test against recette, run
  after every deployment, is the compensating control.
- Estimated monthly cost of recette, excluding tax (application instances are billed per second; prices of the
  public Clever Cloud API, November 2026): about 2 € for the `nano` instance running about 10 hours on working
  days (6 € if it ran all month), a few cents per build on the `S` instance, plus the PostgreSQL plan (about 5 €,
  to be confirmed on the first invoice). Total: under 10 € per month.
- The add-on provides a single database role that owns the database, used both by Flyway and by the application.
  Separating a migration role from a role limited to data access is left to production (T07).
- TLS ends at the Clever Cloud proxy; the application trusts the `X-Forwarded-*` headers
  (`server.forward-headers-strategy=framework`) so that HSTS and `Secure` cookies apply. This is safe only if the
  proxy replaces these headers instead of appending to what the client sends; it must be checked on the deployed
  recette. Until then, links sent by e-mail are built from a configured base URL, never from the request, and
  rate limiting does not rely on a client-supplied address.
- Deployment logs are kept quiet in the public GitHub Actions logs; application logs must never contain personal
  data or secrets.
- Recette is public. Before accounts exist (US01), it gets a `noindex` header and a visible "test environment"
  banner, so that nobody enters real personal data there.
- When recette is stopped, its URL answers with an error page from Clever Cloud until it is started again.

## Alternatives considered

- **VPS in France with Docker Compose** (plan B of the scoping phase): cheaper at constant load, but the system,
  backups, TLS certificates and updates would be the association's responsibility.
- **Container deployment (Docker application)**: deploys the image built in CI, but requires a Dockerfile and an
  image registry, which ADR 0002 avoided.
- **Recette always on**: simpler, but runs for nothing outside working hours, against the scoping's sobriety
  requirement (recette switched off outside working hours).
