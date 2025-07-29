package com.example.grindlog.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.DailyEntry
import com.example.grindlog.data.local.entity.JournalNote
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.JournalNoteRepository
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.usecase.GetDailyAnalysisUseCase
import com.example.grindlog.domain.notification.DailySummaryScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val journalNoteRepository: JournalNoteRepository,
    private val getDailyAnalysisUseCase: GetDailyAnalysisUseCase,
    private val dailySummaryScheduler: DailySummaryScheduler
) : ViewModel() {

    init {
        // Schedule daily summary notifications
        viewModelScope.launch {
            dailySummaryScheduler.scheduleDailySummary()
        }
    }

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    val dailyEntry: StateFlow<DailyEntry?> = dailyEntryRepository.getDailyEntryFlow(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val dailyAnalysis: StateFlow<DailyAnalysis> = getDailyAnalysisUseCase(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),
            DailyAnalysis(0, 0, 0f, emptyList(), 0, 0, 0f))

    fun updatePlatformCount(platform: String, count: Int) {
        viewModelScope.launch {
            val currentEntry = dailyEntry.value ?: DailyEntry(date = today)
            val updatedEntry = when (platform) {
                "leetcode" -> currentEntry.copy(leetcodeCount = count)
                "codeforces" -> currentEntry.copy(codeforcesCount = count)
                "codechef" -> currentEntry.copy(codechefCount = count)
                "geeksforgeeks" -> currentEntry.copy(geeksforgeeksCount = count)
                else -> currentEntry
            }
            dailyEntryRepository.insertOrUpdateDailyEntry(updatedEntry)
        }
    }

    fun updateAllTargets(
        leetcodeTarget: Int,
        codeforcesTarget: Int,
        codechefTarget: Int,
        geeksforgeeksTarget: Int
    ) {
        viewModelScope.launch {
            val currentEntry = dailyEntry.value ?: DailyEntry(date = today)
            val updatedEntry = currentEntry.copy(
                leetcodeTarget = leetcodeTarget,
                codeforcesTarget = codeforcesTarget,
                codechefTarget = codechefTarget,
                geeksforgeeksTarget = geeksforgeeksTarget
            )
            dailyEntryRepository.insertOrUpdateDailyEntry(updatedEntry)
        }
    }

    fun showJournalDialog() {
        _uiState.value = _uiState.value.copy(showJournalDialog = true)
    }

    fun hideJournalDialog() {
        _uiState.value = _uiState.value.copy(showJournalDialog = false, journalTitle = "", journalContent = "")
    }

    fun updateJournalTitle(title: String) {
        _uiState.value = _uiState.value.copy(journalTitle = title)
    }

    fun updateJournalContent(content: String) {
        _uiState.value = _uiState.value.copy(journalContent = content)
    }

    fun saveJournalNote() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.journalTitle.isNotBlank() && state.journalContent.isNotBlank()) {
                val journalNote = JournalNote(
                    title = state.journalTitle,
                    content = state.journalContent,
                    date = today
                )
                journalNoteRepository.insertJournalNote(journalNote)
                hideJournalDialog()
            }
        }
    }

    fun showTargetDialog() {
        _uiState.value = _uiState.value.copy(showTargetDialog = true)
    }

    fun hideTargetDialog() {
        _uiState.value = _uiState.value.copy(showTargetDialog = false)
    }
}

data class TodayUiState(
    val showJournalDialog: Boolean = false,
    val showTargetDialog: Boolean = false,
    val journalTitle: String = "",
    val journalContent: String = ""
)
