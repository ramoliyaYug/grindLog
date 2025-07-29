package com.example.grindlog.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.JournalNote
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.JournalNoteRepository
import com.example.grindlog.data.repository.ReminderRepository
import com.example.grindlog.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val journalNoteRepository: JournalNoteRepository,
    private val reminderRepository: ReminderRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val journalNotes: StateFlow<List<JournalNote>> = journalNoteRepository.getAllJournalNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val filteredJournalNotes: StateFlow<List<JournalNote>> = combine(
        journalNotes,
        _uiState.map { it.searchQuery }
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun showDeleteDataDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDataDialog = true)
    }

    fun hideDeleteDataDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDataDialog = false)
    }

    fun deleteAllData() {
        viewModelScope.launch {
            dailyEntryRepository.deleteAllDailyEntries()
            journalNoteRepository.deleteAllJournalNotes()
            reminderRepository.deleteAllReminders()
            todoRepository.deleteAllTodos()
            hideDeleteDataDialog()
        }
    }

    fun toggleTheme() {
        _uiState.value = _uiState.value.copy(isDarkTheme = !_uiState.value.isDarkTheme)
    }

    fun deleteJournalNote(note: JournalNote) {
        viewModelScope.launch {
            journalNoteRepository.deleteJournalNote(note)
            hideJournalDetailDialog()
        }
    }

    fun showJournalDetailDialog(note: JournalNote) {
        _uiState.value = _uiState.value.copy(
            showJournalDetailDialog = true,
            selectedJournalNote = note
        )
    }

    fun hideJournalDetailDialog() {
        _uiState.value = _uiState.value.copy(
            showJournalDetailDialog = false,
            selectedJournalNote = null
        )
    }
}

data class ProfileUiState(
    val searchQuery: String = "",
    val showDeleteDataDialog: Boolean = false,
    val isDarkTheme: Boolean = false,
    val showJournalDetailDialog: Boolean = false,
    val selectedJournalNote: JournalNote? = null
)
