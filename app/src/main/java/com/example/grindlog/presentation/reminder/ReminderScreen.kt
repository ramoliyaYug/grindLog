package com.example.grindlog.presentation.reminder

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
import com.example.grindlog.presentation.components.PremiumReminderCard
import com.example.grindlog.presentation.components.AddReminderDialog
import com.example.grindlog.presentation.components.PremiumCard
import com.example.grindlog.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val upcomingReminders by viewModel.upcomingReminders.collectAsState()

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
                            text = "Reminders",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Never miss a contest! ⏰",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    FloatingActionButton(
                        onClick = { viewModel.showAddReminderDialog() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 12.dp,
                            pressedElevation = 16.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Reminder",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            if (upcomingReminders.isNotEmpty()) {
                item {
                    PremiumCard(
                        gradient = listOf(
                            PremiumPurple,
                            PremiumBlue
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Upcoming,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "🔥 Upcoming Reminders",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                items(upcomingReminders) { reminder ->
                    PremiumReminderCard(
                        reminder = reminder,
                        isUpcoming = true,
                        onToggleActive = { viewModel.toggleReminderActive(reminder) },
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                }
            }

            if (upcomingReminders.isNotEmpty() && reminders.size > upcomingReminders.size) {
                item {
                    PremiumCard(
                        gradient = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "All Reminders",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            val otherReminders = reminders.filter { !upcomingReminders.contains(it) }
            items(otherReminders) { reminder ->
                PremiumReminderCard(
                    reminder = reminder,
                    isUpcoming = false,
                    onToggleActive = { viewModel.toggleReminderActive(reminder) },
                    onDelete = { viewModel.deleteReminder(reminder) }
                )
            }

            if (reminders.isEmpty()) {
                item {
                    PremiumCard(
                        gradient = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⏰",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            Text(
                                text = "No reminders yet",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add your first reminder to never miss important events!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (uiState.showAddReminderDialog) {
        AddReminderDialog(
            uiState = uiState,
            onTitleChange = viewModel::updateReminderTitle,
            onDescriptionChange = viewModel::updateReminderDescription,
            onDateChange = viewModel::updateSelectedDate,
            onTimeChange = viewModel::updateSelectedTime,
            onPlatformChange = viewModel::updateSelectedPlatform,
            onContestToggle = viewModel::updateIsContestReminder,
            onNotifyBeforeChange = viewModel::updateNotifyBefore,
            onSave = viewModel::saveReminder,
            onDismiss = viewModel::hideAddReminderDialog
        )
    }
}
