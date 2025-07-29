package com.example.grindlog.presentation.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grindlog.presentation.components.PlatformAnalyticsCard
import com.example.grindlog.presentation.components.FilterChips
import com.example.grindlog.presentation.components.SpecificDateAnalysisCard
import com.example.grindlog.presentation.components.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val platformAnalytics by viewModel.platformAnalytics.collectAsState()
    val selectedSpecificDate by viewModel.selectedSpecificDate.collectAsState()
    val specificDateAnalysis by viewModel.specificDateAnalysis.collectAsState()

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalysisViewMode.values().forEach { mode ->
                    FilterChip(
                        onClick = { viewModel.updateViewMode(mode) },
                        label = { Text(mode.displayName) },
                        selected = uiState.viewMode == mode
                    )
                }
            }
        }

        when (uiState.viewMode) {
            AnalysisViewMode.DATE_RANGE -> {
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

                if (platformAnalytics.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📊",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No data available",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Start coding to see your analytics!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            AnalysisViewMode.SPECIFIC_DATE -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Select Date",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.showDatePicker() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Pick Date"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (selectedSpecificDate != null)
                                        "Selected: ${dateFormat.format(selectedSpecificDate)}"
                                    else "Pick a Date"
                                )
                            }
                        }
                    }
                }

                specificDateAnalysis?.let { analysis ->
                    item {
                        Text(
                            text = "Data for ${dateFormat.format(selectedSpecificDate!!)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        SpecificDateAnalysisCard(analysis = analysis)
                    }

                    items(analysis.platformStats) { platformStat ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = platformStat.platform,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${platformStat.count}/${platformStat.target}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (platformStat.target > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = (platformStat.count.toFloat() / platformStat.target).coerceAtMost(1f),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${platformStat.completionPercentage.toInt()}% completed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    if (selectedSpecificDate != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📅",
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No data for this date",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "No coding activity recorded for ${dateFormat.format(selectedSpecificDate!!)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.updateSelectedSpecificDate(date)
                viewModel.hideDatePicker()
            },
            onDismiss = { viewModel.hideDatePicker() }
        )
    }
}
