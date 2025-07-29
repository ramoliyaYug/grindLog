package com.example.grindlog.presentation.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grindlog.presentation.components.ReminderCard
import com.example.grindlog.presentation.components.AddReminderDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val upcomingReminders by viewModel.upcomingReminders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminders",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { viewModel.showAddReminderDialog() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (upcomingReminders.isNotEmpty()) {
            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(upcomingReminders) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    isUpcoming = true,
                    onToggleActive = { viewModel.toggleReminderActive(reminder) },
                    onDelete = { viewModel.deleteReminder(reminder) }
                )
            }

            if (upcomingReminders.isNotEmpty() && reminders.size > upcomingReminders.size) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "All Reminders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(reminders.filter { !upcomingReminders.contains(it) }) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    isUpcoming = false,
                    onToggleActive = { viewModel.toggleReminderActive(reminder) },
                    onDelete = { viewModel.deleteReminder(reminder) }
                )
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
