# Execution Plan - Optimization & Refactoring

## Current Status
- **Refactoring:** Implemented Convention Plugins (`build-logic`) to consolidate build logic and remove duplication across modules.
- **Dependencies Updated:** `libs.versions.toml` updated with Spotless 8.2.1, JUnit 6.0.2, etc.
- **Build Optimized:** `gradle.properties` updated with `nonTransitiveRClass`, `R8.fullMode`, and `ksp.incremental`.
- **CI Optimized:** `.github/workflows/build.yml` refactored for wildcard artifacts and faster compression. Added note about configuration cache encryption key.

## Completed Tasks
- **Convention Plugins:** Created `build-logic` module with plugins for Android App, Library, Compose, Hilt, and Spotless. Migrated all modules to use these plugins.
- **Fix Build Failure:** Configured `tasks.withType<JavaCompile>` to use Java 25 toolchain via convention plugin, ensuring correct compilation even if the Gradle Daemon runs on an older JDK.
- **Setup Script:** Updated `AGENTS.md` to use read-only system setup and explicit `JAVA_HOME=OpenJdk25`.
- **Gradle Properties:** Added optimization flags (`nonTransitiveRClass`, `R8.fullMode`) to `gradle.properties`.
- **R8 Optimization:** Verified `android.enableR8.fullMode=true` is working. Refined `proguard-rules.pro` to replace broad `-keep class ... { *; }` with focused `-keepclassmembers` for data models.

## Next Steps for Future Agent
1.  **CI Cache:** If possible, add `GRADLE_ENCRYPTION_KEY` secret to enable saving configuration cache in CI.
2.  **KSP Stability:** Monitor the KSP `NullPointerException`. If it causes build instability, consider disabling `ksp.incremental`.
3.  **Test Coverage:** Expand unit tests for core logic.
