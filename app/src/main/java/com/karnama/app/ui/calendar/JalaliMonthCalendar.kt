package com.karnama.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnama.app.util.PersianDate
import com.karnama.app.util.toFaDigits

/**
 * تقویم شمسی مینیمال. ماه و سال قابل تغییرند، روزها به‌صورت Grid نمایش
 * داده می‌شوند و روز انتخاب‌شده و امروز به‌شکل ظریف مشخص می‌شوند.
 */
@Composable
fun JalaliMonthCalendar(
    initialMonth: PersianDate = PersianDate.today(),
    selectedDate: PersianDate? = null,
    onDateSelected: (PersianDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var year by remember { mutableStateOf(initialMonth.year) }
    var month by remember { mutableStateOf(initialMonth.month) }
    val today = remember { PersianDate.today() }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (month == 1) { month = 12; year -= 1 } else { month -= 1 }
            }) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null)
            }
            Text(
                text = "${PersianDate.MONTH_NAMES[month - 1]} ${toFaDigits(year.toString())}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = {
                if (month == 12) { month = 1; year += 1 } else { month += 1 }
            }) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = null)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            // هفته از شنبه شروع می‌شود
            val shortNames = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
            shortNames.forEach { name ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val firstOfMonth = PersianDate(year, month, 1)
        val firstWeekdayIndex = PersianDate.WEEKDAY_NAMES.indexOf(firstOfMonth.weekdayName())
        val daysInMonth = firstOfMonth.daysInMonth()
        val totalCells = firstWeekdayIndex + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(modifier = Modifier.padding(top = 4.dp)) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - firstWeekdayIndex + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val date = PersianDate(year, month, dayNumber)
                                val isSelected = selectedDate == date
                                val isToday = today == date
                                val bg = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else -> Color.Transparent
                                }
                                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .fillMaxWidth()
                                        .background(bg, CircleShape)
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = toFaDigits(dayNumber.toString()),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
