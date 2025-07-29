package com.example.grindlog.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindlog.domain.model.HeatmapData
import kotlin.math.floor

@Composable
fun HeatmapView(
    data: List<HeatmapData>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val scrollState = rememberScrollState()

    // Calculate statistics
    val maxCount = data.maxOfOrNull { it.count } ?: 0
    val activeDaysCount = data.count { it.count > 0 }
    val totalCount = data.sumOf { it.count }

    Column(modifier = modifier) {
        // Real data debug info
        Text(
            text = "Debug: $activeDaysCount active days, max count: $maxCount, total: $totalCount",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Data points: ${data.size} (Real Data)",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Show recent activity for debugging
        val recentActivity = data.takeLast(30).count { it.count > 0 }
        Text(
            text = "Recent 30 days activity: $recentActivity days",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        // Legend
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Less",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Legend squares showing different intensity levels
            repeat(5) { index ->
                Canvas(
                    modifier = Modifier.size(12.dp)
                ) {
                    val color = getColorForCount(index, 4, primaryColor, surfaceVariant)
                    drawRect(color = color, size = size)
                }
            }

            Text(
                text = "More",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Heatmap
        Box(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .width(1060.dp) // 53 weeks * 20dp per week
                    .height(160.dp) // 7 days * 20dp per day + space for labels
            ) {
                drawHeatmap(data, primaryColor, surfaceVariant, maxCount)
            }
        }
    }
}

private fun DrawScope.drawHeatmap(
    data: List<HeatmapData>,
    primaryColor: Color,
    surfaceColor: Color,
    maxCount: Int
) {
    val cellSize = 12.dp.toPx()
    val cellSpacing = 3.dp.toPx()
    val weeksInYear = 53

    data.forEachIndexed { index, heatmapData ->
        val week = floor(index / 7.0).toInt()
        val day = index % 7

        if (week < weeksInYear) {
            val x = week * (cellSize + cellSpacing)
            val y = day * (cellSize + cellSpacing) + 20.dp.toPx()

            val color = getColorForCount(heatmapData.count, maxCount, primaryColor, surfaceColor)

            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun getColorForCount(count: Int, maxCount: Int, primaryColor: Color, surfaceColor: Color): Color {
    return when {
        count == 0 -> surfaceColor
        count == 1 -> primaryColor.copy(alpha = 0.3f)
        count <= 3 -> primaryColor.copy(alpha = 0.5f)
        count <= 6 -> primaryColor.copy(alpha = 0.7f)
        count <= 10 -> primaryColor.copy(alpha = 0.85f)
        else -> primaryColor.copy(alpha = 1f)
    }
}
