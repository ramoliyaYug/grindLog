package com.example.grindlog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.grindlog.presentation.analysis.DateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedDateRange: DateRange,
    selectedPlatform: String,
    onDateRangeChange: (DateRange) -> Unit,
    onPlatformChange: (String) -> Unit
) {
    val platforms = listOf("All", "LeetCode", "Codeforces", "CodeChef", "GeeksforGeeks")

    Column {
        Text(
            text = "Time Range",
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DateRange.values()) { dateRange ->
                FilterChip(
                    onClick = { onDateRangeChange(dateRange) },
                    label = { Text(dateRange.displayName) },
                    selected = selectedDateRange == dateRange
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Platform",
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(platforms) { platform ->
                FilterChip(
                    onClick = { onPlatformChange(platform) },
                    label = { Text(platform) },
                    selected = selectedPlatform == platform
                )
            }
        }
    }
}
