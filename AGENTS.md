# AGENTS.md

This file contains instructions for AI agents working on the **Hexium Nodes** repository.

## Project Context
- **Name:** Hexium Nodes
- **Package:** `com.hexium.nodes`
- **Purpose:** Ad-reward application where users watch ads to earn credits.
- **Tech Stack:**
  - Android (Kotlin)
  - Jetpack Compose (UI)
  - Hilt (Dependency Injection)
  - Room (Local Database)
  - Retrofit (Networking - Placeholder)
  - GitHub Actions (CI/CD)

## Coding Standards
- **Language:** Kotlin (Latest Stable)
- **UI:** Jetpack Compose (avoid XML layouts where possible)
- **Architecture:** Clean Architecture (MVVM/MVI)
  - `data/`: Repositories, Data Sources
  - `domain/`: Use Cases, Models
  - `ui/`: ViewModels, Composable Screens
- **Testing:**
  - Unit Tests for ViewModels and Domain logic.
  - UI Tests using Compose Test Rule.

## Security
- **Never hardcode API keys or secrets.** Use GitHub Secrets and inject them via `BuildConfig`.
- **Ad Logic:** All credit logic must be server-authoritative. For development (mock mode), simulate this validation locally.
- **Minification:** Always ensure Release builds are minified (`minifyEnabled true`).

## Build & Release
- **Flavors:**
  - `dev`: For development and PR checks.
  - `stable`: For production release.
- **Versioning:** Automated via GitHub Actions. Do not manually edit `versionName` or `versionCode` in `build.gradle.kts` unless necessary for configuration.

## Environment
- **Local:** Use `keystore.properties` (ignored by git).
- **CI:** Use `base64` encoded keys in Secrets.

## Environment Setup

The following script is already ran on Jules environment. This ensures all system dependencies are updated.

```bash
sudo apt update
sudo apt full-upgrade -y
sudo apt install -y openjdk-25-jdk
sudo update-java-alternatives --set java-1.25.0-openjdk-amd64
echo y | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager "build-tools;36.0.0"
sudo apt autoremove -y
sudo apt clean
echo "org.gradle.jvmargs=-Xmx3072m -XX:MaxMetaspaceSize=1g --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --enable-native-access=ALL-UNNAMED" > gradle.properties
echo "org.gradle.parallel=true" >> gradle.properties
echo "org.gradle.caching=true" >> gradle.properties
echo "org.gradle.configuration-cache=true" >> gradle.properties
echo "org.gradle.vfs.watch=true" >> gradle.properties
echo "android.useAndroidX=true" >> gradle.properties
echo "android.enableJetifier=false" >> gradle.properties
echo "android.nonTransitiveRClass=true" >> gradle.properties
echo "android.enableR8.fullMode=true" >> gradle.properties
echo "ksp.incremental=true" >> gradle.properties
echo "ksp.incremental.intermodule=true" >> gradle.properties
./gradlew --version
java -version
```

## Useful Commands

### Building
*   **Build Debug APK:** `./gradlew assembleDevDebug`
*   **Build Release APK:** `./gradlew assembleDevRelease` (Requires keystore or fallback)
*   **Bundle (Stable):** `./gradlew bundleStableRelease`

### Quality & Testing
*   **Run Lint:** `./gradlew lintDevDebug`
*   **Run Unit Tests:** `./gradlew testDevDebug`
*   **Format Code (Spotless):** `./gradlew spotlessApply` (Run this before committing!)
*   **Check Format:** `./gradlew spotlessCheck`

### Modularization
*   **Sync Project:** `./gradlew --refresh-dependencies`
*   **Clean Build:** `./gradlew clean build`
