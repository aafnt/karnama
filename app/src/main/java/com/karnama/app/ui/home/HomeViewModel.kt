package com.karnama.app.ui.home

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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

data class EditingTaskState(val taskId: Long, val text: TextFieldValue)

data class HomeUiState(
    val selectedDay: PersianDate = PersianDate.today(),
    val dayTab: DayTab = DayTab.TODAY,
    val tasks: List<Task> = emptyList(),
    val quickAddText: TextFieldValue = TextFieldValue(""),
    val suggestions: List<QuickText> = emptyList(),
    val selectionMode: Boolean = false,
    val selectedTaskIds: Set<Long> = emptySet(),
    val showMoveToDateSheet: Boolean = false,
    val actionsForTaskId: Long? = null,
    val editingTask: EditingTaskState? = null
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val quickTextRepository: QuickTextRepository
) : ViewModel() {

    private val today = PersianDate.today()

    private val selectedDay = MutableStateFlow(today)
    private val dayTab = MutableStateFlow(DayTab.TODAY)
    private val quickAddText = MutableStateFlow(TextFieldValue(""))
    // بعد از انتخاب یک پیشنهاد، تا وقتی کاربر دوباره چیزی تایپ نکرده
    // کادر پیشنهادها باید بسته بماند (وگرنه چون متن انتخاب‌شده خودش با
    // جستجو مطابقت دارد، پیشنهاد بلافاصله دوباره ظاهر می‌شود و اصلاً
    // بسته نمی‌شود).
    private val suppressSuggestions = MutableStateFlow(false)
    private val selectionMode = MutableStateFlow(false)
    private val selectedTaskIds = MutableStateFlow<Set<Long>>(emptySet())
    private val showMoveToDateSheet = MutableStateFlow(false)
    private val actionsForTaskId = MutableStateFlow<Long?>(null)
    private val editingTask = MutableStateFlow<EditingTaskState?>(null)

    private val allQuickTexts = quickTextRepository.observeAll()

    private val tasksFlow = selectedDay.flatMapLatest { day ->
        taskRepository.observeTasksForDate(day.toGregorian().toEpochDay())
    }

    private val suggestionsFlow = combine(
        quickAddText, allQuickTexts, suppressSuggestions
    ) { fieldValue, all, suppressed ->
        if (suppressed) emptyList() else quickTextRepository.search(all, fieldValue.text)
    }

    private data class SelectionState(
        val selectionMode: Boolean,
        val selectedTaskIds: Set<Long>,
        val showMoveToDateSheet: Boolean,
        val actionsForTaskId: Long?,
        val editingTask: EditingTaskState?
    )

    private val selectionState = combine(
        selectionMode, selectedTaskIds, showMoveToDateSheet, actionsForTaskId, editingTask
    ) { mode, ids, showSheet, actionsId, editing ->
        SelectionState(mode, ids, showSheet, actionsId, editing)
    }

    private data class DayAndInputState(
        val selectedDay: PersianDate,
        val dayTab: DayTab,
        val quickAddText: TextFieldValue,
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
            actionsForTaskId = selection.actionsForTaskId,
            editingTask = selection.editingTask
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

    fun onQuickAddTextChange(value: TextFieldValue) {
        quickAddText.value = value
        // کاربر دارد دوباره تایپ می‌کند، پس اگر پیشنهادها بسته شده بودند
        // (به‌خاطر انتخاب قبلی) باید دوباره فعال شوند.
        suppressSuggestions.value = false
    }

    fun applySuggestion(quickText: QuickText) {
        // نشانگر را دقیقاً انتهای متنِ جایگزین‌شده قرار می‌دهیم تا کاربر
        // بلافاصله بتواند بدون جابه‌جا کردن نشانگر به تایپ ادامه دهد.
        quickAddText.value = TextFieldValue(
            text = quickText.text,
            selection = TextRange(quickText.text.length)
        )
        suppressSuggestions.value = true
    }

    fun submitQuickAdd() {
        val text = quickAddText.value.text.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            taskRepository.addTask(text, selectedDay.value.toGregorian().toEpochDay())
            quickAddText.value = TextFieldValue("")
            suppressSuggestions.value = false
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

    /** فقط زمانی معنا دارد که دقیقاً یک کار انتخاب شده باشد */
    fun startEditingSelected() {
        val id = selectedTaskIds.value.singleOrNull() ?: return
        val currentTitle = uiState.value.tasks.find { it.id == id }?.title ?: return
        editingTask.value = EditingTaskState(
            taskId = id,
            text = TextFieldValue(currentTitle, selection = TextRange(currentTitle.length))
        )
    }

    fun onEditTextChange(value: TextFieldValue) {
        editingTask.value = editingTask.value?.copy(text = value)
    }

    fun cancelEdit() {
        editingTask.value = null
    }

    fun saveEdit() {
        val state = editingTask.value ?: return
        val newTitle = state.text.text.trim()
        if (newTitle.isEmpty()) return
        viewModelScope.launch {
            taskRepository.updateTitle(state.taskId, newTitle)
            editingTask.value = null
            exitSelectionMode()
        }
    }
}
