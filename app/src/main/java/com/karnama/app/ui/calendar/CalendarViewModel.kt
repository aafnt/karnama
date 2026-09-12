package com.karnama.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class CalendarUiState(
    val selectedDate: PersianDate = PersianDate.today(),
    val tasks: List<Task> = emptyList()
)

class CalendarViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val selectedDate = MutableStateFlow(PersianDate.today())

    private val tasksFlow = selectedDate.flatMapLatest { date ->
        taskRepository.observeTasksForDate(date.toGregorian().toEpochDay())
    }

    val uiState: StateFlow<CalendarUiState> = combine(selectedDate, tasksFlow) { date, tasks ->
        CalendarUiState(selectedDate = date, tasks = tasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun selectDate(date: PersianDate) {
        selectedDate.value = date
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            taskRepository.setCompleted(task.id, !task.completed)
        }
    }
}
