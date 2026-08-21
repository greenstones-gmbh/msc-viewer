<!--
SPDX-FileCopyrightText: 2025 OpenRail Association AISBL
SPDX-FileCopyrightText: 2026 Greenstones GmbH

SPDX-License-Identifier: CC-BY-4.0
-->

# Contributing

This Project welcomes contributions, suggestions, and feedback. All contributions, suggestions, and feedback you submitted are accepted under the [Project's license](./LICENSE). You represent that if you do not own copyright in the code that you have the authority to submit it under the [Project's license](./LICENSE). All feedback, suggestions, or contributions are not confidential.

The Project is licensed under the [MIT License](./LICENSE), an [Open Source Initiative (OSI)](https://opensource.org/licenses) approved open source license.

## Code of Conduct

Participation in this Project is governed by the [Code of Conduct](./CODE_OF_CONDUCT.md). Decision making and the appeal process are described in [GOVERNANCE.md](./GOVERNANCE.md).

## Development setup

The Project is a Spring Boot backend (Java 17) and a React/TypeScript frontend built with Vite.

Backend:

```bash
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=simple
```

Frontend:

```bash
cd frontend && npm install && npm run dev
```

Both together, or the full build:

```bash
invoke up       # start backend and frontend
invoke build    # build both (also: python tasks.py build)
```

With Docker:

```bash
docker compose up --build
```

## Tests and checks

Run these before opening a pull request:

```bash
cd backend && ./mvnw test                                  # backend test suite
cd backend && ./mvnw test -Dtest=ClassName#methodName      # a single test
cd frontend && npm run lint                                # ESLint
cd frontend && npm run build                               # type-check (tsc -b) and production build
```

The frontend has no automated test suite yet; `npm run build` runs the TypeScript compiler and is the type-safety gate. Contributions that add frontend tests are welcome.

## Submitting changes

1. Open an issue first for anything larger than a bug fix, so the approach can be discussed before you invest time.
2. Fork the repository and create a topic branch off `main`.
3. Keep commits focused and write descriptive commit messages explaining *why* the change is needed.
4. Make sure the checks above pass.
5. Open a pull request describing the change, the motivation, and how you verified it. Link the related issue.
6. Address review feedback by pushing additional commits to the same branch.

Contributions are accepted by consensus of the Maintainers as described in [GOVERNANCE.md](./GOVERNANCE.md).

## Code style

- **Java** — standard Java conventions and Spring Boot best practices, targeting Java 17. Follow the existing module structure under `de.greenstones.gsmr.msc.*`.
- **Error handling** — raise backend errors as [`ApplicationException`](./backend/src/main/java/de/greenstones/gsmr/msc/ApplicationException.java).
- **Schema definitions** — follow the builder pattern shown in [`ConfigTypeBuilder`](./backend/src/main/java/de/greenstones/gsmr/msc/types/ConfigTypeBuilder.java).
- **TypeScript/React** — functional components with hooks, TypeScript interfaces for types, React Bootstrap for styling.
- Match the conventions of the surrounding code rather than introducing new ones.

## Becoming a maintainer

Maintainers are added with the approval of the existing Maintainers, as described in [GOVERNANCE.md](./GOVERNANCE.md#1-roles). A Contributor may be nominated, or may self-nominate by opening an issue, once they have:

1. **A sustained track record of merged contributions.** As a guideline, several non-trivial pull requests merged over at least three months, rather than a single large contribution.
2. **Demonstrated breadth.** Familiarity with more than one area of the Project — for example both the backend configuration model and the frontend, or the AI agent integration and its supporting services.
3. **Shown good review judgment.** Constructive participation in reviewing others' pull requests and in issue discussions.
4. **Committed to the Project's policies.** Willingness to abide by the [Code of Conduct](./CODE_OF_CONDUCT.md) and the governance documents, and to help with the ongoing maintenance work — triage, releases, and security fixes — not only feature development.

Existing Maintainers decide on the nomination by consensus. Accepted Maintainers add themselves to [MAINTAINERS.md](./MAINTAINERS.md). Maintainers who are no longer active may be removed by the same process, and are welcome to return.

---
Based on [GitHub's Minimum Viable Governance (MVG)](https://github.com/github/MVG). Licensed under the [CC-BY 4.0 License](https://creativecommons.org/licenses/by/4.0/).
