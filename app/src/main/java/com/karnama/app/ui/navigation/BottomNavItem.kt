package com.karnama.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.karnama.app.R

sealed class Screen(val route: String) {
    data object Tasks : Screen("tasks")
    data object Calendar : Screen("calendar")
    data object More : Screen("more")
    data object QuickTexts : Screen("quick_texts")
    data object DoneTasks : Screen("done_tasks")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}

data class BottomNavItem(
    val screen: Screen,
    val labelRes: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Tasks, R.string.nav_tasks, Icons.Outlined.CheckCircle),
    BottomNavItem(Screen.Calendar, R.string.nav_calendar, Icons.Outlined.CalendarMonth),
    BottomNavItem(Screen.More, R.string.nav_more, Icons.Outlined.MoreHoriz)
)
