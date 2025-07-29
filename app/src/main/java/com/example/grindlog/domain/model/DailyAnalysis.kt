package com.example.grindlog.domain.model

data class DailyAnalysis(
    val totalProblems: Int,
    val totalTargets: Int,
    val overallCompletionPercentage: Float,
    val platformStats: List<PlatformStats>,
    val todosCompleted: Int,
    val totalTodos: Int,
    val todoCompletionPercentage: Float
)
