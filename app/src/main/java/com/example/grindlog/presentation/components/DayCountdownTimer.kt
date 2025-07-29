package com.example.grindlog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCountdownTimer() {
    var timeLeft by remember { mutableStateOf(getTimeLeftInDay()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timeLeft = getTimeLeftInDay()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Timer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Day Ends In",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatTimeLeft(timeLeft),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            val dayProgress = getDayProgress()
            CircularProgressIndicator(
                progress = dayProgress,
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun getTimeLeftInDay(): Long {
    val now = Calendar.getInstance()
    val endOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return endOfDay.timeInMillis - now.timeInMillis
}

private fun getDayProgress(): Float {
    val now = Calendar.getInstance()
    val startOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val endOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    val totalDayTime = endOfDay.timeInMillis - startOfDay.timeInMillis
    val elapsedTime = now.timeInMillis - startOfDay.timeInMillis

    return (elapsedTime.toFloat() / totalDayTime).coerceIn(0f, 1f)
}

private fun formatTimeLeft(millisLeft: Long): String {
    if (millisLeft <= 0) return "00:00:00"

    val hours = millisLeft / (1000 * 60 * 60)
    val minutes = (millisLeft % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (millisLeft % (1000 * 60)) / 1000

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
