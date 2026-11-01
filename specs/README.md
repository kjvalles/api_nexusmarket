# nexusmarcket — Spec-Driven Development (OmniSpec)

This workspace organizes changes into four archetypes with explicit scope and evidence. Start with omni-init to understand the project, or omni-discuss to explore a question without creating a specification.

## The 4 Engineering Archetypes

| Archetype | Template Path | Purpose | Key Artifacts |
| :--- | :--- | :--- | :--- |
| **01. Feature** | `specs/_templates/01-feature/` | New user capabilities, views, or endpoints. | `spec.md` (Clarification, Non-Goals) + `plan.md` (Contracts, Rollback) + `tasks.md` |
| **02. Bugfix** | `specs/_templates/02-bugfix/` | Incidents, runtime crashes, and defects. | `bug.md` (Reproduction) + `rca.md` (Evidence and hypotheses) + `fix.md` (Scoped fix and regression) |
| **03. Tech Debt**| `specs/_templates/03-tech-debt/` | Refactors, legacy code, outdated packages. | `audit.md` (Impact) + `migration-plan.md` (Proportional migration) + `tasks.md` |
| **04. RFC / ADR** | `specs/_templates/04-rfc-idea/` | Architectural decisions and technical spikes. | `rfc.md` (MADR standard with Decision Drivers and trade-offs) |

---

## Git Workflow for Specifications

1. Read project-context.md and the active manifest before starting or resuming work.
2. Complete the appropriate specification and plan before implementation; use audit for structural checks.
3. Execute scoped tasks and record actual results in verification.md. Use verify then complete to close work.
4. Git branches and commits are optional user-controlled actions. OmniSpec does not create them.
