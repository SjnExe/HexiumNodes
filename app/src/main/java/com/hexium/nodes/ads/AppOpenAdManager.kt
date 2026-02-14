package com.hexium.nodes.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.hexium.nodes.core.common.util.LogUtils
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdManager @Inject constructor(
    private val application: Application,
) : DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null

    companion object {
        // Test Ad Unit IDs
        private const val AD_UNIT_ID_PORTRAIT = "ca-app-pub-3940256099942544/3419835294"
        private const val AD_UNIT_ID_LANDSCAPE = "ca-app-pub-3940256099942544/5662855259"
        private const val TAG = "AppOpenAdManager"
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        // Note: ProcessLifecycleOwner observer is added in HexiumApp to control initialization timing
    }

    override fun onStart(owner: LifecycleOwner) {
        // This is called when the App moves to Foreground (or on first launch if already foreground)
        showAdIfAvailable()
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        val orientation = context.resources.configuration.orientation
        val adUnitId = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            AD_UNIT_ID_LANDSCAPE
        } else {
            AD_UNIT_ID_PORTRAIT
        }

        AppOpenAd.load(
            context,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    LogUtils.d(TAG, "App Open Ad loaded ($adUnitId).")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    LogUtils.e(TAG, "App Open Ad failed to load ($adUnitId): ${loadAdError.code} - ${loadAdError.message} - ${loadAdError.domain}", null)
                }
            },
        )
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            currentActivity?.let { activity ->
                appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        loadAd(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        isShowingAd = false
                        appOpenAd = null
                        loadAd(activity)
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
                isShowingAd = true
                appOpenAd?.show(activity)
            } ?: run {
                // If no activity is available, we can't show.
                // But we can try to reload if expired.
                loadAd(application)
            }
        } else {
            loadAd(application)
        }
    }

    private fun isAdAvailable(): Boolean = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }

    // ActivityLifecycleCallbacks
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
}
