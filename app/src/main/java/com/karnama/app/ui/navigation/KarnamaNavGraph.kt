package com.karnama.app.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.karnama.app.KarnamaApplication
import com.karnama.app.ui.actions.ActionsViewModel
import com.karnama.app.ui.calendar.CalendarScreen
import com.karnama.app.ui.calendar.CalendarViewModel
import com.karnama.app.ui.home.HomeScreen
import com.karnama.app.ui.home.HomeViewModel
import com.karnama.app.ui.more.AboutScreen
import com.karnama.app.ui.more.BackupScreen
import com.karnama.app.ui.more.DoneTasksScreen
import com.karnama.app.ui.more.DoneTasksViewModel
import com.karnama.app.ui.more.MoreScreen
import com.karnama.app.ui.quicktexts.QuickTextViewModel
import com.karnama.app.ui.quicktexts.QuickTextsScreen
import com.karnama.app.ui.settings.SettingsScreen
import com.karnama.app.ui.settings.SettingsViewModel

@Composable
fun KarnamaNavGraph() {
    val navController = rememberNavController()
    val container = (LocalContext.current.applicationContext as KarnamaApplication).container

    val bottomBar: @Composable () -> Unit = {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination
        NavigationBar {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(stringResource(item.labelRes)) }
                )
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Tasks.route) {
        composable(Screen.Tasks.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = GenericViewModelFactory {
                    HomeViewModel(container.taskRepository, container.quickTextRepository)
                }
            )
            HomeScreen(
                viewModel = homeViewModel,
                actionsViewModelFactory = { taskId ->
                    ActionsViewModel(taskId, container.taskRepository)
                },
                bottomBar = bottomBar
            )
        }

        composable(Screen.Calendar.route) {
            val calendarViewModel: CalendarViewModel = viewModel(
                factory = GenericViewModelFactory { CalendarViewModel(container.taskRepository) }
            )
            CalendarScreen(viewModel = calendarViewModel, bottomBar = bottomBar)
        }

        composable(Screen.More.route) {
            MoreScreen(
                onQuickTexts = { navController.navigate(Screen.QuickTexts.route) },
                onDoneTasks = { navController.navigate(Screen.DoneTasks.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onBackup = { navController.navigate("backup") },
                onAbout = { navController.navigate(Screen.About.route) },
                bottomBar = bottomBar
            )
        }

        composable(Screen.QuickTexts.route) {
            val vm: QuickTextViewModel = viewModel(
                factory = GenericViewModelFactory { QuickTextViewModel(container.quickTextRepository) }
            )
            QuickTextsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(Screen.DoneTasks.route) {
            val vm: DoneTasksViewModel = viewModel(
                factory = GenericViewModelFactory { DoneTasksViewModel(container.taskRepository) }
            )
            DoneTasksScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = viewModel(
                factory = GenericViewModelFactory { SettingsViewModel(container.settingsRepository) }
            )
            SettingsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable("backup") {
            BackupScreen(backupRepository = container.backupRepository, onBack = { navController.popBackStack() })
        }

        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
