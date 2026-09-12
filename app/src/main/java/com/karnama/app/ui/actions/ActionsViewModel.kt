package com.karnama.app.ui.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karnama.app.data.repository.Task
import com.karnama.app.data.repository.TaskAction
import com.karnama.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActionsUiState(
    val task: Task? = null,
    val actions: List<TaskAction> = emptyList(),
    val newActionText: String = ""
)

class ActionsViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val taskFlow = MutableStateFlow<Task?>(null)
    private val newActionText = MutableStateFlow("")
    private val actionsFlow = taskRepository.observeActionsForTask(taskId)

    init {
        viewModelScope.launch {
            taskFlow.value = taskRepository.getTaskById(taskId)
        }
    }

    val uiState: StateFlow<ActionsUiState> = combine(
        taskFlow, actionsFlow, newActionText
    ) { task, actions, text ->
        ActionsUiState(task = task, actions = actions, newActionText = text)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActionsUiState())

    fun onNewActionTextChange(text: String) {
        newActionText.value = text
    }

    fun submitAction() {
        val text = newActionText.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            taskRepository.addAction(taskId, text)
            newActionText.value = ""
            taskFlow.value = taskRepository.getTaskById(taskId)
        }
    }
}
