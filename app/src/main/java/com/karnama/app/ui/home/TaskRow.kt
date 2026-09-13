@file:OptIn(ExperimentalFoundationApi::class)

package com.karnama.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.karnama.app.data.repository.Task

@Composable
fun TaskRow(
    task: Task,
    isSelected: Boolean,
    selectionMode: Boolean,
    onToggleCompleted: () -> Unit,
    onOpenActions: () -> Unit,
    onLongPress: () -> Unit,
    onTap: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(200),
        label = "taskTextColor"
    )
    val rowBg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent

    Row(
        modifier = Modifier
            .background(rowBg, RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { if (selectionMode) onTap() },
                onLongClick = onLongPress,
                onDoubleClick = onOpenActions
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { onToggleCompleted() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = buildAnnotatedString {
                if (task.completed) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(task.title)
                    }
                } else {
                    append(task.title)
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (task.hasActions) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onOpenActions)
            )
        }
    }
}
