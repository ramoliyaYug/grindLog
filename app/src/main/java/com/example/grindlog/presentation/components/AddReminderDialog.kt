package com.example.grindlog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.grindlog.presentation.reminder.ReminderUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    uiState: ReminderUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Date) -> Unit,
    onTimeChange: (String) -> Unit,
    onPlatformChange: (String) -> Unit,
    onContestToggle: (Boolean) -> Unit,
    onNotifyBeforeChange: (Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val platforms = listOf("LeetCode", "Codeforces", "CodeChef", "GeeksforGeeks", "AtCoder", "TopCoder")
    val notifyOptions = listOf(
        15 to "15 minutes before",
        30 to "30 minutes before",
        60 to "1 hour before",
        120 to "2 hours before",
        1440 to "1 day before"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add Reminder",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.reminderTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.reminderDescription,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        if (uiState.selectedDate == null) {
                            val tomorrow = Calendar.getInstance()
                            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
                            onDateChange(tomorrow.time)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (uiState.selectedDate != null)
                            "Date: ${dateFormat.format(uiState.selectedDate)}"
                        else "Select Date"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.selectedTime,
                    onValueChange = onTimeChange,
                    label = { Text("Time (HH:MM)") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    placeholder = { Text("09:00") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                var expandedNotify by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedNotify,
                    onExpandedChange = { expandedNotify = !expandedNotify }
                ) {
                    OutlinedTextField(
                        value = notifyOptions.find { it.first == uiState.notifyBefore }?.second ?: "1 hour before",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Notify me") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNotify) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedNotify,
                        onDismissRequest = { expandedNotify = false }
                    ) {
                        notifyOptions.forEach { (minutes, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    onNotifyBeforeChange(minutes)
                                    expandedNotify = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = uiState.isContestReminder,
                            onClick = { onContestToggle(!uiState.isContestReminder) },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isContestReminder,
                        onCheckedChange = onContestToggle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contest Reminder")
                }

                if (uiState.isContestReminder) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedPlatform,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Platform") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            platforms.forEach { platform ->
                                DropdownMenuItem(
                                    text = { Text(platform) },
                                    onClick = {
                                        onPlatformChange(platform)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        enabled = uiState.reminderTitle.isNotBlank() && uiState.selectedDate != null
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
