# Execution Plan - Optimization & Refactoring

## Current Status
- **Dependencies Updated:** `libs.versions.toml` updated with Spotless 8.2.1, JUnit 6.0.2, etc.
- **Build Optimized:** `gradle.properties` updated with `nonTransitiveRClass`, `R8.fullMode`, and `ksp.incremental`.
- **CI Optimized:** `.github/workflows/build.yml` refactored for wildcard artifacts and faster compression.
- **Current Failure:** Build fails at `:app:hiltJavaCompileDevDebug` with `error: invalid source release: 25`. There is also a `java.lang.NullPointerException` in KSP related to IntelliJ internals.

## Unfinished Tasks
- **Fix Build Failure:** Diagnose why Hilt/Java compilation is failing with Java 25. May need to explicitly set `javaCompiler` for Hilt tasks or disable KSP incrementalism if unstable with Java 25.
- **Gradle Plugin Refactoring:** Moving common logic to convention plugins was identified as a goal but deferred.

## Next Steps for Future Agent
1.  **Diagnose Java 25 Error:** Check `compileOptions` in `app/build.gradle.kts`. Ensure the Hilt annotation processor is running on the correct JDK.
2.  **Verify KSP Stability:** If KSP crashes persist, try disabling `ksp.incremental` in `gradle.properties`.
3.  **Validate R8 Full Mode:** Once the build passes, verify that the release APK actually works (R8 full mode can be aggressive).
4.  **Refactor:** Consider implementing convention plugins for cleaner build logic if time permits.
