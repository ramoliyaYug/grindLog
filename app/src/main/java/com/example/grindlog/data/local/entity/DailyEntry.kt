package com.example.grindlog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_entries")
data class DailyEntry(
    @PrimaryKey
    val date: Date,
    val leetcodeCount: Int = 0,
    val codeforcesCount: Int = 0,
    val codechefCount: Int = 0,
    val geeksforgeeksCount: Int = 0,
    val leetcodeTarget: Int = 0,
    val codeforcesTarget: Int = 0,
    val codechefTarget: Int = 0,
    val geeksforgeeksTarget: Int = 0
) {
    override fun toString(): String {
        return "DailyEntry(date=$date, leetcode=$leetcodeCount, codeforces=$codeforcesCount, codechef=$codechefCount, gfg=$geeksforgeeksCount)"
    }
}
