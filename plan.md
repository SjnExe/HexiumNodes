# Execution Plan - Optimization & Refactoring

## Current Status
- **Dependencies Updated:** `libs.versions.toml` updated with Spotless 8.2.1, JUnit 6.0.2, etc.
- **Build Optimized:** `gradle.properties` updated with `nonTransitiveRClass`, `R8.fullMode`, and `ksp.incremental`.
- **CI Optimized:** `.github/workflows/build.yml` refactored for wildcard artifacts and faster compression.
- **Current Status:** Build Passing. Fixed `hiltJavaCompileDevDebug` failure and optimized R8 configuration.
- **Known Issues:** Non-fatal `java.lang.NullPointerException` in KSP persists but does not block the build.

## Completed Tasks
- **Fix Build Failure:** Configured `tasks.withType<JavaCompile>` to use Java 25 toolchain, ensuring correct compilation even if the Gradle Daemon runs on an older JDK.
- **Setup Script:** Updated `AGENTS.md` to use read-only system setup and explicit `JAVA_HOME=OpenJdk25`.
- **Gradle Properties:** Added optimization flags (`nonTransitiveRClass`, `R8.fullMode`) to `gradle.properties`.
- **R8 Optimization:** Verified `android.enableR8.fullMode=true` is working. Refined `proguard-rules.pro` to replace broad `-keep class ... { *; }` with focused `-keepclassmembers` for data models, enabling proper code shrinking (Est. Release APK: ~10-12MB vs Debug APK: ~31MB).

## Next Steps for Future Agent
1.  **Refactor:** Consider implementing convention plugins for cleaner build logic if time permits.
2.  **KSP Stability:** Monitor the KSP `NullPointerException`. If it causes build instability, consider disabling `ksp.incremental`.
