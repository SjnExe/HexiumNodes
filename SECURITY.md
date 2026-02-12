# Security Policy

## Reporting a Vulnerability

Please report security vulnerabilities by creating a **private** issue or contacting the maintainers directly. Do NOT open a public issue for sensitive security bugs.

## Public Repository & Secrets Management

This repository is **public**. Therefore, **NO SENSITIVE DATA** (API keys, Keystore passwords, Signing keys) should ever be committed to the codebase.

### How we handle secrets:
1.  **CI/CD (GitHub Actions):** All sensitive keys are stored in **GitHub Secrets**.
    *   `KEYSTORE_BASE64`: The Base64 encoded release keystore.
    *   `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`: Keystore credentials.
2.  **Local Development:**
    *   Developers use a local `keystore.properties` file which is **ignored by git** (`.gitignore`).
    *   If `keystore.properties` is missing, the build script falls back to a debug keystore or environment variables.
3.  **Mock Data:**
    *   The app currently uses a `MockAdRepository` for development.
    *   Future backend integration must assume the client is untrusted. All credit validation logic must happen on the server.

### Integrity Checks
*   **Play Integrity API:** The app is configured to use Play Integrity. The server (when implemented) must verify the integrity token to prevent cheated ad views.
*   **Ad Logic:** Ad rewards are verified via server-side callbacks (SSV) from the Ad provider (AdMob), not just by client-side success callbacks.

## Development Guidelines for Security
*   **Do not** hardcode URLs that contain sensitive tokens.
*   **Do not** log sensitive user data (PII) in production builds.
*   **Use** `LogUtils` which can be stripped or disabled in release builds.
*   **Review** `proguard-rules.pro` to ensure obfuscation is active for release builds.
