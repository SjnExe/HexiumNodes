package com.hexium.nodes.ui.home

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hexium.nodes.ui.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val credits by viewModel.credits.collectAsState()
    val availableAds by viewModel.availableAds.collectAsState()
    val maxAds by viewModel.maxAds.collectAsState()
    val history by viewModel.history.collectAsState()
    val username by viewModel.username.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hexium Nodes",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(onClick = { viewModel.logout() }) {
                    Text("Logout")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Credits Card
            if (username != null) {
                Text("Welcome, $username", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Current Balance", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = String.format("%.2f", credits),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ad Status
            Text("Daily Ad Limit: $availableAds / $maxAds")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.watchAd() },
                enabled = availableAds > 0
            ) {
                Text("Watch Ad (+1.00)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("History", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(history) { timestamp ->
                    HistoryItem(timestamp)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(timestamp: Long) {
    val relativeTime = DateUtils.getRelativeTimeSpanString(
        timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    )

    // Calculate when this slot becomes available
    val nextAvailableTime = timestamp + (24 * 60 * 60 * 1000)
    val availableIn = DateUtils.getRelativeTimeSpanString(
        nextAvailableTime,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Watched: $relativeTime")
            Text("Available: $availableIn")
        }
    }
}
