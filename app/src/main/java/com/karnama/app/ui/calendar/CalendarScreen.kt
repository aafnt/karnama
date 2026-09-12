package com.karnama.app.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnama.app.R
import com.karnama.app.ui.home.TaskRow

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    bottomBar: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.calendar_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
            )

            JalaliMonthCalendar(
                initialMonth = uiState.selectedDate,
                selectedDate = uiState.selectedDate,
                onDateSelected = viewModel::selectDate
            )

            Divider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = uiState.selectedDate.formatFull(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.tasks.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_today_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn {
                    items(uiState.tasks, key = { it.id }) { task ->
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
