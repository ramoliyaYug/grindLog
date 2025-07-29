package com.example.grindlog.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.grindlog.data.repository.DailyEntryRepository
import com.example.grindlog.data.repository.TodoRepository
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.model.PlatformStats
import com.example.grindlog.domain.notification.DailySummaryScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DailySummaryReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dailyEntryRepository: DailyEntryRepository

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var dailySummaryScheduler: DailySummaryScheduler

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            try {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val dailyEntry = dailyEntryRepository.getDailyEntry(today)
                val todos = todoRepository.getTodosByDate(today).first()

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

                val analysis = DailyAnalysis(
                    totalProblems = totalProblems,
                    totalTargets = totalTargets,
                    overallCompletionPercentage = overallCompletionPercentage,
                    platformStats = platformStats,
                    todosCompleted = todosCompleted,
                    totalTodos = totalTodos,
                    todoCompletionPercentage = todoCompletionPercentage
                )

                dailySummaryScheduler.showDailySummaryNotification(analysis)

            } catch (e: Exception) {
            }
        }
    }
}
