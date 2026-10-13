# Sophisticated Backpacks - Digital Storages Expansion

NeoForge addon for Minecraft 1.21.1 that adds universal Sophisticated Backpacks upgrades for
connecting to Applied Energistics 2, Refined Storage 2, and Tom's Simple Storage.

Sophisticated Core and Sophisticated Backpacks are required. Storage integrations are optional.
The upgrade reuses Sophisticated's deposit filters and each storage system's own connectivity
and access rules. This development version intentionally has no custom item model or texture.

## Development

- Build: `./gradlew build` (Windows: `gradlew.bat build`)
- Run client: `./gradlew runClient` (Windows: `gradlew.bat runClient`)
- VS Code: use `Digital Storages Expansion: Build` or `Digital Storages Expansion: Run NeoForge Client` from
  the task runner.

Optional development JARs for the supported integrations are listed in [`libs/README.md`](libs/README.md).
The MVP plan and development documentation are indexed in [`docs/README.md`](docs/README.md).

The GitHub Actions workflows run the build for pull requests, create semantic
version tags for Conventional Commit changes merged to `main`, and publish a
release containing the built mod JAR for each tag.
