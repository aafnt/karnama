package com.karnama.app.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karnama.app.R
import com.karnama.app.util.toFaDigits

@Composable
fun SelectionToolbar(
    selectedCount: Int,
    onClose: () -> Unit,
    onMoveToTomorrow: () -> Unit,
    onMoveToDate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.cancel))
        }
        Text(
            text = stringResource(R.string.selected_count, toFaDigits(selectedCount.toString())),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onMoveToTomorrow) {
            Text(stringResource(R.string.move_to_tomorrow))
        }
        TextButton(onClick = onMoveToDate) {
            Text(stringResource(R.string.move_to_date))
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
