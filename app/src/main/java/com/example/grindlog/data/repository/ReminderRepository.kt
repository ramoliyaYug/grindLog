package com.example.grindlog.data.repository

import com.example.grindlog.data.local.dao.ReminderDao
import com.example.grindlog.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getActiveReminders(): Flow<List<Reminder>> = reminderDao.getActiveReminders()

    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()

    fun getUpcomingReminders(currentTime: Date): Flow<List<Reminder>> =
        reminderDao.getUpcomingReminders(currentTime)

    suspend fun getReminderById(id: Long): Reminder? = reminderDao.getReminderById(id)

    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder)

    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)

    suspend fun deleteAllReminders() = reminderDao.deleteAllReminders()
}
