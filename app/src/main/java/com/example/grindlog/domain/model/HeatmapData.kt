package com.example.grindlog.domain.model

import java.util.Date

data class HeatmapData(
    val date: Date,
    val count: Int // Changed from intensity: Float to count: Int
)
