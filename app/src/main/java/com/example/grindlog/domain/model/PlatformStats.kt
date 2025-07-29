package com.example.grindlog.domain.model

data class PlatformStats(
    val platform: String,
    val count: Int,
    val target: Int,
    val completionPercentage: Float
) {
    val isTargetMet: Boolean get() = count >= target
}
