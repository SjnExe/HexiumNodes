# Hexium Nodes - Project Plan & Roadmap

## 1. Project Overview
**Hexium Nodes** is an Android application where users can earn credits by watching ads. The credits are intended to be used on the Hexium Nodes platform.
- **Package Name:** `com.hexium.nodes`
- **Minimum Android Version:** Android 7.0 (API 24)
- **Target Android Version:** Android 15 (API 35)
- **Architecture:** MVVM + Clean Architecture (Data, Domain, UI)
- **Tech Stack:** Kotlin (JDK 25), Jetpack Compose, Hilt, Room, Retrofit.

## 2. Architecture & Modules
The app follows **Modern Android Architecture** principles and is modularized:
*   `:app`: The application entry point (DI Components).
*   `:feature:home`: Home screen and Ad interaction logic.
*   `:feature:auth`: Login and Splash screens.
*   `:feature:settings`: Settings and Developer options.
*   `:core:ui`: Shared UI components, Theme, and Resources.
*   `:core:model`: Shared data models.
*   `:core:common`: Shared utilities.
*   `:data`: Repositories, Data Sources, and Networking.

## 3. Server & Data Strategy
### Current State (Mock/Dev)
*   **Backend:** Mock mode where `MockAdRepository` simulates a backend.
*   **Configuration:** Dynamic settings (Ad Limits, Maintenance Mode, Min Version, Test Users) are fetched from a static JSON file (`config.json`) hosted on **GitHub Pages**.
*   **Auth:** Login validates against `testUsers` list in the fetched config.
*   **Data Persistence:** User credits and ad history are stored locally using `SharedPreferences` (Double precision) and `DataStore`.

### Future State (Production)
*   **Auth:** Integration with Hexium Nodes existing authentication (or Firebase Auth).
*   **Database:** A real backend (e.g., Firebase, Supabase, or Custom PHP/SQL) is required to securely store user credits.
    *   *Note:* GitHub Pages cannot be used as a write-able database.
*   **Security:**
    *   **Server-Side Verification (SSV):** Ad rewards must be validated on the server via AdMob callbacks.
    *   **Play Integrity:** The app sends an integrity token to the server to verify the request is coming from a genuine, unmodified app instance.

## 4. Completed Progress
### 4.1 Modularization & Quality
- [x] **Modularization:** Split app into `:core`, `:data`, `:feature` modules.
- [x] **Linting:** Enforced code style with **Spotless/Ktlint**.
- [x] **Java 25:** Configured build to use `jvmToolchain(25)`.
- [x] **Precision:** Switched financial data types to `Double` to fix precision errors.

### 4.2 CI/CD & Config
- [x] **CI Optimization:** Fail-fast Lint/Test steps, Artifact uploads.
- [x] **GitHub Pages:** Workflow to manually publish `config/config.json`.
- [x] **Remote Config:** App fetches config from Server URL (defaulting to GitHub Pages).

### 4.3 UI/UX
- [x] **Theme:** Material 3, Dynamic Colors, Dark/Light mode chips.
- [x] **Settings:** Redesigned User Profile, Fixed Input Glitches.
- [x] **Splash:** Instant launch feel, removed artificial delays, handled "Maintenance" and "Update Required" states.

## 5. Maintenance Guide
### Building Locally
1.  **Debug:** `./gradlew assembleDevDebug`
2.  **Release:** `./gradlew assembleDevRelease` (Requires keystore or fallback).

### Quality
- **Format:** `./gradlew spotlessApply`
- **Lint:** `./gradlew lintDevDebug`
- **Test:** `./gradlew testDevDebug`

## 6. Future Roadmap (Remaining Tasks)
- [ ] **Phase 2: Real API Integration:** Replace `MockAdRepository` with `NetworkAdRepository` connected to real Hexium Nodes API.
- [ ] **Phase 3: Production Security:** Implement Play Integrity token generation and server-side verification.
- [ ] **Phase 4: Release:** Proguard/R8 optimization checks and Play Store submission.
