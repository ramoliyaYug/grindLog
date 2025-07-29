package com.example.grindlog.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.grindlog.domain.model.DailyAnalysis
import com.example.grindlog.domain.notification.DailySummaryScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailySummarySchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationHelper: NotificationHelper
) : DailySummaryScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun scheduleDailySummary() {
        val intent = Intent(context, DailySummaryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_SUMMARY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 55)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override suspend fun cancelDailySummary() {
        val intent = Intent(context, DailySummaryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_SUMMARY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override suspend fun showDailySummaryNotification(analysis: DailyAnalysis) {
        val title = "📊 Daily Summary"
        val content = buildSummaryContent(analysis)

        notificationHelper.showNotification(
            id = DAILY_SUMMARY_NOTIFICATION_ID,
            title = title,
            content = content,
            isContest = false
        )
    }

    private fun buildSummaryContent(analysis: DailyAnalysis): String {
        val totalProblems = analysis.totalProblems
        val completionRate = analysis.overallCompletionPercentage.toInt()
        val todosCompleted = analysis.todosCompleted
        val totalTodos = analysis.totalTodos

        return buildString {
            append("🎯 Problems solved: $totalProblems")
            if (analysis.totalTargets > 0) {
                append(" (${completionRate}% of target)")
            }
            append("\n✅ Todos: $todosCompleted/$totalTodos completed")

            if (completionRate >= 100) {
                append("\n🏆 Amazing! You hit all your targets!")
            } else if (completionRate >= 75) {
                append("\n🔥 Great progress today!")
            } else if (completionRate >= 50) {
                append("\n💪 Good effort! Keep it up!")
            } else if (totalProblems > 0) {
                append("\n🌱 Every step counts!")
            } else {
                append("\n📚 Tomorrow is a new opportunity!")
            }
        }
    }

    companion object {
        private const val DAILY_SUMMARY_REQUEST_CODE = 1001
        private const val DAILY_SUMMARY_NOTIFICATION_ID = 1001
    }
}
