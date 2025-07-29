package com.example.grindlog.domain.usecase

import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.TodoRepository
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.model.PlatformStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date
import javax.inject.Inject

class GetDailyAnalysisUseCase @Inject constructor(
    private val dailyEntryRepository: DailyEntryRepository,
    private val todoRepository: TodoRepository
) {
    operator fun invoke(date: Date): Flow<DailyAnalysis> {
        return combine(
            dailyEntryRepository.getDailyEntryFlow(date),
            todoRepository.getTodosByDate(date)
        ) { dailyEntry, todos ->
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

            DailyAnalysis(
                totalProblems = totalProblems,
                totalTargets = totalTargets,
                overallCompletionPercentage = overallCompletionPercentage,
                platformStats = platformStats,
                todosCompleted = todosCompleted,
                totalTodos = totalTodos,
                todoCompletionPercentage = todoCompletionPercentage
            )
        }
    }
}
