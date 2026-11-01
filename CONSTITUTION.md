# CONSTITUTION — nexusmarcket

> This file acts as the supreme engineering law for developers and AI coding agents. Every specification, architecture plan, and line of code must strictly adhere to these guidelines before merging.

---

## 1. Governance & Code Surgery (Constitutional Foundation)

1. **Pre-Execution Diagnostic:** Before making any non-trivial change, present a structured assessment: *Problem, Proposed Solution, and Consequences/Risks*.
2. **Strict Non-Destructive Policy:** Destructive changes, large refactors, or database modifications require explicit approval.
3. **Evidence and uncertainty:** Distinguish verified facts, hypotheses and unresolved decisions. Inspect repository evidence first; ask the user about material ambiguity. Never invent business rules or claim certainty from a structural audit.
4. **Minimal Touch Surgery:** Modify exclusively the files necessary to achieve the verified objective. Do not touch adjacent working code or reformat unrelated modules.
5. **Demonstrable Quality Gates:** Record actual results and pre-existing failures. Automated gates must pass for automated verification; when none apply, document explicitly labeled manual evidence. A checklist is not proof of behavior.

---

## 2. Domain & Regulatory Invariants (General software project)

### 2.1 Applicable security boundaries
* Review what data and capabilities this project actually exposes. If authorization applies, enforce it at the trusted boundary and test unauthorized access.
* If the project is multi-tenant, document and test tenant isolation; do not introduce tenancy into a single-user tool.
* Avoid exposing secrets or sensitive data in logs and outputs.
### 2.2 Changes and recovery
* If persistence exists, preserve its documented integrity constraints and concurrency behavior.
* For migrations, choose and test a recovery strategy appropriate to the data and deployment; a down.sql file is not universally appropriate.
* If no database, UI or remote service exists, record N/A rather than creating one to satisfy a template.

These profile controls are review prompts; applicability must be established for this project. They do not certify regulatory compliance.

---

## 3. Technology Stack & Documentation Standards

* **Primary Tech Stack:** `Monorepo: Java / Maven`
* **Documentation Standard:** `JavaDoc` (All public APIs, exported interfaces, and complex functions must follow this convention).

---

## 4. Delivery Quality Gates

Configured commands, executed from the project root by verify. Review commands before execution.

```text
docker compose exec -T nexusmarcket-app mvn test
```

## 5. UI Ergonomics & Anti-Slop (Frontend Only)

* **Zero Visual Slop:** Avoid generic AI-generated templates, excessive card-nesting (card-itis), and ungrounded gradients.
* **4 Mandatory Interface States:** Loading (skeleton/spinner), Empty (clear explanation), Error (with retry trigger), and Protected/Restricted.
* **Accessibility:** Full keyboard navigation (`Tab`, `Enter`, `Escape`) and WCAG AA contrast compliance.
