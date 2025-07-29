package com.example.grindlog.data.local.dao

import androidx.room.*
import com.example.grindlog.data.local.entity.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailyEntryDao {
    @Query("SELECT * FROM daily_entries WHERE date = :date")
    suspend fun getDailyEntry(date: Date): DailyEntry?

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    fun getDailyEntryFlow(date: Date): Flow<DailyEntry?>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun getAllDailyEntries(): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDailyEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<DailyEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyEntry(dailyEntry: DailyEntry)

    @Update
    suspend fun updateDailyEntry(dailyEntry: DailyEntry)

    @Delete
    suspend fun deleteDailyEntry(dailyEntry: DailyEntry)

    @Query("DELETE FROM daily_entries")
    suspend fun deleteAllDailyEntries()

    // Debug query to check data
    @Query("SELECT * FROM daily_entries WHERE leetcodeCount > 0 OR codeforcesCount > 0 OR codechefCount > 0 OR geeksforgeeksCount > 0")
    suspend fun getEntriesWithActivity(): List<DailyEntry>
}
