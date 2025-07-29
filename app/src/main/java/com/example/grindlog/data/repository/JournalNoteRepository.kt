package com.example.grindlog.data.repository

import com.example.grindlog.data.local.dao.JournalNoteDao
import com.example.grindlog.data.local.entity.JournalNote
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalNoteRepository @Inject constructor(
    private val journalNoteDao: JournalNoteDao
) {
    fun getAllJournalNotes(): Flow<List<JournalNote>> = journalNoteDao.getAllJournalNotes()

    fun getJournalNotesBetweenDates(startDate: Date, endDate: Date): Flow<List<JournalNote>> =
        journalNoteDao.getJournalNotesBetweenDates(startDate, endDate)

    fun searchJournalNotes(query: String): Flow<List<JournalNote>> =
        journalNoteDao.searchJournalNotes(query)

    suspend fun getJournalNoteById(id: Long): JournalNote? = journalNoteDao.getJournalNoteById(id)

    suspend fun insertJournalNote(journalNote: JournalNote): Long =
        journalNoteDao.insertJournalNote(journalNote)

    suspend fun updateJournalNote(journalNote: JournalNote) = journalNoteDao.updateJournalNote(journalNote)

    suspend fun deleteJournalNote(journalNote: JournalNote) = journalNoteDao.deleteJournalNote(journalNote)

    suspend fun deleteAllJournalNotes() = journalNoteDao.deleteAllJournalNotes()
}
