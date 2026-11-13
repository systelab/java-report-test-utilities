---
name: upgrade-components
description: >
  Select and apply Maven dependency, plugin, and Java upgrades with compatibility
  checks, explicit approval, full-build verification, and documentation review.
---

# Upgrade components

## Project facts

- This repository is a single-module Maven library (`com.systelab:java-report-test-utilities`).
- Its root `pom.xml` has no parent POM, imported BOM, or internal modules.
- Versioned components include dependencies, build plugins, and `java.version`.
- The project's own `<version>` is its release version, not an upgrade component.
- Ask the user for the intended release version before any release-version change; never change it without approval.

## Rules

- Preserve unrelated worktree changes and never commit unless explicitly requested.
- Keep any version the user asks to retain.
- Never silently omit a component with a newer stable release from update selection.
- Mark an item `Keep` automatically only when release evidence confirms no newer stable version.
- Do not remove version pins or dependency declarations as part of an upgrade.
- Keep `poi` and `poi-ooxml` aligned under `poi.version`.
- Keep `junit-jupiter-api` and `junit-jupiter-engine` aligned under `junit-jupiter.version`.
- Name version properties `<artifact_id>.version`; family properties use the shared artifact id.
- Renaming `junit.jupiter.version` requires separate approval unless included in an approved JUnit upgrade.
- `java.version` and `project.*` properties are exempt from the property naming convention.
- Use the user's requested source; corroborate stable releases with publisher documentation and Maven Central.
- Exclude alpha, beta, RC, milestone, early-access, and SNAPSHOT releases.
- Do not treat the last Maven metadata entry or stale Solr results as proof of the latest stable version.
- If release discovery is blocked, report `Unknown` and ask how to proceed; do not mark the item `Keep`.
- The `github` profile's GitHub Packages `distributionManagement` is not a dependency version source.
- For requested MCP integrations, verify availability, source, permissions, and installation instructions.
- Do not assume when scope, version, compatibility, or documentation wording is unclear; ask the user.

## Workflow

### 1. Preliminary POM review

- Before workspace inspection or update selection, ask whether to review the `pom.xml` for defects.
- Offer `Review` or `Skip review`; wait for the user's choice.
- If requested, follow `.agents/skills/pom-cleanup/SKILL.md`.
- Keep any approved POM fixes separate from dependency upgrades.
- Validate approved fixes with the full build before continuing.
- Refresh the component inventory after POM fixes.
- If review is skipped, record that decision and continue.

### 2. Prepare the upgrade inventory

- Read relevant guidance: `README.md`, `CONTRIBUTING.md`, `CODING_GUIDELINES.md`,
  `.github/workflows/build.yml`, `.github/workflows/deploy.yml`, and applicable templates.
- Exclude generated output such as `target/`; report inaccessible or contradictory guidance.
- Inventory `java.version`, compiler and Surefire plugins, and every declared dependency.
- Include `poi`, `poi-ooxml`, `lombok`, both Jupiter artifacts, `assertj-core`, `pdfbox`, and `pdfcompare`.
- Verify current stable Java feature and applicable JDK patch releases against publisher documentation.
- Do not limit Java choices to the installed JDK or CI-pinned JDK.

### 3. Select updates

- Present every dependency, plugin, and Java version with current, latest stable, proposed target, and notes.
- Include unchanged components; initially mark available upgrades `Pending`, not `Keep`.
- Use an actual multi-select control only when available; markdown checkboxes are not interactive.
- Otherwise ask sequentially whether to keep or update each component, stating current and target versions.
- Ask about Java before dependent libraries so its selection informs compatibility analysis.
- Select `poi` with `poi-ooxml`, and Jupiter API with its engine, as aligned families.
- Explicitly offer the newest stable release even when recommending `Keep` or another compatible target.
- Confirm every item has an explicit decision or evidence that no newer stable version exists.
- Do not infer consent for upgrades, coordinate changes, forks, or prerelease versions.

### 4. Check compatibility

- Analyze the selected set together and consult release notes and migration guides.
- Check Java compiler `source`/`target`/`release`, Lombok processor support, and compiler-plugin support.
- Check POI, PDFBox, and pdfcompare compatibility with Java and one another.
- Inspect `mvn dependency:tree` for transitive PDFBox and POI conflicts.
- Check Jupiter API/engine alignment, Surefire support, and AssertJ compatibility.
- Distinguish documented compatibility from runtime behavior not yet tested.
- Explain each conflict and propose a concrete alternative; obtain individual approval before changing selections.
- Recheck revised selections; report any unresolved uncertainty or blocker.

### 5. Confirm and execute

- Show the complete final update table immediately before asking for execution approval.
- Include every update, keep decision, Java decision, property rename, constraint, migration, risk, and test command.
- Ask explicitly for approval to start; a generic confirmation without the displayed table is insufficient.
- In plan mode, save the plan to the session workspace, track todos, and use `exit_plan_mode`.
- If the proposal changes, show the revised table and obtain approval again.
- Run the baseline command `mvn -B clean install`; capture its actual exit code and full logs.
- Preserve baseline Surefire reports outside `target/` before running `clean` again.
- If baseline validation fails, explain the cause and ask before fixing it or proceeding with the limitation.
- Apply only approved updates and directly necessary migrations.

### 6. Verify and report

- Run the full post-update command `mvn -B clean install`; do not substitute a partial or offline build.
- Verify resolved versions with `mvn dependency:tree` and `mvn help:effective-pom`.
- Include transitive PDFBox and POI versions in the version-change report.
- Parse fresh `target/surefire-reports` XML; report discovered, passed, failed, errored, and skipped tests.
- Avoid double counting, investigate test-discovery decreases, and mark skipped tests explicitly.
- For each failure, report cause, evidence, impact, and a proposed fix; ask approval for each fix separately.
- Rerun affected tests and then the full build after approved fixes.
- Report unresolved or declined fixes; do not claim success if any suite is failing, blocked, or unverified.
- Summarize Maven, compiler, JVM, and runtime warning categories with counts and actionable status.
- Keep warning remediation separate; do not suppress warnings indiscriminately.
- Save reproducible commands, report paths, and sanitized logs in the session workspace.

### 7. Review documentation

- Compare actual versions, migrations, tests, runtime requirements, and warnings with relevant documentation.
- Check `README.md`, `CONTRIBUTING.md`, `CODING_GUIDELINES.md`, and build/deploy workflows.
- Present each affected document, outdated content, proposed replacement, and reason.
- Ask approval for each document change separately; dependency approval is not documentation approval.
- Apply only approved edits and run existing documentation checks where applicable.

## Compatibility cautions

- Check POI major-release notes for deprecated API removal and packaging changes.
- Confirm `pdfcompare` supports the resolved PDFBox version; it has its own PDFBox dependency.
- Confirm Lombok supports the selected Java and compiler-plugin versions.
- Treat known JDK warnings, such as Lombok `sun.misc.Unsafe` warnings, as evidence to assess,
  not automatically as new defects.

## Report

- State the POM-review decision, findings, approved fixes, and post-fix results, or that review was skipped.
- Report selected versions, feasibility, and final before/after resolved versions.
- Include baseline and post-update test results, failures, blockers, and warning proposals.
- Report accepted, declined, or pending documentation changes.
