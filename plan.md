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
- **Workflow Optimization:**
    - Removed redundant steps.
    - Implemented fail-fast Lint check.
    - Consolidate artifact uploads.
- **UI Refactoring:**
    - **Theme:** Fixed deprecated status bar warning; implemented Brand Colors fallback for non-Material You devices.
    - **Splash Screen:** Replaced text with `CircularProgressIndicator`; implemented immediate auth check.
    - **Login Screen:** Added Username/Password fields; improved UI.
- **Developer Tools (Dev Flavor):**
    - Added "Copy Logs" and "Save Logs" buttons in Settings.
    - Implemented `LogUtils` to capture and filter app logs.
- **Configuration:**
    - Updated default Server URL to `https://placeholder.hexium.nodes`.

### 2.2 Pending Tasks
- **Modularization:** Refactor the app into multiple modules (core, data, ui, app) to improve build times and separation of concerns.

## 3. Maintenance Guide
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

## 4. Future Roadmap
- **Modularization**: Split into feature modules.
- **Server Integration**: Switch from Mock to Real API (Auth, Ad Limits).
- **Play Integrity**: Implement server-side verification of client tokens.
- **Optimization**: Verify R8 shrinking efficiency.
