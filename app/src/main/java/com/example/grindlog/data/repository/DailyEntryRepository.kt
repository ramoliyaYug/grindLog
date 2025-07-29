package com.example.grindlog.data.repository

import com.example.grindlog.data.local.dao.DailyEntryDao
import com.example.grindlog.data.local.entity.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyEntryRepository @Inject constructor(
    private val dailyEntryDao: DailyEntryDao
) {
    suspend fun getDailyEntry(date: Date): DailyEntry? = dailyEntryDao.getDailyEntry(date)

    fun getDailyEntryFlow(date: Date): Flow<DailyEntry?> = dailyEntryDao.getDailyEntryFlow(date)

    fun getAllDailyEntries(): Flow<List<DailyEntry>> = dailyEntryDao.getAllDailyEntries()

    fun getDailyEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<DailyEntry>> =
        dailyEntryDao.getDailyEntriesBetweenDates(startDate, endDate)

    suspend fun insertOrUpdateDailyEntry(dailyEntry: DailyEntry) {
        dailyEntryDao.insertDailyEntry(dailyEntry)
    }

    suspend fun deleteDailyEntry(dailyEntry: DailyEntry) = dailyEntryDao.deleteDailyEntry(dailyEntry)

    suspend fun deleteAllDailyEntries() = dailyEntryDao.deleteAllDailyEntries()
}
