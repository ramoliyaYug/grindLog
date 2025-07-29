package com.example.grindlog.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.Todo
import com.example.grindlog.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    val todayTodos: StateFlow<List<Todo>> = todoRepository.getTodosByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val allTodos: StateFlow<List<Todo>> = todoRepository.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val todoStats: StateFlow<TodoStats> = todayTodos.map { todos ->
        val completed = todos.count { it.isCompleted }
        val total = todos.size
        val completionRate = if (total > 0) (completed.toFloat() / total * 100) else 0f
        TodoStats(completed, total, completionRate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TodoStats(0, 0, 0f))

    fun showAddTodoDialog() {
        _uiState.value = _uiState.value.copy(showAddTodoDialog = true)
    }

    fun hideAddTodoDialog() {
        _uiState.value = _uiState.value.copy(
            showAddTodoDialog = false,
            todoTitle = "",
            todoDescription = ""
        )
    }

    fun updateTodoTitle(title: String) {
        _uiState.value = _uiState.value.copy(todoTitle = title)
    }

    fun updateTodoDescription(description: String) {
        _uiState.value = _uiState.value.copy(todoDescription = description)
    }

    fun saveTodo() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.todoTitle.isNotBlank()) {
                val todo = Todo(
                    title = state.todoTitle,
                    description = state.todoDescription,
                    date = today
                )
                todoRepository.insertTodo(todo)
                hideAddTodoDialog()
            }
        }
    }

    fun toggleTodoCompletion(todo: Todo) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
            todoRepository.updateTodo(updatedTodo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
        }
    }

    fun updateViewMode(viewMode: TodoViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
    }
}

data class TodoUiState(
    val showAddTodoDialog: Boolean = false,
    val todoTitle: String = "",
    val todoDescription: String = "",
    val viewMode: TodoViewMode = TodoViewMode.TODAY
)

data class TodoStats(
    val completed: Int,
    val total: Int,
    val completionRate: Float
)

enum class TodoViewMode(val displayName: String) {
    TODAY("Today"),
    ALL("All")
}
