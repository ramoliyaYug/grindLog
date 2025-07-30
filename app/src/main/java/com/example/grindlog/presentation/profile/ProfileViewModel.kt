package com.example.grindlog.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.JournalNote
import com.example.grindlog.data.local.entity.Reminder
import com.example.grindlog.data.notification.NotificationHelper
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.JournalNoteRepository
import com.example.grindlog.data.repository.ReminderRepository
import com.example.grindlog.data.repository.TodoRepository
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.model.PlatformStats
import com.example.grindlog.domain.notification.DailySummaryScheduler
import com.example.grindlog.domain.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val journalNoteRepository: JournalNoteRepository,
    private val reminderRepository: ReminderRepository,
    private val todoRepository: TodoRepository,
    private val notificationHelper: NotificationHelper,
    private val notificationScheduler: NotificationScheduler,
    private val dailySummaryScheduler: DailySummaryScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    val debugLogs: StateFlow<List<String>> = _debugLogs.asStateFlow()

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

    private fun addDebugLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "[$timestamp] $message"
        _debugLogs.value = _debugLogs.value + logEntry
    }

    fun clearDebugLogs() {
        _debugLogs.value = emptyList()
    }

    fun testReminderNotification() {
        viewModelScope.launch {
            try {
                addDebugLog("🔔 Testing reminder notification...")
                notificationHelper.showNotification(
                    id = 9999,
                    title = "🧪 Test Reminder",
                    content = "This is a test reminder notification. If you see this, reminders are working!",
                    isContest = false,
                    isDailySummary = false
                )
                addDebugLog("✅ Reminder notification sent successfully")
            } catch (e: Exception) {
                addDebugLog("❌ Reminder notification failed: ${e.message}")
            }
        }
    }

    fun testContestNotification() {
        viewModelScope.launch {
            try {
                addDebugLog("🏆 Testing contest notification...")
                notificationHelper.showNotification(
                    id = 9998,
                    title = "🏆 Contest Starting Soon!",
                    content = "LeetCode Weekly Contest starts in 1 hour. Get ready to code! 🚀",
                    isContest = true,
                    isDailySummary = false
                )
                addDebugLog("✅ Contest notification sent successfully")
            } catch (e: Exception) {
                addDebugLog("❌ Contest notification failed: ${e.message}")
            }
        }
    }

    fun testDailySummaryNotification() {
        viewModelScope.launch {
            try {
                addDebugLog("📊 Testing daily summary notification...")
                val mockAnalysis = DailyAnalysis(
                    totalProblems = 5,
                    totalTargets = 8,
                    overallCompletionPercentage = 62.5f,
                    platformStats = listOf(
                        PlatformStats("LeetCode", 3, 4, 75f),
                        PlatformStats("Codeforces", 2, 4, 50f)
                    ),
                    todosCompleted = 4,
                    totalTodos = 6,
                    todoCompletionPercentage = 66.7f
                )
                dailySummaryScheduler.showDailySummaryNotification(mockAnalysis)
                addDebugLog("✅ Daily summary notification sent successfully")
            } catch (e: Exception) {
                addDebugLog("❌ Daily summary notification failed: ${e.message}")
            }
        }
    }

    fun scheduleTestReminder() {
        viewModelScope.launch {
            try {
                addDebugLog("⏰ Scheduling test reminder for 10 seconds...")
                val testReminder = Reminder(
                    id = 9997,
                    title = "🧪 Scheduled Test Reminder",
                    description = "This reminder was scheduled 10 seconds ago for testing purposes",
                    dateTime = Date(System.currentTimeMillis() + 10000), // 10 seconds from now
                    isContestReminder = false,
                    platform = null,
                    isActive = true
                )

                notificationScheduler.scheduleReminderWithDelay(testReminder, 0) // No additional delay
                addDebugLog("✅ Test reminder scheduled successfully")
                addDebugLog("⏳ Reminder will trigger in 10 seconds...")
            } catch (e: Exception) {
                addDebugLog("❌ Failed to schedule test reminder: ${e.message}")
            }
        }
    }

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
