# Execution Plan - Optimization & Refactoring

## Current Status
- **Dependencies Updated:** `libs.versions.toml` updated with Spotless 8.2.1, JUnit 6.0.2, etc.
- **Build Optimized:** `gradle.properties` updated with `nonTransitiveRClass`, `R8.fullMode`, and `ksp.incremental`.
- **CI Optimized:** `.github/workflows/build.yml` refactored for wildcard artifacts and faster compression.
- **Current Status:** Build Passing. Fixed `hiltJavaCompileDevDebug` failure (`invalid source release: 25`) by explicitly configuring the Java toolchain for `JavaCompile` tasks.
- **Known Issues:** Non-fatal `java.lang.NullPointerException` in KSP persists but does not block the build.

## Completed Tasks
- **Fix Build Failure:** Configured `tasks.withType<JavaCompile>` to use Java 25 toolchain, ensuring correct compilation even if the Gradle Daemon runs on an older JDK.
- **Setup Script:** Updated `AGENTS.md` with a cleaner setup script.
- **Gradle Properties:** Added `android.nonTransitiveRClass=true`, `android.enableR8.fullMode=true`, `ksp.incremental=true`, `ksp.incremental.intermodule=true` to `gradle.properties`.

## Next Steps for Future Agent
1.  **Validate R8 Full Mode:** While `assembleDevRelease` passes, verify runtime behavior to ensure aggressive shrinking (R8 full mode) hasn't removed necessary classes.
2.  **Refactor:** Consider implementing convention plugins for cleaner build logic if time permits.
3.  **KSP Stability:** Monitor the KSP `NullPointerException`. If it causes build instability, consider disabling `ksp.incremental`.
