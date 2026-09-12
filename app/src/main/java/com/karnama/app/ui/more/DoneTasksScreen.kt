package com.karnama.app.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karnama.app.R
import com.karnama.app.data.repository.Task
import com.karnama.app.data.repository.TaskRepository
import com.karnama.app.ui.home.TaskRow
import com.karnama.app.util.PersianDate
import com.karnama.app.util.toFaDigits
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DoneTasksViewModel(private val repository: TaskRepository) : ViewModel() {
    val tasks: StateFlow<List<Task>> = repository.observeCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleCompleted(task: Task) {
        viewModelScope.launch { repository.setCompleted(task.id, !task.completed) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneTasksScreen(viewModel: DoneTasksViewModel, onBack: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.more_done_tasks)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    val date = PersianDate.fromGregorian(LocalDate.ofEpochDay(task.dueDateEpochDay))
                    Column {
                        Text(
                            text = "${date.monthName()} ${toFaDigits(date.day.toString())}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        TaskRow(
                            task = task,
                            isSelected = false,
                            selectionMode = false,
                            onToggleCompleted = { viewModel.toggleCompleted(task) },
                            onOpenActions = {},
                            onLongPress = {},
                            onTap = {}
                        )
                    }
                }
            }
        }
    }
}
