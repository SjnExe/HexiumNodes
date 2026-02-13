package com.hexium.nodes

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.hexium.nodes.ads.AppOpenAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HexiumApp : Application() {

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) { }

        // Register Lifecycle Observer
        // This will trigger 'onStart' when the app is in foreground.
        // It will NOT trigger 'onStart' if the app is started in background (Service/Receiver).
        ProcessLifecycleOwner.get().lifecycle.addObserver(appOpenAdManager)

        // Removed explicit loadAd() here to prevent background network calls on process creation.
        // loadAd() will be called by appOpenAdManager.onStart() when UI is visible.
    }
}
