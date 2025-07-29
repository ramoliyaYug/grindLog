package com.example.grindlog.domain.model

data class PlatformAnalytics(
    val platform: String,
    val totalSolved: Int,
    val totalTargets: Int,
    val averageDaily: Float,
    val targetAchievementRate: Float,
    val activeDays: Int
)
