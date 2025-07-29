package com.example.grindlog.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminder_id", 0)
        val title = intent.getStringExtra("reminder_title") ?: "Reminder"
        val description = intent.getStringExtra("reminder_description") ?: ""
        val isContest = intent.getBooleanExtra("is_contest", false)
        val platform = intent.getStringExtra("platform")
        val minutesBefore = intent.getIntExtra("minutes_before", 60)

        val notificationTitle = if (isContest) {
            "Contest Starting Soon! 🏆"
        } else {
            title
        }

        val notificationContent = if (isContest) {
            "$platform contest starts in ${formatTime(minutesBefore)}. Get ready!"
        } else {
            description.ifBlank { "Reminder: $title" }
        }

        notificationHelper.showNotification(
            id = reminderId.toInt(),
            title = notificationTitle,
            content = notificationContent,
            isContest = isContest
        )
    }

    private fun formatTime(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes minutes"
            minutes == 60 -> "1 hour"
            minutes < 1440 -> "${minutes / 60} hours"
            else -> "${minutes / 1440} days"
        }
    }
}
