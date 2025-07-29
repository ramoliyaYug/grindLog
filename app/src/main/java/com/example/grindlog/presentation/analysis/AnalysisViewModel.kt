package com.example.grindlog.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.TodoRepository
import com.example.grindlog.domain.model.PlatformAnalytics
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.model.PlatformStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRange.WEEK)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("All")
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    private val _selectedSpecificDate = MutableStateFlow<Date?>(null)
    val selectedSpecificDate: StateFlow<Date?> = _selectedSpecificDate.asStateFlow()

    val platformAnalytics: StateFlow<List<PlatformAnalytics>> = combine(
        dailyEntryRepository.getAllDailyEntries(),
        selectedDateRange,
        selectedPlatform
    ) { entries, dateRange, platform ->
        if (_uiState.value.viewMode == AnalysisViewMode.DATE_RANGE) {
            calculatePlatformAnalytics(entries, dateRange, platform)
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val specificDateAnalysis: StateFlow<DailyAnalysis?> = combine(
        selectedSpecificDate,
        _uiState.map { it.viewMode }
    ) { date, viewMode ->
        if (viewMode == AnalysisViewMode.SPECIFIC_DATE && date != null) {
            getSpecificDateAnalysis(date)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun updateViewMode(viewMode: AnalysisViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
    }

    fun updateDateRange(dateRange: DateRange) {
        _selectedDateRange.value = dateRange
    }

    fun updateSelectedPlatform(platform: String) {
        _selectedPlatform.value = platform
    }

    fun updateSelectedSpecificDate(date: Date) {
        val normalizedDate = normalizeDate(date)
        _selectedSpecificDate.value = normalizedDate
    }

    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    private fun normalizeDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private suspend fun getSpecificDateAnalysis(date: Date): DailyAnalysis {
        val normalizedDate = normalizeDate(date)

        println("Searching for date: $normalizedDate (${normalizedDate.time})")

        val dailyEntry = dailyEntryRepository.getDailyEntry(normalizedDate)
        val todos = todoRepository.getTodosByDate(normalizedDate).first()

        println("Found daily entry: $dailyEntry")
        println("Found todos: ${todos.size}")

        val platformStats = if (dailyEntry != null) {
            listOf(
                PlatformStats("LeetCode", dailyEntry.leetcodeCount, dailyEntry.leetcodeTarget,
                    if (dailyEntry.leetcodeTarget > 0) (dailyEntry.leetcodeCount.toFloat() / dailyEntry.leetcodeTarget * 100) else 0f),
                PlatformStats("Codeforces", dailyEntry.codeforcesCount, dailyEntry.codeforcesTarget,
                    if (dailyEntry.codeforcesTarget > 0) (dailyEntry.codeforcesCount.toFloat() / dailyEntry.codeforcesTarget * 100) else 0f),
                PlatformStats("CodeChef", dailyEntry.codechefCount, dailyEntry.codechefTarget,
                    if (dailyEntry.codechefTarget > 0) (dailyEntry.codechefCount.toFloat() / dailyEntry.codechefTarget * 100) else 0f),
                PlatformStats("GeeksforGeeks", dailyEntry.geeksforgeeksCount, dailyEntry.geeksforgeeksTarget,
                    if (dailyEntry.geeksforgeeksTarget > 0) (dailyEntry.geeksforgeeksCount.toFloat() / dailyEntry.geeksforgeeksTarget * 100) else 0f)
            )
        } else {
            listOf(
                PlatformStats("LeetCode", 0, 0, 0f),
                PlatformStats("Codeforces", 0, 0, 0f),
                PlatformStats("CodeChef", 0, 0, 0f),
                PlatformStats("GeeksforGeeks", 0, 0, 0f)
            )
        }

        val totalProblems = platformStats.sumOf { it.count }
        val totalTargets = platformStats.sumOf { it.target }
        val overallCompletionPercentage = if (totalTargets > 0) (totalProblems.toFloat() / totalTargets * 100) else 0f

        val todosCompleted = todos.count { it.isCompleted }
        val totalTodos = todos.size
        val todoCompletionPercentage = if (totalTodos > 0) (todosCompleted.toFloat() / totalTodos * 100) else 0f

        return DailyAnalysis(
            totalProblems = totalProblems,
            totalTargets = totalTargets,
            overallCompletionPercentage = overallCompletionPercentage,
            platformStats = platformStats,
            todosCompleted = todosCompleted,
            totalTodos = totalTodos,
            todoCompletionPercentage = todoCompletionPercentage
        )
    }

    private fun calculatePlatformAnalytics(
        entries: List<com.example.grindlog.data.local.entity.DailyEntry>,
        dateRange: DateRange,
        selectedPlatform: String
    ): List<PlatformAnalytics> {
        val filteredEntries = filterEntriesByDateRange(entries, dateRange)

        val platforms = listOf("LeetCode", "Codeforces", "CodeChef", "GeeksforGeeks")

        return platforms.mapNotNull { platform ->
            if (selectedPlatform != "All" && selectedPlatform != platform) return@mapNotNull null

            val totalSolved = filteredEntries.sumOf { entry ->
                when (platform) {
                    "LeetCode" -> entry.leetcodeCount
                    "Codeforces" -> entry.codeforcesCount
                    "CodeChef" -> entry.codechefCount
                    "GeeksforGeeks" -> entry.geeksforgeeksCount
                    else -> 0
                }
            }

            val totalTargets = filteredEntries.sumOf { entry ->
                when (platform) {
                    "LeetCode" -> entry.leetcodeTarget
                    "Codeforces" -> entry.codeforcesTarget
                    "CodeChef" -> entry.codechefTarget
                    "GeeksforGeeks" -> entry.geeksforgeeksTarget
                    else -> 0
                }
            }

            val averageDaily = if (filteredEntries.isNotEmpty()) totalSolved.toFloat() / filteredEntries.size else 0f
            val targetAchievementRate = if (totalTargets > 0) (totalSolved.toFloat() / totalTargets * 100) else 0f

            PlatformAnalytics(
                platform = platform,
                totalSolved = totalSolved,
                totalTargets = totalTargets,
                averageDaily = averageDaily,
                targetAchievementRate = targetAchievementRate,
                activeDays = filteredEntries.count { entry ->
                    when (platform) {
                        "LeetCode" -> entry.leetcodeCount > 0
                        "Codeforces" -> entry.codeforcesCount > 0
                        "CodeChef" -> entry.codechefCount > 0
                        "GeeksforGeeks" -> entry.geeksforgeeksCount > 0
                        else -> false
                    }
                }
            )
        }
    }

    private fun filterEntriesByDateRange(
        entries: List<com.example.grindlog.data.local.entity.DailyEntry>,
        dateRange: DateRange
    ): List<com.example.grindlog.data.local.entity.DailyEntry> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        when (dateRange) {
            DateRange.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            DateRange.MONTH -> calendar.add(Calendar.MONTH, -1)
            DateRange.YEAR -> calendar.add(Calendar.YEAR, -1)
        }

        val startDate = calendar.time
        return entries.filter { it.date >= startDate && it.date <= endDate }
    }
}

data class AnalysisUiState(
    val viewMode: AnalysisViewMode = AnalysisViewMode.DATE_RANGE,
    val showDatePicker: Boolean = false
)

enum class AnalysisViewMode(val displayName: String) {
    DATE_RANGE("Date Range"),
    SPECIFIC_DATE("Specific Date")
}

enum class DateRange(val displayName: String) {
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}
