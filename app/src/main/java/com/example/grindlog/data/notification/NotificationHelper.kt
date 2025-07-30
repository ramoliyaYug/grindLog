package com.example.grindlog.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.grindlog.MainActivity
import com.example.grindlog.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val CONTEST_CHANNEL_ID = "contest_channel"
        const val DAILY_SUMMARY_CHANNEL_ID = "daily_summary_channel"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for custom reminders"
                enableVibration(true)
                enableLights(true)
            }

            val contestChannel = NotificationChannel(
                CONTEST_CHANNEL_ID,
                "Contest Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming contests"
                enableVibration(true)
                enableLights(true)
            }

            val dailySummaryChannel = NotificationChannel(
                DAILY_SUMMARY_CHANNEL_ID,
                "Daily Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily progress summary notifications"
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(contestChannel)
            notificationManager.createNotificationChannel(dailySummaryChannel)
        }
    }

    fun showNotification(
        id: Int,
        title: String,
        content: String,
        isContest: Boolean = false,
        isDailySummary: Boolean = false
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val channelId = when {
                isDailySummary -> DAILY_SUMMARY_CHANNEL_ID
                isContest -> CONTEST_CHANNEL_ID
                else -> REMINDER_CHANNEL_ID
            }

            val icon = R.drawable.ic_launcher_foreground

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(
                    if (isDailySummary) NotificationCompat.PRIORITY_DEFAULT
                    else NotificationCompat.PRIORITY_HIGH
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()

            notificationManager.notify(id, notification)

            println("✅ Notification sent successfully - ID: $id, Title: $title")

        } catch (e: Exception) {
            println("❌ Failed to send notification: ${e.message}")
            throw e
        }
    }

    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    fun getNotificationChannelStatus(): Map<String, Boolean> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mapOf(
                "Reminders" to (notificationManager.getNotificationChannel(REMINDER_CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE),
                "Contests" to (notificationManager.getNotificationChannel(CONTEST_CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE),
                "Daily Summary" to (notificationManager.getNotificationChannel(DAILY_SUMMARY_CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE)
            )
        } else {
            mapOf("All Notifications" to areNotificationsEnabled())
        }
    }
}
