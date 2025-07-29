package com.example.grindlog.presentation.todo

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
import com.example.grindlog.presentation.components.TodoCard
import com.example.grindlog.presentation.components.AddTodoDialog
import com.example.grindlog.presentation.components.TodoStatsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val todayTodos by viewModel.todayTodos.collectAsState()
    val allTodos by viewModel.allTodos.collectAsState()
    val todoStats by viewModel.todoStats.collectAsState()

    val displayTodos = when (uiState.viewMode) {
        TodoViewMode.TODAY -> todayTodos
        TodoViewMode.ALL -> allTodos
    }

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
                text = "Todo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { viewModel.showAddTodoDialog() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // View Mode Toggle
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TodoViewMode.values().forEach { mode ->
                FilterChip(
                    onClick = { viewModel.updateViewMode(mode) },
                    label = { Text(mode.displayName) },
                    selected = uiState.viewMode == mode
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Card (only for today view)
        if (uiState.viewMode == TodoViewMode.TODAY) {
            TodoStatsCard(stats = todoStats)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Todo List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayTodos) { todo ->
                TodoCard(
                    todo = todo,
                    onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                    onDelete = { viewModel.deleteTodo(todo) }
                )
            }

            if (displayTodos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.viewMode == TodoViewMode.TODAY) "No todos for today" else "No todos yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add your first todo to get started!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddTodoDialog) {
        AddTodoDialog(
            title = uiState.todoTitle,
            description = uiState.todoDescription,
            onTitleChange = viewModel::updateTodoTitle,
            onDescriptionChange = viewModel::updateTodoDescription,
            onSave = viewModel::saveTodo,
            onDismiss = viewModel::hideAddTodoDialog
        )
    }
}
