package com.example.grindlog.domain.notification

import com.example.grindlog.data.local.entity.Reminder

interface NotificationScheduler {
    suspend fun scheduleReminder(reminder: Reminder)
    suspend fun scheduleReminderWithDelay(reminder: Reminder, minutesBefore: Int)
    suspend fun cancelReminder(reminderId: Long)
    suspend fun scheduleContestReminder(reminder: Reminder)
}
