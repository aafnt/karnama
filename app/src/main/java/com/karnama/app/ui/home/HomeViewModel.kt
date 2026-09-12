package com.karnama.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karnama.app.data.repository.QuickText
import com.karnama.app.data.repository.QuickTextRepository
import com.karnama.app.data.repository.Task
import com.karnama.app.data.repository.TaskRepository
import com.karnama.app.util.PersianDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DayTab { YESTERDAY, TODAY, TOMORROW }

data class HomeUiState(
    val selectedDay: PersianDate = PersianDate.today(),
    val dayTab: DayTab = DayTab.TODAY,
    val tasks: List<Task> = emptyList(),
    val quickAddText: String = "",
    val suggestions: List<QuickText> = emptyList(),
    val selectionMode: Boolean = false,
    val selectedTaskIds: Set<Long> = emptySet(),
    val showMoveToDateSheet: Boolean = false,
    val actionsForTaskId: Long? = null
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val quickTextRepository: QuickTextRepository
) : ViewModel() {

    private val today = PersianDate.today()

    private val selectedDay = MutableStateFlow(today)
    private val dayTab = MutableStateFlow(DayTab.TODAY)
    private val quickAddText = MutableStateFlow("")
    private val selectionMode = MutableStateFlow(false)
    private val selectedTaskIds = MutableStateFlow<Set<Long>>(emptySet())
    private val showMoveToDateSheet = MutableStateFlow(false)
    private val actionsForTaskId = MutableStateFlow<Long?>(null)

    private val allQuickTexts = quickTextRepository.observeAll()

    private val tasksFlow = selectedDay.flatMapLatest { day ->
        taskRepository.observeTasksForDate(day.toGregorian().toEpochDay())
    }

    private val suggestionsFlow = combine(quickAddText, allQuickTexts) { query, all ->
        quickTextRepository.search(all, query)
    }

    private data class SelectionState(
        val selectionMode: Boolean,
        val selectedTaskIds: Set<Long>,
        val showMoveToDateSheet: Boolean,
        val actionsForTaskId: Long?
    )

    private val selectionState = combine(
        selectionMode, selectedTaskIds, showMoveToDateSheet, actionsForTaskId
    ) { mode, ids, showSheet, actionsId ->
        SelectionState(mode, ids, showSheet, actionsId)
    }

    private data class DayAndInputState(
        val selectedDay: PersianDate,
        val dayTab: DayTab,
        val quickAddText: String,
        val suggestions: List<QuickText>
    )

    private val dayAndInputState = combine(
        selectedDay, dayTab, quickAddText, suggestionsFlow
    ) { day, tab, text, suggestions ->
        DayAndInputState(day, tab, text, suggestions)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        dayAndInputState, tasksFlow, selectionState
    ) { dayInput, tasks, selection ->
        HomeUiState(
            selectedDay = dayInput.selectedDay,
            dayTab = dayInput.dayTab,
            tasks = tasks,
            quickAddText = dayInput.quickAddText,
            suggestions = dayInput.suggestions,
            selectionMode = selection.selectionMode,
            selectedTaskIds = selection.selectedTaskIds,
            showMoveToDateSheet = selection.showMoveToDateSheet,
            actionsForTaskId = selection.actionsForTaskId
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun selectDayTab(tab: DayTab) {
        dayTab.value = tab
        selectedDay.value = when (tab) {
            DayTab.YESTERDAY -> today.minusDays(1)
            DayTab.TODAY -> today
            DayTab.TOMORROW -> today.plusDays(1)
        }
        exitSelectionMode()
    }

    fun onQuickAddTextChange(text: String) {
        quickAddText.value = text
    }

    fun applySuggestion(quickText: QuickText) {
        quickAddText.value = quickText.text
    }

    fun submitQuickAdd() {
        val text = quickAddText.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            taskRepository.addTask(text, selectedDay.value.toGregorian().toEpochDay())
            quickAddText.value = ""
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            taskRepository.setCompleted(task.id, !task.completed)
        }
    }

    fun onTaskLongPress(taskId: Long) {
        selectionMode.value = true
        selectedTaskIds.value = setOf(taskId)
    }

    fun toggleTaskSelection(taskId: Long) {
        val current = selectedTaskIds.value
        val updated = if (current.contains(taskId)) current - taskId else current + taskId
        selectedTaskIds.value = updated
        if (updated.isEmpty()) selectionMode.value = false
    }

    fun exitSelectionMode() {
        selectionMode.value = false
        selectedTaskIds.value = emptySet()
    }

    fun moveSelectedToTomorrow() {
        val ids = selectedTaskIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            val target = selectedDay.value.plusDays(1)
            taskRepository.moveTasks(ids, target.toGregorian().toEpochDay())
            exitSelectionMode()
        }
    }

    fun openMoveToDateSheet() {
        showMoveToDateSheet.value = true
    }

    fun dismissMoveToDateSheet() {
        showMoveToDateSheet.value = false
    }

    fun moveSelectedToDate(date: PersianDate) {
        val ids = selectedTaskIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            taskRepository.moveTasks(ids, date.toGregorian().toEpochDay())
            showMoveToDateSheet.value = false
            exitSelectionMode()
        }
    }

    fun deleteSelected() {
        val ids = selectedTaskIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            taskRepository.deleteTasks(ids)
            exitSelectionMode()
        }
    }

    fun openActions(taskId: Long) {
        actionsForTaskId.value = taskId
    }

    fun closeActions() {
        actionsForTaskId.value = null
    }
}
