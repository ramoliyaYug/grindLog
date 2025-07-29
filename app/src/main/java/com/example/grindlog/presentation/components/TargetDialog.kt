package com.example.grindlog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.grindlog.data.local.entity.DailyEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDialog(
    dailyEntry: DailyEntry?,
    onAllTargetsChange: (Int, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var leetcodeTarget by remember { mutableStateOf(dailyEntry?.leetcodeTarget?.toString() ?: "0") }
    var codeforcesTarget by remember { mutableStateOf(dailyEntry?.codeforcesTarget?.toString() ?: "0") }
    var codechefTarget by remember { mutableStateOf(dailyEntry?.codechefTarget?.toString() ?: "0") }
    var geeksforgeeksTarget by remember { mutableStateOf(dailyEntry?.geeksforgeeksTarget?.toString() ?: "0") }

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
                    text = "Set Today's Targets",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = leetcodeTarget,
                    onValueChange = { leetcodeTarget = it },
                    label = { Text("LeetCode Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codeforcesTarget,
                    onValueChange = { codeforcesTarget = it },
                    label = { Text("Codeforces Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codechefTarget,
                    onValueChange = { codechefTarget = it },
                    label = { Text("CodeChef Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = geeksforgeeksTarget,
                    onValueChange = { geeksforgeeksTarget = it },
                    label = { Text("GeeksforGeeks Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

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
                        onClick = {
                            // Save all targets at once
                            onAllTargetsChange(
                                leetcodeTarget.toIntOrNull() ?: 0,
                                codeforcesTarget.toIntOrNull() ?: 0,
                                codechefTarget.toIntOrNull() ?: 0,
                                geeksforgeeksTarget.toIntOrNull() ?: 0
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save All Targets")
                    }
                }
            }
        }
    }
}
