# ADR 0002 — Single deployable: the API serves the PWA

- Status: accepted
- Date: 2026-11-14
- Issue: #5

## Context

The platform has two parts: a Spring Boot API and an Angular PWA. The scoping phase (Bloc 1) considered a PWA
with server-side rendering (SSR) running next to the API. The project has about ten hours of work per week, a
single developer, and a client association with no system administrator. The architecture also relies on
session cookies with CSRF protection, which work best when the PWA and the API share the same origin.

## Decision

- The API serves the compiled PWA from its own classpath (`static/`): one Spring Boot jar, one process, one origin.
- The Maven profile `frontend` builds the PWA during the backend build. `frontend-maven-plugin` installs the Node
  version given in the pom (aligned with `.nvmrc`), runs `npm ci` and `npm run build`, and the resources plugin
  copies `frontend/dist/avis-stades/browser` into the jar. `./mvnw -Pfrontend package` builds the deliverable.
- Without the profile, `./mvnw verify` builds and tests the API alone, so the backend CI stays fast.
- The API is under `/api`, the health probe under `/actuator/health`. Any other path without a file extension is
  forwarded to `index.html`, so a deep link or a page refresh opens the right PWA route.
- Server-side rendering is postponed.

## Consequences

- One artifact to build, version, deploy and roll back; it matches a Java application on Clever Cloud.
- Same origin: the session cookie stays `HttpOnly` and `SameSite`, the CSRF token is read by Angular from the
  `XSRF-TOKEN` cookie, and no CORS configuration is needed.
- The PWA cannot be deployed alone; a front-end change ships with an API release.
- The packaging build downloads Node and runs npm, which makes it slower than the API build; CI runs it once,
  in the smoke test job.
- `frontend-maven-plugin` downloads Node over HTTPS from nodejs.org at a pinned version but does not verify its
  checksum. This residual supply-chain risk is accepted for now; checking the archive against the signed
  `SHASUMS256.txt` of the release can be added to the pipeline later.
- Without SSR, search engines and link previews only see the static `index.html`. The impact is limited for a
  platform whose audience comes from an association, and the trade-off is discussed in Bloc 3.

## Alternatives considered

- **PWA and API deployed separately (with or without SSR)**: two hosts to run and secure, cross-origin cookies
  and CORS, twice the deployment work for one developer.
- **Docker multi-stage image**: same result, but it requires Docker to produce the deliverable and a Docker
  runtime at the host.
- **CI copies the PWA into the backend before packaging**: simplest pipeline, but the deliverable cannot be
  reproduced with one local command.
