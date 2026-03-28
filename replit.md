# Silva Patcher

## Project Overview

Silva Patcher is a Kotlin library for patching Android applications. It is a core engine used by the Silva ecosystem (CLI, Manager) for modifying Android APKs via bytecode patching, resource patching, and arbitrary file manipulation. Forked and rebranded from morphe-patcher.

## Tech Stack

- **Language**: Kotlin (JVM 11 target)
- **Build System**: Gradle 8.9 with Kotlin DSL
- **Runtime**: Java 19 (GraalVM CE 22.3.1)
- **Group ID**: `app.silva`
- **Artifact**: `silva-patcher`
- **Key Libraries**:
  - `smali` / `multidexlib2`: Dalvik bytecode manipulation
  - `apktool-lib`: Android resource decoding/encoding
  - `arsclib`: Android Resource Table (.arsc) manipulation
  - `apksig` / `apkzlib`: APK signing and packaging
  - `kotlinx-coroutines`: Async operations
  - `kotlinx-serialization`: JSON handling
  - `Guava`: Utility functions
- **Testing**: JUnit 5 + MockK

## Project Layout

```
src/main/kotlin/app/silva/patcher/
├── apk/            # APK merging, signing, manifest utilities
├── patch/          # Patch interface and execution contexts
├── resource/       # Android resource processing
├── util/           # General utilities, smali compilers, proxy types
├── Fingerprint.kt  # Code-matching logic
└── Patcher.kt      # Main entry point

src/test/kotlin/app/silva/patcher/  # Unit tests mirroring main source
src/main/resources/app/silva/patcher/version.properties
api/silva-patcher.api               # Binary compatibility validation
docs/                               # Documentation
gradle/                             # Gradle wrapper and version catalogs
```

## Build & Run

This is a library project — there is no frontend or web server.

**Build the library:**
```bash
./gradlew assemble --no-daemon
```

**Run tests:**
```bash
./gradlew test --no-daemon
```

## Workflow

- **Build**: `./gradlew assemble --no-daemon` (console output)

## Publishing

Published to GitHub Packages at:
`https://maven.pkg.github.com/SilvaTechB/silva-patcher`

## Environment Requirements

The build requires GitHub credentials (auto-populated in this environment):
- `GITHUB_TOKEN`: For accessing GitHub Packages (custom Maven repositories)
- `GITHUB_ACTOR`: GitHub username

## Dependencies

Some dependencies are fetched from:
- `https://jitpack.io` (smali/baksmali builds)
- `https://maven.pkg.github.com/MorpheApp/registry` (upstream morphe libraries: apktool-lib, arsclib, multidexlib2)
