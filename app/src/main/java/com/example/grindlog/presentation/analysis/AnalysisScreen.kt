package com.example.grindlog.presentation.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grindlog.presentation.components.PlatformAnalyticsCard
import com.example.grindlog.presentation.components.FilterChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val platformAnalytics by viewModel.platformAnalytics.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            FilterChips(
                selectedDateRange = selectedDateRange,
                selectedPlatform = selectedPlatform,
                onDateRangeChange = viewModel::updateDateRange,
                onPlatformChange = viewModel::updateSelectedPlatform
            )
        }

        item {
            Text(
                text = "Platform Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(platformAnalytics) { analytics ->
            PlatformAnalyticsCard(analytics = analytics)
        }
    }
}
