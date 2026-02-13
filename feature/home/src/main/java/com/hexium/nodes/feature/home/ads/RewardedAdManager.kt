package com.hexium.nodes.feature.home.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.hexium.nodes.core.common.util.LogUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var rewardedAd: RewardedAd? = null
    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private var isLoading = false

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // Google Test Rewarded Ad ID
        private const val TAG = "RewardedAdManager"
    }

    fun loadAd() {
        if (isLoading || rewardedAd != null) {
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                LogUtils.e(TAG, "Rewarded Ad failed to load: ${loadAdError.message}", null)
                rewardedAd = null
                isLoading = false
                _isAdLoaded.value = false
            }

            override fun onAdLoaded(ad: RewardedAd) {
                LogUtils.d(TAG, "Rewarded Ad was loaded.")
                rewardedAd = ad
                isLoading = false
                _isAdLoaded.value = true

                // Set callbacks for when ad is shown
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        // Ad is showing
                        rewardedAd = null
                        _isAdLoaded.value = false
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Ad failed to show
                        LogUtils.e(TAG, "Ad failed to show: ${adError.message}", null)
                        rewardedAd = null
                        _isAdLoaded.value = false
                        loadAd() // Try to reload
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Ad dismissed, load next one
                        LogUtils.d(TAG, "Ad dismissed.")
                        loadAd()
                    }
                }
            }
        })
    }

    fun showAd(activity: Activity, onUserEarnedReward: (Double) -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity, OnUserEarnedRewardListener { rewardItem ->
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                LogUtils.d(TAG, "User earned the reward: $rewardAmount $rewardType")
                onUserEarnedReward(rewardAmount.toDouble())
            })
        } ?: run {
            LogUtils.d(TAG, "The rewarded ad wasn't ready yet.")
            loadAd()
        }
    }
}
