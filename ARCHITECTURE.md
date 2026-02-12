# Hexium Nodes - Architecture & Roadmap

## 1. Project Overview
**Hexium Nodes** is an Android application where users can earn credits by watching ads. The credits are intended to be used on the Hexium Nodes platform.

## 2. Architecture
The app follows **Modern Android Architecture** principles:
*   **Modularization:** Code is split into feature and core modules to improve separation of concerns and build speeds.
    *   `:app`: The application entry point.
    *   `:feature:home`: Home screen and Ad interaction logic.
    *   `:feature:auth`: Login and Splash screens.
    *   `:feature:settings`: Settings and Developer options.
    *   `:core:ui`: Shared UI components, Theme, and Resources.
    *   `:core:model`: Shared data models.
    *   `:core:common`: Shared utilities.
    *   `:data`: Repositories, Data Sources, and Networking.
*   **UI Pattern:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF) using Jetpack Compose.
*   **Dependency Injection:** Hilt.
*   **Asynchronous Processing:** Kotlin Coroutines & Flow.

## 3. Server & Data Strategy
### Current State (Mock/Dev)
*   **Backend:** Currently, the app runs in a "Mock" mode where `MockAdRepository` simulates a backend.
*   **Configuration:** Dynamic settings (Ad Limits, Maintenance Mode) are fetched from a static JSON file hosted on **GitHub Pages** (read-only).
*   **Data Persistence:** User credits and ad history are stored locally using `Room` and `DataStore` (for settings).

### Future State (Production)
*   **Auth:** Integration with Hexium Nodes existing authentication (or Firebase Auth).
*   **Database:** A real backend (e.g., Firebase, Supabase, or Custom PHP/SQL) is required to securely store user credits.
    *   *Note:* GitHub Pages cannot be used as a write-able database.
*   **Security:**
    *   **Server-Side Verification (SSV):** Ad rewards must be validated on the server via AdMob callbacks.
    *   **Play Integrity:** The app sends an integrity token to the server to verify the request is coming from a genuine, unmodified app instance.

## 4. Development & CI/CD
*   **Public Repository:** This repo is public. **NEVER COMMIT SECRETS.**
*   **CI/CD:** GitHub Actions handles Linting, Testing, Building, and Release creation.
*   **Linting:** Spotless (Ktlint) enforces code style. Run `./gradlew spotlessApply` before pushing.

## 5. Roadmap
- [x] Modularization (Core, Features, Data).
- [x] CI/CD Optimization (Lint, Test, Artifact Uploads).
- [x] UI Standardization (Material 3, Material Icons).
- [ ] **Phase 2:** Integrate with real Hexium Nodes API (Login, Balance).
- [ ] **Phase 3:** Implement Remote Config (via GitHub Pages or Firebase).
- [ ] **Phase 4:** Production Release Preparation (Obfuscation checks, Play Store compliance).
