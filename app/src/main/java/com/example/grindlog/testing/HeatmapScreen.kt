package com.example.grindlog.testing

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.*

// Data class representing one day in the heatmap
data class HeatmapDay(
    val date: LocalDate,
    val total: Int
)

@Composable
fun HeatmapScreen(days: List<HeatmapDay>) {
    // Scrollable container for the heatmap
    Column(
        modifier = Modifier
            .padding(16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Text("Yearly Heatmap", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        YearlyHeatmap(days)
        Spacer(Modifier.height(8.dp))
        HeatmapLegend()
    }
}

@Composable
fun YearlyHeatmap(days: List<HeatmapDay>) {
    // Split days into weeks (7 days per column)
    val weeks = days.chunked(7)

    Row {
        weeks.forEach { week ->
            Column(
                modifier = Modifier.padding(end = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                week.forEach { day ->
                    DayBox(day)
                }
            }
        }
    }
}

@Composable
fun DayBox(day: HeatmapDay) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(getColorFor(day.total))
            .clickable { showPopup = true }
    )

    // Tooltip or popup on click
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("OK")
                }
            },
            title = { Text(day.date.toString()) },
            text = { Text("Solved: ${day.total} problems") }
        )
    }
}

// Function to map 'total' to specific colors
fun getColorFor(total: Int): Color {
    return when {
        total == 0 -> Color(0xFFEEEEEE)         // ▢
        total in 1..5 -> Color(0xFFAED581)      // ▤
        total in 6..10 -> Color(0xFF81C784)     // ▥
        total in 11..20 -> Color(0xFF4CAF50)    // ▦
        total > 20 -> Color(0xFF2E7D32)         // ▧
        else -> Color.White
    }
}

// Optional: Show a simple color legend
@Composable
fun HeatmapLegend() {
    val labels = listOf("0", "1-5", "6-10", "11-20", "20+")
    val colors = listOf(0, 1, 6, 11, 21).map { getColorFor(it) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Legend: ", style = MaterialTheme.typography.bodySmall)
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color)
                    .padding(2.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(labels[index], style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.width(8.dp))
        }
    }
}

// Fake data for preview/testing
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun generateSampleHeatmapData(): List<HeatmapDay> {
    val today = LocalDate.now()
    val start = today.minusDays(364)
    val random = Random()

    return (0..364).map { offset ->
        val date = start.plusDays(offset.toLong())
        val total = when (random.nextInt(100)) {
            in 0..40 -> 0
            in 41..60 -> random.nextInt(1, 6)
            in 61..80 -> random.nextInt(6, 11)
            in 81..95 -> random.nextInt(11, 21)
            else -> random.nextInt(21, 40)
        }
        HeatmapDay(date, total)
    }
}

// Preview for testing the layout
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Preview(showBackground = true)
@Composable
fun PreviewHeatmap() {
    HeatmapScreen(generateSampleHeatmapData())
}