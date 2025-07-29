package com.example.grindlog.data.local.dao

import androidx.room.*
import com.example.grindlog.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isActive = 1 ORDER BY dateTime ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY dateTime DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE dateTime >= :currentTime AND isActive = 1 ORDER BY dateTime ASC")
    fun getUpcomingReminders(currentTime: Date): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Insert
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}
