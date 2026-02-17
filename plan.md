# Implementation Plan - Pterodactyl Panel Integration

## Goal
Integrate a fully functional Pterodactyl Minecraft Server Panel into the "Hexium Nodes" Android app. This includes a new navigation structure, a "Smart Home" for server selection, and feature-rich Dashboard, Console, and File Manager screens. The UI will strictly follow Material You (Material 3) guidelines.

## Architecture & Navigation
1.  **Navigation Refactor:**
    -   **MainScreen:** Uses `Scaffold` with a Bottom Navigation Bar containing "Servers", "Rewards", and "Account" tabs.
    -   **Panel Architecture:** Server management uses a Sidebar (Drawer) navigation structure (`PanelScreen`), hiding the main app bars to focus on server tasks.
    -   **Smart Home Logic:** Automatically navigates to the single server panel if only one server is available.

## Data Layer
2.  **Pterodactyl API:**
    -   **Service:** Retrofit service for REST endpoints (`/api/client/...`).
    -   **Repository:** `PterodactylRepository` handles data fetching and caching.
    -   **Features:** Server List, Resources, Power, Files, Backups, Network, Users, Startup.
    -   **WebSocket:** Uses OkHttp for real-time Console streaming.
    -   **Security:** API Key is stored in `EncryptedSharedPreferences`.

## Feature Implementation

### 1. Panel Features
-   **Dashboard:**
    -   Live status and visual power controls (Start/Stop/Restart/Kill).
    -   Real-time resource monitoring (CPU, RAM, Disk) with progress bars.
    -   Unlimited resources displayed as "∞".
-   **Console:**
    -   Real-time ANSI-colored logs (stripped for display).
    -   Command input.
-   **File Manager:**
    -   Browse, Upload, Download, Edit, Rename, Delete.
    -   Integrated Code Editor.
-   **Backups:** List and create server backups.
-   **Network:** View allocations (IP/Port).
-   **Users:** View sub-users.
-   **Startup:** View startup variables.

### 2. Account & Settings
-   **Account Tab:** Dedicated screen for user profile and logout.
-   **Settings:** App preferences (Theme, Dynamic Colors) and Developer Options.

### 3. Cloudflare Verification
-   WebView-based verification flow.
-   Automatic cookie clearing to prevent 403 loops.

## UI/UX Polish
-   Consistent Material 3 styling (Cards, FABs, Navigation Drawer).
-   Error handling with Retry buttons.
-   Loading indicators for async operations.
