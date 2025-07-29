package com.example.grindlog.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.TodoRepository
import com.example.grindlog.domain.model.PlatformAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _selectedDateRange = MutableStateFlow(DateRange.WEEK)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("All")
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    val platformAnalytics: StateFlow<List<PlatformAnalytics>> = combine(
        dailyEntryRepository.getAllDailyEntries(),
        selectedDateRange,
        selectedPlatform
    ) { entries, dateRange, platform ->
        calculatePlatformAnalytics(entries, dateRange, platform)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun updateDateRange(dateRange: DateRange) {
        _selectedDateRange.value = dateRange
    }

    fun updateSelectedPlatform(platform: String) {
        _selectedPlatform.value = platform
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

enum class DateRange(val displayName: String) {
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}
