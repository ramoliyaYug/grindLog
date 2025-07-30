package com.example.grindlog.presentation.today

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grindlog.presentation.components.*
import com.example.grindlog.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailyEntry by viewModel.dailyEntry.collectAsState()
    val dailyAnalysis by viewModel.dailyAnalysis.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Progress",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Keep grinding! 🚀",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    FloatingActionButton(
                        onClick = { viewModel.showJournalDialog() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 12.dp,
                            pressedElevation = 16.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Journal",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            item {
                DayCountdownTimer()
            }

            item {
                PremiumAnalysisCard(
                    title = "Overall Progress",
                    percentage = dailyAnalysis.overallCompletionPercentage,
                    subtitle = "${dailyAnalysis.totalProblems}/${dailyAnalysis.totalTargets} problems solved",
                    gradient = listOf(
                        PremiumPurple,
                        PremiumBlue,
                        PremiumTeal
                    )
                )
            }

            items(dailyAnalysis.platformStats) { platformStat ->
                val platformGradient = when (platformStat.platform) {
                    "LeetCode" -> listOf(PremiumOrange, PremiumRed)
                    "Codeforces" -> listOf(PremiumBlue, PremiumPurple)
                    "CodeChef" -> listOf(PremiumGreen, PremiumTeal)
                    "GeeksforGeeks" -> listOf(PremiumTeal, PremiumGreen)
                    else -> listOf(PremiumPurple, PremiumBlue)
                }

                PremiumPlatformCard(
                    platform = platformStat.platform,
                    count = platformStat.count,
                    target = platformStat.target,
                    onCountChange = { newCount ->
                        viewModel.updatePlatformCount(platformStat.platform.lowercase(), newCount)
                    },
                    gradient = platformGradient
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientButton(
                        text = "Set Targets",
                        onClick = { viewModel.showTargetDialog() },
                        icon = Icons.Default.Flag,
                        modifier = Modifier.weight(1f),
                        gradient = listOf(PremiumPurple, PremiumBlue)
                    )

                    GradientButton(
                        text = "Add Journal",
                        onClick = { viewModel.showJournalDialog() },
                        icon = Icons.Default.Note,
                        modifier = Modifier.weight(1f),
                        gradient = listOf(PremiumTeal, PremiumGreen)
                    )
                }
            }

            item {
                PremiumCard(
                    gradient = listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Todo Progress",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${dailyAnalysis.todosCompleted}/${dailyAnalysis.totalTodos} completed",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = dailyAnalysis.todoCompletionPercentage / 100f,
                                modifier = Modifier.size(60.dp),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.tertiary,
                                trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                            )
                            Text(
                                text = "${dailyAnalysis.todoCompletionPercentage.toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = dailyAnalysis.todoCompletionPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
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
