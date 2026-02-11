# Hexium Nodes

Ad-reward application built with modern Android development practices (Kotlin, Jetpack Compose, Clean Architecture).

## Prerequisites

- **JDK 21+**
- **Android Studio Ladybug+** (or command line tools)
- **Termux** (for mobile development)

## Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/hexium-nodes.git
   cd hexium-nodes
   ```

2. **Setup Local Signing (Optional):**
   Create a `keystore.properties` file in the root directory:
   ```properties
   storeFile=my-upload-key.keystore
   storePassword=your_password
   keyAlias=your_alias
   keyPassword=your_password
   ```

3. **Build the App:**
   ```bash
   ./gradlew :app:assembleDevDebug
   ```

## Development

- **Architecture:** Clean Architecture + MVVM.
  - `data/`: Repositories and data sources.
  - `domain/`: Business logic.
  - `ui/`: ViewModels and Compose screens.
- **Testing:** Unit tests run via `./gradlew test`.

## Releases

- **Stable:** Tag a commit with `vX.Y.Z` (e.g., `v1.0.0`) to trigger a stable release build.
- **Dev:** Open a Pull Request to `dev` branch or dispatch manually.

## Contact

For questions or issues, please open an issue in the repository.
