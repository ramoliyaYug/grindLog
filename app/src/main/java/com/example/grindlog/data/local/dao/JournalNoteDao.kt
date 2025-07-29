package com.example.grindlog.data.local.dao

import androidx.room.*
import com.example.grindlog.data.local.entity.JournalNote
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface JournalNoteDao {
    @Query("SELECT * FROM journal_notes ORDER BY date DESC, createdAt DESC")
    fun getAllJournalNotes(): Flow<List<JournalNote>>

    @Query("SELECT * FROM journal_notes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getJournalNotesBetweenDates(startDate: Date, endDate: Date): Flow<List<JournalNote>>

    @Query("SELECT * FROM journal_notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchJournalNotes(query: String): Flow<List<JournalNote>>

    @Query("SELECT * FROM journal_notes WHERE id = :id")
    suspend fun getJournalNoteById(id: Long): JournalNote?

    @Insert
    suspend fun insertJournalNote(journalNote: JournalNote): Long

    @Update
    suspend fun updateJournalNote(journalNote: JournalNote)

    @Delete
    suspend fun deleteJournalNote(journalNote: JournalNote)

    @Query("DELETE FROM journal_notes")
    suspend fun deleteAllJournalNotes()
}
