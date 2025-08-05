package com.example.grindlog.presentation.today

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.DailyEntry
import com.example.grindlog.data.local.entity.JournalNote
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.JournalNoteRepository
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.notification.DailySummaryScheduler
import com.example.grindlog.domain.usecase.GetDailyAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    var timeLeft by mutableStateOf(getTimeLeftInDay())

    val triggerTimeForTesting = System.currentTimeMillis() + 30_000L

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                 timeLeft = getTimeLeftInDay()
                 if (timeLeft == 60_000L) {
                     val realAnalysis: DailyAnalysis = dailyAnalysis.value
                     dailySummaryScheduler.showDailySummaryNotification(realAnalysis)
                 }

//                if (System.currentTimeMillis() > triggerTimeForTesting) {
//                    val realAnalysis: DailyAnalysis = dailyAnalysis.value
//                    dailySummaryScheduler.showDailySummaryNotification(realAnalysis)
//                    break
//                }
            }
        }
    }

    private fun getTimeLeftInDay(): Long {
        val now = Calendar.getInstance()
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return endOfDay.timeInMillis - now.timeInMillis
    }

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
