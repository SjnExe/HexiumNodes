# Hexium Nodes - Project Plan & Roadmap

## 1. Project Overview
**Hexium Nodes** is an ad-reward application where users watch ads to earn credits.
- **Package Name:** `com.hexium.nodes`
- **Minimum Android Version:** Android 7.0 (API 24)
- **Target Android Version:** Android 15 (API 35)
- **Architecture:** MVVM + Clean Architecture (Data, Domain, UI)
- **Tech Stack:** Kotlin, Jetpack Compose, Hilt, Room, Retrofit.

## 2. Repository Structure
```
/
├── .github/workflows/   # CI/CD Workflows
├── app/                 # Android App Module
│   ├── src/main/java/   # Source Code (MVVM)
│   ├── src/dev/         # Dev-specific configs
│   └── src/stable/      # Stable-specific configs
├── gradle/              # Gradle Wrapper & Version Catalog
└── keystore.properties  # Local signing keys (Ignored)
```

## 3. Current Implementation (Mock Phase)
Since backend access is currently unavailable, the app uses a **Mock Repository** (`MockAdRepository`) to simulate server logic locally.
- **Ad Limit:** 50 ads per day.
- **Regeneration:** Slots regenerate 24 hours after being used.
- **Storage:** Uses SharedPreferences (encrypted in future) to store credits, timestamps, and login session.
- **Versioning:**
  - **Stable:** Tags `vX.Y.Z` -> Version `X.Y.Z`
  - **Dev:** `X.Y.Z-beta-PR{number}.{commit_count}`

## 4. Future Roadmap (Post-Approval)
### Phase 2: Server Integration
1.  **Backend API:** Replace `MockAdRepository` with `NetworkAdRepository` implementing `AdRepository` interface.
2.  **Authentication:** Implement real Login/Register with JWT tokens.
3.  **Security:**
    -   Move ad limit logic to server.
    -   Implement "App Attest" or "Play Integrity API" to prevent modified APKs.
    -   Encrypt sensitive data in `DataStore`.

### Phase 3: Monetization Optimization
1.  **Ad Mediation:** Integrate multiple ad networks (AdMob, Unity Ads, etc.) via mediation.
2.  **Analytics:** Add Firebase Analytics to track user behavior.

## 5. Maintenance Guide
### Building Locally
1.  **Debug:** `./gradlew assembleDevDebug`
2.  **Release:** `./gradlew assembleStableRelease` (Requires `keystore.properties`)

### GitHub Actions
- **Secrets Required:**
  - `KEYSTORE_BASE64`: Base64 encoded `.jks` file.
  - `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
- **Triggers:**
  - Push to `v*` tag: Builds Stable Release.
  - PR to `dev`: Builds Dev Release.

### generating Keystore (Termux/Linux)
Run this command to generate a keystore and get the base64 string for GitHub Secrets:
```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias hexium_key
base64 -w 0 keystore.jks > keystore_base64.txt
```
Copy content of `keystore_base64.txt` to `KEYSTORE_BASE64` secret.
