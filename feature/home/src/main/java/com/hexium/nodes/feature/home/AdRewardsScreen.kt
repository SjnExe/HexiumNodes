package com.hexium.nodes.feature.home

import android.app.Activity
import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.ui.R
import com.hexium.nodes.feature.home.ads.BannerAd
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun AdRewardsScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val credits by viewModel.credits.collectAsState()
    val availableAds by viewModel.availableAds.collectAsState()
    val maxAds by viewModel.maxAds.collectAsState()
    val history by viewModel.history.collectAsState()
    val username by viewModel.username.collectAsState()
    val adRate by viewModel.adRate.collectAsState()
    val adExpiryHours by viewModel.adExpiryHours.collectAsState()
    val isAdLoaded by viewModel.isAdLoaded.collectAsState()
    val adCooldownSeconds by viewModel.adCooldownSeconds.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Credits Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.current_balance),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatCredits(credits),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                if (username != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.user_label, username!!),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ad Status
        Text(
            text = stringResource(R.string.daily_ad_limit, availableAds, maxAds),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val isOnCooldown = adCooldownSeconds > 0
        val isEnabled = availableAds > 0 && isAdLoaded && !isOnCooldown

        Button(
            onClick = {
                if (isEnabled) {
                    activity?.let { viewModel.watchAd(it) }
                }
            },
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isOnCooldown) {
                    Text("Next ad in ${adCooldownSeconds}s")
                } else if (isAdLoaded) {
                    Text(stringResource(R.string.watch_ad))
                    Text(stringResource(R.string.earn_credits, adRate.toString()), style = MaterialTheme.typography.labelSmall)
                } else {
                    Text("Loading Ad...")
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(onClick = { viewModel.refreshData() }) {
                Text(stringResource(R.string.refresh))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(history) { timestamp ->
                HistoryItem(timestamp, adExpiryHours)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner Ad
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            shape = MaterialTheme.shapes.small,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                BannerAd()
            }
        }
    }
}

@Composable
fun HistoryItem(timestamp: Long, expiryHours: Int) {
    val now = System.currentTimeMillis()
    val expiryTime = timestamp + (expiryHours.toLong() * DateUtils.HOUR_IN_MILLIS)
    val remaining = expiryTime - now

    val relativeTime = if (remaining <= 0) {
        stringResource(R.string.expired)
    } else if (remaining < DateUtils.HOUR_IN_MILLIS) {
        stringResource(R.string.expires_in_minutes, remaining / DateUtils.MINUTE_IN_MILLIS)
    } else {
        stringResource(R.string.expires_in_hours, remaining / DateUtils.HOUR_IN_MILLIS)
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    val absoluteTime = dateFormat.format(Date(timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.watched_ad),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = relativeTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = absoluteTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatCredits(credits: Double): String {
    val formatted = String.format(Locale.US, "%.2f", credits)
    return if (formatted.endsWith(".00")) {
        formatted.substring(0, formatted.length - 3)
    } else if (formatted.endsWith("0") && formatted.contains(".")) {
        formatted.substring(0, formatted.length - 1)
    } else {
        formatted
    }
}
