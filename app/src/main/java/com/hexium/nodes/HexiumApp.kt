package com.hexium.nodes

import android.app.Application
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
        // Force initialization and initial load
        appOpenAdManager.loadAd(this)
    }
}
