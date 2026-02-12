# Hexium Nodes (Android)

**Hexium Nodes** is an Android application designed to allow users to earn credits by engaging with ads.

## 📱 Features
*   **Ad Rewards:** Watch ads to earn credits (Mock implementation for development).
*   **Ad History:** View history of watched ads and their expiry.
*   **Ad Limits:** Daily limits on ad consumption.
*   **Developer Options:** Configure ad rates, limits, and expiry for testing.
*   **Theming:** Support for Dark, Light, and System themes, including Material You (Dynamic Colors).

## 🛠 Tech Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel), Clean Architecture
*   **Dependency Injection:** Hilt
*   **Persistence:** Room Database, DataStore
*   **Networking:** Retrofit (Placeholder for future API)
*   **Build System:** Gradle (Kotlin DSL), Version Catalogs

## 📂 Modularization
The project is modularized to ensure separation of concerns and faster build times:
*   `:app` - Application entry point.
*   `:feature:home` - Home screen, Ad watching logic.
*   `:feature:auth` - Login and Splash screens.
*   `:feature:settings` - Settings and Developer options.
*   `:data` - Repositories, Data Sources, Networking.
*   `:core:ui` - Shared UI components, Theme, Resources (`strings.xml`).
*   `:core:model` - Shared data models.
*   `:core:common` - Shared utilities (`LogUtils`).

## 🚀 Getting Started

### Prerequisites
*   JDK 21 (Required for AGP 9.0+)
*   Android Studio (Latest Stable)

### Building the App
*   **Debug Build:**
    ```bash
    ./gradlew assembleDevDebug
    ```
*   **Release Build:**
    ```bash
    ./gradlew assembleDevRelease
    ```
    *Note: Requires `keystore.properties` or environment variables (see [SECURITY.md](SECURITY.md)).*

### Testing & Quality
*   **Run Unit Tests:**
    ```bash
    ./gradlew testDevDebug
    ```
*   **Run Lint:**
    ```bash
    ./gradlew lintDevDebug
    ```
*   **Format Code:**
    ```bash
    ./gradlew spotlessApply
    ```

## 🌐 GitHub Pages Configuration (Testing)
This repository includes a workflow to publish a static `config.json` to GitHub Pages for testing remote configuration.
1.  Update `config/config.json`.
2.  Go to **Actions** tab -> **Publish Config to GitHub Pages**.
3.  Run workflow.
4.  In the App -> Settings -> Developer Options, set Server URL to `https://<user>.github.io/HexiumNodes`.

## 🤝 Contributing
1.  Fork the repository.
2.  Create a feature branch.
3.  Commit your changes (Sign-off required).
4.  Push to the branch.
5.  Open a Pull Request.

## 📄 License
[MIT License](LICENSE)
