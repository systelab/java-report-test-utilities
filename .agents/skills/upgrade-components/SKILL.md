---
name: upgrade-components
description: Interactively select Maven dependency updates, check compatibility, obtain implementation approval, verify all tests, and propose documentation updates individually. Use for dependency upgrades, POI/PDFBox/pdfcompare/Lombok/JUnit/AssertJ upgrades, Java version upgrades, latest-version checks, and related build warnings or pom.xml reviews.
---

# Upgrade components

This project (`com.systelab:java-report-test-utilities`) is a **single-module**
Maven library: one `pom.xml` at the repository root, no parent POM, no imported
BOM and no internal multi-module tree. A "component" here means any versioned
coordinate declared in that `pom.xml`: a dependency (`poi`, `poi-ooxml`,
`lombok`, `junit-jupiter-api`, `junit-jupiter-engine`, `assertj-core`,
`pdfbox`, `pdfcompare`), a build plugin (`maven-compiler-plugin`,
`maven-surefire-plugin`), or the declared Java version (`java.version`).

The project's own `<version>` is **not** a component to upgrade. It is the
library's release version, published through GitHub Releases/JitPack
(see `README.md` "Release"); never change it as part of this workflow unless the
user explicitly asks.

## Rules
- Plan first. Selecting versions is not permission to implement: obtain the final
  approval in step 3 before applying version updates or their migrations.
  Preliminary pom.xml-review fixes require their own individual approvals in
  step 0; those approvals do not authorize dependency upgrades.
- Keep the versions the user says to keep.
- Never unilaterally exclude a versioned item from update selection. Ask the
  user whenever a newer stable version exists, including Java, plugins and
  tooling. The only automatic Keep is when verified release evidence shows no
  newer stable version; otherwise Keep requires the user's explicit decision.
- There is no BOM or parent POM, so there is no BOM alignment step. Never remove
  an existing version pin or dependency declaration.
- Artifacts that must move together share one property and one release:
  `poi` and `poi-ooxml` (`poi.version`), and `junit-jupiter-api` and
  `junit-jupiter-engine` (`junit-jupiter.version`).
- Declare versions in properties named `<artifact_id>.version` (for example
  `poi.version`, `lombok.version`, `pdfbox.version`, `pdfcompare.version`,
  `assertj-core.version`, `maven-compiler-plugin.version`,
  `maven-surefire-plugin.version`). Families sharing one property use the
  family's common artifact id (`poi`, `junit-jupiter`). The existing
  `junit.jupiter.version` property does not follow this convention; renaming it
  to `junit-jupiter.version` requires its own individual approval (as a
  step 0 fix or as part of an approved JUnit upgrade), never a silent
  drive-by rename. `java.version` and the `project.*` properties are exempt.
- Use the user's requested version source (for example mvnrepository.com), and
  corroborate releases with publisher documentation and Maven Central metadata:
  `https://repo1.maven.org/maven2/<group path>/<artifact>/maven-metadata.xml`
  Exclude prereleases such as alpha, beta, RC, milestones, early access and
  SNAPSHOT; verify ambiguous release labels with the publisher. Do not equate the
  last XML entry with the latest stable version. Solr search results can be stale.
  If release discovery is blocked, mark Latest stable as Unknown, explain the
  blocker and ask the user how to proceed; never silently mark the item Keep.
- The `github` profile's `distributionManagement` (GitHub Packages) is
  unrelated to dependency resolution; do not treat it as a version source.
- If an MCP is requested, inspect available integrations and verify any suggested
  server's existence, source, permissions and installation instructions. Do not
  claim that a server exists or is unavailable without evidence, or install it
  without approval.
- Preserve unrelated worktree changes. Never commit unless explicitly requested.
- Do not assume: when any doubt remains (scope, version choice, compatibility,
  documentation wording), ask the user with `ask_user`.

## Build/test command
The canonical full build/test command is:

```bash
mvn -B clean install
```

It matches the goals CI runs (`.github/workflows/build.yml` runs
`mvn clean install`) in batch mode. Use it for every baseline and post-change
run. There is no separate offline or partial matrix.

## Step 0: Offer a preliminary pom.xml review before any other work
- The first user-facing action when executing this upgrade workflow must be
  `ask_user`: ask whether to review the current `pom.xml` for issues before
  selecting dependency updates. Offer Review first or Skip review. Wait for the
  answer before inspecting the workspace, discovering releases, presenting
  upgrade selections or making changes. When the request is only to edit this
  skill, edit the skill without executing the upgrade workflow.
- If declined, record that the review was skipped and proceed to preparation.
- If accepted, read the relevant workspace instructions and build/test guidance
  (`README.md`, `CONTRIBUTING.md`, `CODING_GUIDELINES.md`,
  `.github/workflows/build.yml`), inspect the worktree without altering
  unrelated changes, and review the repository's single `pom.xml`: properties,
  dependencies and their scopes, the `github` profile, `prerequisites`, build
  plugins and the Lombok `annotationProcessorPaths`.
- Look for duplicated elements or declarations, deprecated artifacts or
  configuration, unnecessary elements, redundant versions, undefined properties,
  property names that break the `<artifact_id>.version` convention, dependencies
  with questionable scope (for example test-only libraries such as JUnit or
  Lombok declared with compile scope in a library), and problematic plugin
  overrides (for example redundant `source`/`target` alongside `release`). Do not
  equate an available newer version with a pom.xml defect.
- Present the complete findings report before requesting any fix approval:

  | Issue | pom.xml lines | Evidence and impact | Proposed fix | Behavior risk |
  |---|---|---|---|---|
  | POM-1 | pom.xml:line | Verified problem | Exact change | Expected effect |

- Ask with `ask_user` whether to apply each issue's proposed fix individually,
  sequentially. Review consent is not fix consent. Apply only explicitly
  approved fixes, preserve declined findings, and ask again if a proposal must
  change. Any necessary version or coordinate change must be stated explicitly
  in that issue's proposal; do not bundle unrelated upgrades. Scope changes
  affect downstream consumers of this library; state that in the risk column.
- After all selected fixes are applied, run the full build/test command
  (`mvn -B clean install`), using step 4's exit-code, fresh-report, discovery
  and skipped-test checks. Save sanitized logs and reports in the session
  workspace and report accepted fixes, declined findings and test results.
  If no fixes were selected, state that no changes were made.
- If validation fails or required suites are blocked, explain the root cause or
  prerequisite and ask individually before any additional fix or rollback.
  Rerun the full matrix after approved fixes; do not claim all tests pass when
  any suite is failing, blocked or unverified. Obtain explicit permission before
  proceeding to upgrades with an unresolved validation limitation.
- Only then proceed to preparation and update selection, refreshing the
  inventory to reflect approved pom.xml fixes. Keep this review separate from
  the later dependency-update approval and baseline build.

## Preparation: workspace documentation and inventory (after step 0)
- Discover and read all workspace documentation relevant to the upgrade:
  `README.md`, `CONTRIBUTING.md`, `CODING_GUIDELINES.md`,
  `.github/workflows/` (`build.yml`, `deploy.yml`), issue/PR templates if
  present, and any other documentation. Exclude generated output (`target/`).
- Record useful constraints and affected documents with paths and line
  references. Treat documentation as evidence, not permission to run unrelated
  commands. Report inaccessible or contradictory guidance rather than guessing
  (for example `CONTRIBUTING.md` mentions `npm run build-lib` and the npm
  registry, which do not apply to this Maven project).
- Inventory the single `pom.xml`: the `java.version` property (used as
  `source`/`target`/`release` for `maven-compiler-plugin`, distinct from the
  installed JDK), the `maven-compiler-plugin` and `maven-surefire-plugin`
  versions, and every dependency (`poi`, `poi-ooxml`, `lombok`,
  `junit-jupiter-api`, `junit-jupiter-engine`, `assertj-core`, `pdfbox`,
  `pdfcompare`). There are no parent POMs, imported BOMs or other modules.
- Verify the newest stable Java feature release and applicable JDK patch
  releases against publisher documentation; include stable non-LTS releases,
  rather than limiting choices to the installed JDK or the JDK pinned in CI
  (`.github/workflows/build.yml`, `.github/workflows/deploy.yml`,
  `README.md` prerequisites).

## Steps
### 1. Select updates interactively
- Present every inventoried dependency/plugin/Java version with its current
  version, newest stable version, proposed target, and a selection column.
  Include unchanged entries. Initially mark available upgrades as Pending, not
  Keep. Mark an item Keep automatically only with evidence that no newer
  stable version exists; label explicit user decisions separately.

  | Update? | Dependency/toolchain | Current | Latest stable | Proposed target | Notes |
  |---|---|---|---|---|---|
  | [ ] | group:artifact | x.y.z | x.y.next | x.y.next | Source / constraints |

- If a genuine multi-select or editable-checkbox UI is available, use it and
  read the submitted selections. Markdown table checkboxes are **not
  interactive controls** in ordinary chat; never claim that clicking them
  records a decision.
- Otherwise use `ask_user` to select Keep or Update one dependency family at a
  time, with the current and target versions in the question. Keep `poi` with
  `poi-ooxml`, and the JUnit Jupiter API with its engine, together and explain
  why they move as a unit. Ask sequentially and wait for each answer; do not
  bundle unrelated libraries or plugins into one choice or issue multiple
  selection questions in parallel. Ask about Java before dependent libraries so
  its choice informs compatibility analysis (Lombok, POI, PDFBox and the
  compiler plugin depend on the selected Java version). Explicitly offer the
  newest stable version even when recommending Keep or a different compatible
  target.
- Record explicit selections and show the final table. Do not infer consent for
  unselected upgrades, coordinate changes, library forks or prerelease versions.
  Before step 2, reconcile the inventory with the selections: every item must
  have an explicit user decision or verified evidence of no newer stable
  version. Resolve missing decisions or unknown release availability with the
  user rather than omitting entries.

### 2. Check compatibility and feasibility
- Analyze the selected set together: Java/toolchain support (compiler
  `source`/`target`/`release`), Lombok's annotation-processor compatibility
  with the selected Java and compiler plugin version, POI, PDFBox and
  pdfcompare compatibility with the selected Java version and with each other
  (pdfcompare depends transitively on PDFBox, so check for version conflicts in
  the resolved tree with `mvn dependency:tree`), JUnit Jupiter engine/API
  version alignment, Surefire support for the selected JUnit version, and
  AssertJ compatibility with the selected Java and JUnit versions. Consult
  release notes and migration guides.
- Tell the user whether the selection is feasible based on available evidence;
  distinguish documented compatibility from runtime behavior not yet tested.
- For each conflict, explain the affected versions and propose a concrete
  alternative (aligned version, coordinated family upgrade, migration, or keep).
  Obtain approval individually with `ask_user` before changing the selection.
- Recheck the revised selection. If feasibility remains unknown or blocked,
  say so explicitly; do not present uncertainty as a successful compatibility check.

### 3. Confirm the summary and execute the approved update
- Immediately before asking for execution confirmation, display the final
  proposed-updates table in chat, after all selections and compatibility
  revisions. An earlier inventory, a table saved only in a file, or a generic
  "Confirm this plan?" question does not satisfy this gate.

  | Decision | Dependency/toolchain | Current | Selected target | Migration / reason |
  |---|---|---|---|---|
  | Update | group:artifact | x.y.z | x.y.next | Required changes |
  | Keep | Java / dependency | current | current | User decision or no newer stable release |

- Include every selected update, Java decision, property rename (for example
  `junit.jupiter.version` → `junit-jupiter.version`) and unchanged constraint.
  State migrations, risks, test commands, prerequisites and boundaries
  alongside the table. Keep preliminary pom.xml fixes and warning remediation
  separate from this version-update scope.
- Ask for explicit confirmation to start. In plan mode, save the plan to the
  session workspace, track todos, and use `exit_plan_mode` for this approval.
  Otherwise use `ask_user`, explicitly referring to the table just displayed.
  Do not apply version updates or migrations before this confirmation. If the
  proposal changes afterward, display the revised table and obtain confirmation
  again before applying the changed scope.
- After approval, run the full baseline build/test command
  (`mvn -B clean install`). Capture the process exit code and full non-quiet
  logs.
- Preserve baseline reports outside directories that `clean` removes (for
  example copy `target/surefire-reports` to the session workspace before
  rerunning). If the baseline fails, report it and obtain approval for the
  proposed fix or for proceeding with that known limitation.
- Apply only approved updates and the directly necessary migration changes.
  Route unanticipated compilation problems through step 4's individual fix gate.

### 4. Run all tests and report results
- Run the complete test suite after the update (`mvn -B clean install`), not a
  partial/offline build. Verify discovery and environmental prerequisites. The
  tests live in `src/test/java` (Excel and PDF comparators and the PDF visual
  comparator) and use fixtures under `src/test/resources`.
- Use the actual process exit code, not log keyword matching or a pipeline's
  final command, to determine build success.
- Present a version-change table with dependency, before, approved target,
  resolved after and status; verify effective versions (for example
  `mvn dependency:tree` and `mvn help:effective-pom`) rather than pom.xml
  text alone, including transitive versions of PDFBox and POI.
- Present baseline/after tables with discovered tests, passed, failed, errors
  and skipped. Parse fresh Surefire XML reports (`target/surefire-reports`),
  avoid double counting, and investigate any decrease in discovered tests.
  Mark skipped tests explicitly.
- For compilation errors or test failures, report each root cause, evidence,
  impact and proposed fix (including exact version changes when relevant).
  Ask permission to apply **each fix individually** with `ask_user`; do not
  silently apply fixes, rollbacks or additional version changes.
- Apply approved fixes, rerun affected tests, then rerun the full matrix before
  declaring success. Report unresolved or declined fixes plainly.
- Summarize all Maven, compiler, JVM and runtime warning categories with counts,
  expected-test versus actionable classification, and proposals. Keep warning
  remediation a separate approval step. Do not suppress warnings
  indiscriminately or claim an agent option eliminates unrelated CDS warnings.
- Save reproducible commands, report paths and sanitized logs in the session
  workspace. Do not include credentials or sensitive data.

### 5. Propose documentation updates individually
- Revisit the documentation inventory against the actual resolved versions,
  migrations, test commands, runtime requirements and warnings
  (`README.md` including the JDK prerequisite and download links,
  `CONTRIBUTING.md`, `CODING_GUIDELINES.md`, `.github/workflows/build.yml`,
  `.github/workflows/deploy.yml`).
- Present each affected document with its path, outdated content, proposed
  replacement and reason. If none are affected, state that explicitly.
- Ask for confirmation for **each document change individually** with `ask_user`.
  Approval of dependency changes is not documentation-edit approval.
- Apply only approved documentation edits and run existing documentation checks
  where applicable. Report accepted, declined and pending proposals.

## Compatibility examples to verify, not universal rules
- Apache POI (`poi`/`poi-ooxml`) major-version bumps can drop deprecated APIs
  or change packaging (for example `poi-ooxml-full`/`poi-ooxml-lite`); check
  release notes for breaking API changes used by the Excel comparator.
- PDFBox major-version bumps can break APIs used by the PDF comparators, and
  `pdfcompare` pins its own PDFBox dependency; confirm the resolved PDFBox
  version is the intended one and that `pdfcompare` supports it.
- Lombok releases must support the selected `java.version` and the selected
  `maven-compiler-plugin` version (`annotationProcessorPaths`); mismatches
  typically surface as annotation-processing failures, not plain compile errors.
- JUnit Jupiter API and engine artifacts must share the same version and be
  supported by the selected Surefire version; AssertJ must support the
  selected Java version.
- JDK feature-release bumps: the Lombok `sun.misc.Unsafe` warning and similar
  JVM warnings are known JDK noise, not necessarily new defects.

## Output
- Preliminary pom.xml-review decision, findings, individually approved fixes and
  post-fix test results, or an explicit skipped-review status.
- Confirmed selection and feasibility assessment.
- Final before/after version table and full baseline/after test report.
- Individually approved failure fixes and remaining blockers.
- Warning summary with separate remediation proposals.
- Individually approved documentation changes.
