package com.schednd.ui.components

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarGrid(
    selectedDates: Set<LocalDate>,
    onDateToggled: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    selectableDates: Set<LocalDate>? = null,
    minDate: LocalDate = LocalDate.now(),
    dateAttendeeCount: Map<LocalDate, Int> = emptyMap()
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(minDate)) }

    Column(modifier = modifier) {
        // Month navigation header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val prev = currentMonth.minusMonths(1)
                    if (!prev.isBefore(YearMonth.from(minDate))) {
                        currentMonth = prev
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente")
            }
        }

        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val dayNames = listOf("L", "M", "X", "J", "V", "S", "D")
            dayNames.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Calendar days
        val firstDayOfMonth = currentMonth.atDay(1)
        // Monday = 1, so offset = dayOfWeek - 1
        val startOffset = firstDayOfMonth.dayOfWeek.value - 1
        val daysInMonth = currentMonth.lengthOfMonth()

        val cells = buildList {
            repeat(startOffset) { add(null) }
            for (day in 1..daysInMonth) {
                add(currentMonth.atDay(day))
            }
        }

        // Use Row-based grid instead of LazyVerticalGrid to avoid
        // nested scrollable crash inside verticalScroll Column
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    if (date == null) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val isPast = date.isBefore(minDate)
                        val isSelectable = !isPast && (selectableDates == null || date in selectableDates)
                        val isSelected = date in selectedDates

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .then(
                                    if (isSelectable) {
                                        Modifier.clickable { onDateToggled(date) }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val count = dateAttendeeCount[date] ?: 0
                            val textColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                !isSelectable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${date.dayOfMonth}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor
                                )
                                if (count > 0) {
                                    Text(
                                        text = "+$count",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp
                                        ),
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                // Fill remaining slots in the last row
                repeat(7 - week.size) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}
