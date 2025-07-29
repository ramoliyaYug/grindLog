package com.example.grindlog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dateTime: Date,
    val isContestReminder: Boolean = false,
    val platform: String? = null,
    val isActive: Boolean = true,
    val createdAt: Date = Date()
)
