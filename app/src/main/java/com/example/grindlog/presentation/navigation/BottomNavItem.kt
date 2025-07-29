package com.example.grindlog.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Today : BottomNavItem("today", "Today", Icons.Default.Today)
    object Reminder : BottomNavItem("reminder", "Reminder", Icons.Default.Notifications)
    object Analysis : BottomNavItem("analysis", "Analysis", Icons.Default.Analytics)
    object Todo : BottomNavItem("todo", "Todo", Icons.Default.CheckCircle)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}
