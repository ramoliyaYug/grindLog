package com.example.grindlog.presentation.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grindlog.presentation.components.DayCountdownTimer
import com.example.grindlog.presentation.components.PlatformCard
import com.example.grindlog.presentation.components.AnalysisCard
import com.example.grindlog.presentation.components.JournalDialog
import com.example.grindlog.presentation.components.TargetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailyEntry by viewModel.dailyEntry.collectAsState()
    val dailyAnalysis by viewModel.dailyAnalysis.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            DayCountdownTimer()
        }

        item {
            AnalysisCard(
                title = "Overall Progress",
                percentage = dailyAnalysis.overallCompletionPercentage,
                subtitle = "${dailyAnalysis.totalProblems}/${dailyAnalysis.totalTargets} problems solved"
            )
        }

        items(dailyAnalysis.platformStats) { platformStat ->
            PlatformCard(
                platform = platformStat.platform,
                count = platformStat.count,
                target = platformStat.target,
                onCountChange = { newCount ->
                    viewModel.updatePlatformCount(platformStat.platform.lowercase(), newCount)
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.showTargetDialog() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Targets")
                }

                Button(
                    onClick = { viewModel.showJournalDialog() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Note, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Journal")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Todo Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = dailyAnalysis.todoCompletionPercentage / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${dailyAnalysis.todosCompleted}/${dailyAnalysis.totalTodos} todos completed (${dailyAnalysis.todoCompletionPercentage.toInt()}%)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (uiState.showJournalDialog) {
        JournalDialog(
            title = uiState.journalTitle,
            content = uiState.journalContent,
            onTitleChange = viewModel::updateJournalTitle,
            onContentChange = viewModel::updateJournalContent,
            onSave = viewModel::saveJournalNote,
            onDismiss = viewModel::hideJournalDialog
        )
    }

    if (uiState.showTargetDialog) {
        TargetDialog(
            dailyEntry = dailyEntry,
            onAllTargetsChange = viewModel::updateAllTargets,
            onDismiss = viewModel::hideTargetDialog
        )
    }
}
