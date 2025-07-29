package com.example.grindlog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grindlog.domain.model.DailyAnalysis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificDateAnalysisCard(
    analysis: DailyAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Daily Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Problems",
                    value = analysis.totalProblems.toString(),
                    subtitle = "solved"
                )

                SummaryItem(
                    title = "Target",
                    value = "${analysis.overallCompletionPercentage.toInt()}%",
                    subtitle = "completed"
                )

                SummaryItem(
                    title = "Todos",
                    value = "${analysis.todosCompleted}/${analysis.totalTodos}",
                    subtitle = "finished"
                )
            }

            if (analysis.totalTargets > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = analysis.overallCompletionPercentage / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    value: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
