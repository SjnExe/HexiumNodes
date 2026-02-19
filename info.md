# Project Development Setup Info

This document details the development environment, tools, plugins, and configuration used in this project.

## Tools & Environment
- **Java Version:** 25
- **Gradle Version:** 9.3.1
- **Android SDK Build Tools:** 36.0.0

## Gradle Plugins
Versions are defined in `gradle/libs.versions.toml`.

| Plugin ID | Version |
| :--- | :--- |
| `com.android.application` | 9.0.1 |
| `com.android.library` | 9.0.1 |
| `org.jetbrains.kotlin.plugin.compose` | 2.3.10 |
| `org.jetbrains.kotlin.android` | 2.3.10 |
| `com.google.devtools.ksp` | 2.3.5 |
| `com.google.dagger.hilt.android` | 2.59.1 |
| `com.diffplug.spotless` | 8.2.1 |
| `io.gitlab.arturbosch.detekt` | 1.23.8 |
| `org.jetbrains.kotlinx.kover` | 0.9.1 |
| `io.github.takahirom.roborazzi` | 1.57.0 |
| `com.autonomousapps.dependency-analysis` | 3.5.1 |
| `com.jraska.module.graph.assertion` | 2.9.0 |
| `nl.littlerobots.version-catalog-update` | 1.0.1 |

## Convention Plugins
These local plugins are located in `build-logic/convention/src/main/kotlin/`.

- `AndroidApplicationConventionPlugin`
- `AndroidComposeConventionPlugin`
- `AndroidLibraryConventionPlugin`
- `DetektConventionPlugin`
- `HiltConventionPlugin`
- `KoverConventionPlugin`
- `RoborazziConventionPlugin`
- `SpotlessConventionPlugin`

## Configuration (gradle.properties)
Key build and runtime settings.

- **JVM Args:** `-Xmx3072m -XX:MaxMetaspaceSize=1g --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --enable-native-access=ALL-UNNAMED`
- **Parallel Execution:** `true`
- **Build Caching:** `true`
- **Configuration Cache:** `true`
- **VFS Watching:** `true`
- **R8 Full Mode:** `true`
- **Non-Transitive R Class:** `true`
- **KSP Incremental:** `true`

## Libraries
Major dependencies and their versions.

- **Compose BOM:** 2026.02.00
- **Activity Compose:** 1.12.4
- **Navigation Compose:** 2.9.7
- **Hilt Navigation Compose:** 1.3.0
- **Hilt Android:** 2.59.1
- **Room:** 2.8.4
- **Retrofit:** 3.0.0
- **OkHttp:** 5.3.2
- **Coroutines Android:** 1.10.2
- **Core KTX:** 1.17.0
- **Material 3:** (via Compose BOM)

## Environment Setup Script

```bash
sudo apt update
sudo apt full-upgrade -y
sudo apt install -y openjdk-25-jdk
sudo update-java-alternatives --set java-1.25.0-openjdk-amd64
echo y | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager "build-tools;36.0.0"
sudo apt autoremove -y
sudo apt clean
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
./gradlew --version
java -version
```
