# Hexium Nodes - Project Plan & Roadmap

## 1. Project Overview
**Hexium Nodes** is an ad-reward application where users watch ads to earn credits.
- **Package Name:** `com.hexium.nodes`
- **Minimum Android Version:** Android 7.0 (API 24)
- **Target Android Version:** Android 15 (API 35)
- **Architecture:** MVVM + Clean Architecture (Data, Domain, UI)
- **Tech Stack:** Kotlin, Jetpack Compose, Hilt, Room, Retrofit.

## 2. Recent Progress & Current State
### 2.1 Completed
- **CI/CD Refactor:**
    - Split into `setup`, `lint`, `build`, `test_arm`, `release` jobs.
    - Implemented "floating dev tag" strategy for Dev releases.
    - Added real-time logging (`tee`) to CI.
    - Signing logic: Uses `KEYSTORE_BASE64` secret for Stable; falls back to generated debug key for Dev/PRs.
- **Dependencies:** Added `security-crypto`, `datastore`, `play-integrity`, `chucker` (debug).
- **Files Created:**
    - `SecurityManager.kt`, `SecurityModule.kt` (Encryption/Integrity).
    - `SettingsScreen.kt`, `SettingsViewModel.kt`, `SettingsRepository.kt` (Settings & DataStore).
    - `SplashScreen.kt`, `SplashViewModel.kt` (Auth check).
    - `Theme.kt` (Dynamic colors).
    - `ic_settings.xml` (Drawable).

### 2.2 Pending Fixes (Blocking Build)
The local build `assembleDevRelease` is failing with compilation errors:
1.  **MainActivity.kt**:
    - Unresolved `HexiumNodesTheme` (Check `Theme.kt` package/imports).
    - Unresolved `HomeScreen` parameters (Signature mismatch).
2.  **LoginScreen.kt**:
    - Unresolved `R.drawable.ic_settings` (Resource ID not found).
3.  **HomeScreen.kt**:
    - Need to verify signature to match `MainActivity` usage.

## 3. Next Steps (For Next Session)
1.  **Fix Compilation Errors**:
    - **Check `Theme.kt`**: Verify package declaration `package com.hexium.nodes.ui.theme` and import it in `MainActivity`.
    - **Check `HomeScreen.kt`**: Read file to confirm constructor parameters. Update `MainActivity` call site or `HomeScreen` definition.
    - **Check Resources**: Ensure `ic_settings.xml` is valid. Run `./gradlew clean` to regenerate `R` class if needed.
2.  **Verify Build**: Run `./gradlew assembleDevRelease`.
3.  **Submit Changes**: Push the `workflow-refactor` branch.

## 4. Maintenance Guide
### Building Locally
1.  **Debug:** `./gradlew assembleDevDebug`
2.  **Release:** `./gradlew assembleDevRelease` (Uses debug keystore fallback in dev flavor).

### Keystore Generation (For User)
- Run in Termux to generate keys for GitHub Secrets:
  ```bash
  mkdir -p ~/storage/shared/HexiumNodes/Signature
  keytool -genkey -v -keystore ~/storage/shared/HexiumNodes/Signature/hexium_release.jks \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias hexium_key \
    -dname "CN=Sin Exe, OU=App, O=Hexium Nodes, L=Palakkad, S=Kerala, C=IN" \
    -storepass "YOUR_PASSWORD" -keypass "YOUR_PASSWORD"
  base64 -w 0 ~/storage/shared/HexiumNodes/Signature/hexium_release.jks > ~/storage/shared/HexiumNodes/Signature/keystore_base64.txt
  ```
- Upload `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` to GitHub Secrets.

## 5. Future Roadmap
- **Server Integration**: Switch from Mock to Real API (Auth, Ad Limits).
- **Play Integrity**: Implement server-side verification of client tokens.
- **Optimization**: Verify R8 shrinking efficiency.
