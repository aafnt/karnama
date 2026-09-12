package com.karnama.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnama.app.ui.actions.ActionsBottomSheet
import com.karnama.app.ui.actions.ActionsViewModel
import com.karnama.app.util.toFaDigits

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    actionsViewModelFactory: (Long) -> ActionsViewModel,
    bottomBar: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
        ) {
            if (uiState.selectionMode) {
                SelectionToolbar(
                    selectedCount = uiState.selectedTaskIds.size,
                    onClose = viewModel::exitSelectionMode,
                    onMoveToTomorrow = viewModel::moveSelectedToTomorrow,
                    onMoveToDate = viewModel::openMoveToDateSheet,
                    onDelete = viewModel::deleteSelected
                )
            } else {
                Text(
                    text = dayTabLabel(uiState.dayTab),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = uiState.selectedDay.formatFull(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )
                DaySelector(
                    selected = uiState.dayTab,
                    onSelect = viewModel::selectDayTab,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.tasks.isEmpty()) {
                    EmptyTasksState()
                } else {
                    LazyColumn {
                        items(uiState.tasks, key = { it.id }) { task ->
                            TaskRow(
                                task = task,
                                isSelected = uiState.selectedTaskIds.contains(task.id),
                                selectionMode = uiState.selectionMode,
                                onToggleCompleted = { viewModel.toggleCompleted(task) },
                                onOpenActions = { viewModel.openActions(task.id) },
                                onLongPress = { viewModel.onTaskLongPress(task.id) },
                                onTap = { viewModel.toggleTaskSelection(task.id) }
                            )
                        }
                    }
                }
            }

            QuickAddInput(
                text = uiState.quickAddText,
                onTextChange = viewModel::onQuickAddTextChange,
                onSubmit = viewModel::submitQuickAdd,
                suggestions = uiState.suggestions,
                onSuggestionPicked = viewModel::applySuggestion,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }

    if (uiState.showMoveToDateSheet) {
        MoveToDateSheet(
            onDismiss = viewModel::dismissMoveToDateSheet,
            onDateSelected = viewModel::moveSelectedToDate
        )
    }

    uiState.actionsForTaskId?.let { taskId ->
        ActionsBottomSheet(
            viewModel = actionsViewModelFactory(taskId),
            onDismiss = viewModel::closeActions
        )
    }
}

@Composable
private fun dayTabLabel(tab: DayTab): String = androidx.compose.ui.res.stringResource(
    when (tab) {
        DayTab.YESTERDAY -> com.karnama.app.R.string.yesterday
        DayTab.TODAY -> com.karnama.app.R.string.today
        DayTab.TOMORROW -> com.karnama.app.R.string.tomorrow
    }
)
