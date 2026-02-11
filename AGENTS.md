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
sudo apt autoremove -y
sudo apt clean
./gradlew --version
```
