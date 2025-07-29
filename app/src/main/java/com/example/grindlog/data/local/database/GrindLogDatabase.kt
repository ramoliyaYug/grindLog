package com.example.grindlog.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.grindlog.data.local.dao.DailyEntryDao
import com.example.grindlog.data.local.dao.JournalNoteDao
import com.example.grindlog.data.local.dao.ReminderDao
import com.example.grindlog.data.local.dao.TodoDao
import com.example.grindlog.data.local.entity.DailyEntry
import com.example.grindlog.data.local.entity.JournalNote
import com.example.grindlog.data.local.entity.Reminder
import com.example.grindlog.data.local.entity.Todo
import com.example.grindlog.data.local.converter.DateConverter

@Database(
    entities = [DailyEntry::class, JournalNote::class, Reminder::class, Todo::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class GrindLogDatabase : RoomDatabase() {
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun journalNoteDao(): JournalNoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun todoDao(): TodoDao
}
