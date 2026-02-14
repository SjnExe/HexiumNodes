# AdMob Setup Instructions

This project has been configured with **Google AdMob** integration for the following ad formats:
1.  **App Open Ads:** Shows when the app is opened or returns from the background.
2.  **Rewarded Video Ads:** Shows when the user clicks "Watch Ad" to earn credits.
3.  **Banner Ads:** Shows a banner at the bottom of the Home Screen.

## 1. Current Configuration (Test Mode)
Currently, the app is hardcoded to use **Google's Test Ad Unit IDs**. This is the recommended way to develop and test your implementation without risking your AdMob account (invalid traffic policy violations).

*   **App Open ID:** `ca-app-pub-3940256099942544/3419835294`
*   **Rewarded Video ID:** `ca-app-pub-3940256099942544/5224354917`
*   **Banner ID:** `ca-app-pub-3940256099942544/6300978111`

**You do NOT need to add any secrets or keys to run the app right now.** Just build and run. You will see ads with a "Test Ad" label.

## 2. Moving to Production
When you are ready to release the app to the Play Store, follow these steps:

1.  **Create an AdMob Account:** Go to [admob.google.com](https://admob.google.com/).
2.  **Add Application:** Create a new App in AdMob.
3.  **Create Ad Units:** Create three ad units:
    *   App Open
    *   Rewarded
    *   Banner
4.  **Get IDs:** Copy the **App ID** and the **Ad Unit IDs**.
5.  **Update Code:**
    *   **App ID:** Update `app/src/main/AndroidManifest.xml` meta-data `com.google.android.gms.ads.APPLICATION_ID` with your real App ID.
    *   **Ad Unit IDs:** Ideally, store these in `gradle.properties` (locally) and GitHub Secrets (CI), and inject them via `BuildConfig`.
    *   For now, you can manually replace the IDs in:
        *   `app/src/main/java/com/hexium/nodes/ads/AppOpenAdManager.kt`
        *   `feature/home/src/main/java/com/hexium/nodes/feature/home/ads/RewardedAdManager.kt`
        *   `feature/home/src/main/java/com/hexium/nodes/feature/home/ads/BannerAd.kt`

## 3. Safety Features Added
*   **Debounce:** The "Watch Ad" button is disabled for 2 seconds after clicking to prevent accidental double-clicks.
*   **Loading State:** The button shows "Loading Ad..." until the ad is actually ready to show.
*   **Reward Verification:** Credits are only added to the user's balance *after* the ad has finished playing and the "Reward Earned" callback is received.
*   **Auto-Reload:** When an ad is closed or fails to show, a new one is automatically loaded in the background so it's ready for next time.

## 4. Troubleshooting
*   **"Loading Ad..." stuck:** Check your internet connection. Test ads usually load instantly, but real ads (or bad network) can take time.
*   **No App Open Ad:** It only shows on "Cold Start" (first launch) or after the app has been in the background for a while (4 hours limit logic implemented in `AppOpenAdManager` ensures we don't spam, but you can adjust `wasLoadTimeLessThanNHoursAgo` for testing).
