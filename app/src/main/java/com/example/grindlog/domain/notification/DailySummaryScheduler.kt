package com.example.grindlog.domain.notification

import com.example.grindlog.domain.model.DailyAnalysis

interface DailySummaryScheduler {
    suspend fun scheduleDailySummary()
    suspend fun cancelDailySummary()
    suspend fun showDailySummaryNotification(analysis: DailyAnalysis)
}
