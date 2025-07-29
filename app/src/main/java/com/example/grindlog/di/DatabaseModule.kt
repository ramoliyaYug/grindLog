package com.example.grindlog.di

import android.content.Context
import androidx.room.Room
import com.example.grindlog.data.local.database.GrindLogDatabase
import com.example.grindlog.data.local.dao.DailyEntryDao
import com.example.grindlog.data.local.dao.JournalNoteDao
import com.example.grindlog.data.local.dao.ReminderDao
import com.example.grindlog.data.local.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGrindLogDatabase(@ApplicationContext context: Context): GrindLogDatabase {
        return Room.databaseBuilder(
            context,
            GrindLogDatabase::class.java,
            "grindlog_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDailyEntryDao(database: GrindLogDatabase): DailyEntryDao = database.dailyEntryDao()

    @Provides
    fun provideJournalNoteDao(database: GrindLogDatabase): JournalNoteDao = database.journalNoteDao()

    @Provides
    fun provideReminderDao(database: GrindLogDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideTodoDao(database: GrindLogDatabase): TodoDao = database.todoDao()
}
