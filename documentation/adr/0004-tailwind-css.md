# ADR 0004 — Tailwind CSS for the PWA styles

- Status: accepted
- Date: 2026-11-14
- Issue: #5

## Context

The PWA needs a consistent, mobile-first look, with explicit accessibility constraints: RGAA 4.1.2 with a target
of at least 75 %, visible keyboard focus, sufficient contrast. It must also stay light: the stadium page has to
weigh less than 1 MB and load in under 2 seconds on 4G. The scoping phase did not choose a CSS approach.

## Decision

- Styles use **Tailwind CSS 4**, imported in plain CSS (`@import 'tailwindcss'` in `src/styles.css`) through its
  PostCSS plugin, without Sass.
- Components style their templates with utility classes; global rules (colour scheme, base colours) live in a
  `@layer base` block of `src/styles.css`.
- Accessibility patterns are written explicitly with utilities: `sr-only` / `focus:not-sr-only` for the skip link,
  visible focus outlines, colour pairs from the Tailwind palette chosen for contrast.

## Consequences

- Only the classes used in templates end up in the CSS bundle, which keeps the CSS small.
- No component library: every interactive component (rating, dialogs, menus) is built and tested for
  accessibility by the project, with native HTML elements first.
- Templates carry long class lists; repeated patterns are extracted into Angular components rather than custom CSS.
- Contrast and focus are not guaranteed by the framework; they are checked by axe in the smoke test and by the
  RGAA audit.

## Alternatives considered

- **Angular Material**: ready-made accessible components, but a heavier bundle, a Material look to override, and
  styling through Sass themes.
- **Plain CSS or Sass with a home-made design system**: full control, but more CSS to write and maintain for one
  developer.
- **Bootstrap**: familiar, but its JavaScript components overlap with Angular and its CSS is larger.
