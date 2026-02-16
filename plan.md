# Implementation Plan - Pterodactyl Panel Integration

## Goal
Integrate a fully functional Pterodactyl Minecraft Server Panel into the "Hexium Nodes" Android app. This includes a new navigation structure, a "Smart Home" for server selection, and feature-rich Dashboard, Console, and File Manager screens. The UI will strictly follow Material You (Material 3) guidelines.

## Architecture & Navigation
1.  **Navigation Refactor:**
    -   **MainScreen:** Uses `Scaffold` with a Bottom Navigation Bar containing "Servers" and "Rewards" tabs.
    -   **Nested Graph:** The "Servers" tab uses a nested navigation graph (`servers_graph`) to handle `server_list` -> `server_detail` transitions while keeping the bottom bar visible.
    -   **Smart Home Logic:** Automatically navigates to the single server dashboard if only one server is available.

## Data Layer
2.  **Pterodactyl API:**
    -   **Service:** Retrofit service for REST endpoints (`/api/client/...`) including Server List, Resources, Power, Files, and Download.
    -   **Repository:** `PterodactylRepository` handles data fetching and caching.
    -   **WebSocket:** Uses OkHttp for real-time Console streaming.
    -   **Security:** API Key is stored in `EncryptedSharedPreferences`.
    -   **Models:** Data classes for `ServerAttributes`, `Resources`, `PowerSignal`, `FileData`, etc.

## Feature Implementation

### 1. Settings (Developer Options)
-   Added input field for **Pterodactyl API Key** in Developer Options.
-   Key is securely stored and used for all API requests.

### 2. Server List & Dashboard
-   **Server List:** Displays servers in Material Cards. Handles Loading/Error states.
-   **Dashboard:**
    -   **Status:** Shows current state (Running, Offline, etc.).
    -   **Power Controls:** Start, Stop, Restart, Kill buttons.
    -   **Resources:** Live usage stats for CPU, RAM, and Disk.

### 3. Console
-   **WebSocket:** Real-time bi-directional communication.
-   **UI:** Scrollable log view with command input field.
-   **Features:** ANSI color stripping, Auto-scroll, Clear logs.

### 4. File Manager
-   **Browser:** Navigate directories, view file details (size, mode).
-   **Editor:** View and Edit text files. Save changes back to server.
-   **Download:** Download files to device using Android `DownloadManager`.
-   **Upload:** Upload files from device using system file picker (`ActivityResultContracts.GetContent`).

## UI/UX Polish
-   Consistent Material 3 styling.
-   Error handling with Retry buttons.
-   Loading indicators for async operations.
