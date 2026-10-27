# ADR 0001 — Branching and release model: develop and main

- Status: accepted
- Date: 2026-10-27
- Issue: #4

## Context

The scoping phase (Bloc 1) planned GitHub Flow: a single `main` branch, deployed after each merge.
The project needs two environments: an acceptance environment where the association tests new features,
and a production environment that only receives validated releases. Each release must be identified by a
fixed version number, and the history must link every change to its GitHub issue.

## Decision

- `develop` is the default branch, deployed to acceptance; `main` is production.
- Every issue starts from `develop` on a short branch and comes back through a pull request merged with
  **squash**: one commit per issue, whose message is the pull request title, a one-line Conventional Commit
  ending with the issue number, for example `feat: submit a review (#15)`.
- A release goes from `develop` to `main` through a pull request merged with a **merge commit**, so `main`
  keeps one commit per issue.
- On `main`, release-please opens a release pull request that sets the version and writes the CHANGELOG
  from these commits. Merging it creates the `vX.Y.Z` tag and the GitHub release.
- A workflow then opens a pull request from `main` back to `develop`, which sets the next development
  version (`X.Y+1.0-SNAPSHOT`). It is merged with a merge commit.
- Both branches are protected by rulesets with no bypass: pull request required, conversations resolved,
  the seven CI checks required, no force push, no deletion. `main` only allows merge commits; `develop`
  allows squash, and merge commits for the pull request coming back from `main`.

## Consequences

- The version in `backend/pom.xml` and `frontend/package.json` always says where the code stands:
  `-SNAPSHOT` on `develop`, the released version on `main` and in the tag.
- `develop` accepts merge commits, so its history is no longer strictly linear. Using a merge commit for
  anything other than the pull request coming back from `main` is a mistake that the rules cannot prevent;
  it is visible in the history and is caught when reviewing the pull request.
- Squashing the pull request back from `main` would make the version line conflict at the next release.
- Release-please and the back-merge workflow use a fine-grained token (`RELEASE_PLEASE_TOKEN`, limited to
  this repository), because pull requests opened with the default Actions token do not trigger the CI
  required to merge them. The token expires and must be renewed.

## Alternatives considered

- **GitHub Flow, as planned in Bloc 1**: simpler, but no acceptance stage separate from production.
- **Squash from `develop` to `main`**: linear history everywhere, but each release would appear as a single
  commit and the CHANGELOG would lose the link with each issue.
- **Release-please on `develop`**: no merge commits at all, but the tag would be created before the release
  reaches production.
