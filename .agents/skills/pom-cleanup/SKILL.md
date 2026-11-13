---
name: pom-cleanup
description: >
  Review Maven POM files for structural defects, dependency issues, version
  conflicts, and unused or redundant configuration; propose fixes for approval.
---

# POM cleanup

## Purpose and rules

- Use this skill to inspect Maven POM files for defects, not to perform routine version upgrades.
- Review every relevant POM, including parent and module POMs when present.
- Preserve unrelated worktree changes.
- Treat newer available versions as upgrade candidates, not POM defects.
- Never change a POM based only on suspicion; report evidence and uncertainty.
- Do not edit or remove anything until the user approves that specific proposed fix.
- Keep unrelated fixes separate and do not commit unless explicitly requested.

## Review

- Inspect project instructions, module structure, dependency usage, build configuration, and test coverage.
- Check for duplicate or conflicting declarations, including duplicates in XML sections and repeated properties.
- Check dependency `groupId:artifactId:type:classifier` coordinates declared more than once.
- Identify conflicting versions, scopes, exclusions, or optional flags for the same dependency.
- Inspect the resolved dependency tree for direct/transitive version conflicts and mediation.
- Identify dependencies that appear unused in source and tests.
- Use `mvn dependency:analyze` as supporting evidence, not definitive proof; reflection, service loading,
  annotations, generated code, and test-only usage can produce false positives.
- Distinguish dependencies unused by production code from those needed only by tests or build plugins.
- Check for undefined, misspelled, duplicated, or unused properties and property references.
- Check version pins, dependency management, parent/BOM imports, and plugin versions for inconsistency.
- Identify redundant version declarations, overrides, and configuration inherited from a parent or BOM.
- Check dependencies and plugins for deprecated or obsolete coordinates and configuration.
- Identify unnecessary dependencies, plugins, profiles, repositories, and configuration with no demonstrated use.
- Check dependency scopes and optional declarations against actual usage and downstream impact.
- Flag likely test-only dependencies declared with compile scope; do not change scope without approval.
- Check compiler settings for conflicting or redundant `source`, `target`, and `release` values.
- Inspect annotation processor configuration, including explicit paths and version alignment.
- Check repository, plugin, profile, and distribution settings only for issues relevant to the POM's purpose.
- Do not treat a configured publishing repository as a dependency-version source.
- Consider supported Maven behavior and actual effective POM output before proposing inherited-config changes.

## Findings and approval

- Report all findings before asking to apply fixes.
- For each finding, include a stable identifier, file and line range, evidence, impact, proposed exact change,
  and behavior or compatibility risk.
- Separate verified issues from suspicious items that need confirmation.
- Do not label an item a defect without evidence; say what evidence is missing.
- Ask the user about each proposed fix individually and sequentially.
- Apply only explicitly approved fixes; preserve declined findings.
- If evidence changes the proposal, explain the change and ask for approval again.
- State when dependency scope changes can affect downstream consumers.

## Validation and result

- Choose the smallest relevant validation for approved changes; use the project's canonical full build
  when the change affects dependency resolution, compilation, plugins, or tests.
- Verify effective and resolved configuration when the fix concerns inheritance or version conflicts.
- Report validation command, actual result, and any tests skipped or blocked.
- If validation fails, report evidence and request approval before any additional fix or rollback.
- Save any required logs or reports in the session workspace; omit credentials and sensitive data.
- Summarize accepted fixes, declined findings, unresolved uncertainty, and validation status.
