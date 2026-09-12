package com.karnama.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnama.app.R

@Composable
fun DaySelector(
    selected: DayTab,
    onSelect: (DayTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayChip(
            label = stringResource(R.string.yesterday),
            selected = selected == DayTab.YESTERDAY,
            onClick = { onSelect(DayTab.YESTERDAY) }
        )
        DayChip(
            label = stringResource(R.string.today),
            selected = selected == DayTab.TODAY,
            onClick = { onSelect(DayTab.TODAY) }
        )
        DayChip(
            label = stringResource(R.string.tomorrow),
            selected = selected == DayTab.TOMORROW,
            onClick = { onSelect(DayTab.TOMORROW) }
        )
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val weight = if (selected) FontWeight.SemiBold else FontWeight.Normal

    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = weight
        )
    }
}
