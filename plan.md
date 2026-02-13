# Remaining Tasks for AdMob Integration Fixes

The current session successfully implemented the core AdMob fixes (Orientation logic, Background safety, Config updates) but encountered build issues during verification.

## 1. Fix Build Configuration (Java 25)
- **Issue:** `Execution failed for task ':app:hiltJavaCompileDevDebug'. > Java compilation initialization error: invalid source release: 25`.
- **Cause:** The Hilt/Java compiler task isn't picking up the Java 25 toolchain correctly, or `compileOptions` is missing/misconfigured.
- **Action:**
    - Verify `app/build.gradle.kts` has `compileOptions`.
    - Try explicitly setting `sourceCompatibility` and `targetCompatibility` to `JavaVersion.toVersion(25)` (or string "25").
    - Ensure `AGENTS.md` instructions for environment setup (JDK 25) are valid for the Gradle version.

## 2. Fix Lint Warnings
- **String Format:**
    - **File:** `core/ui/src/main/res/values/strings.xml`
    - **Issue:** `daily_ad_limit` uses `%d` multiple times without positional args.
    - **Fix:** Change to `Daily Ad Limit: %1$d / %2$d`.
- **Annotation Ambiguity:**
    - **File:** `feature/home/src/main/java/com/hexium/nodes/feature/home/ads/RewardedAdManager.kt`
    - **Issue:** `@Inject constructor(@ApplicationContext ...)` triggers a Kotlin warning.
    - **Fix:** Use `@param:ApplicationContext` or `@Inject constructor(...)` with `-Xannotation-default-target=param-property`.

## 3. Verify Fixes
- Run `./gradlew lintDevDebug testDevDebug` to ensure a clean build.
- Confirm "App Open" ads load correctly (no "Format Mismatch") and background network usage is zero.
