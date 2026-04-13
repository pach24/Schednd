package com.schednd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schednd.model.Participant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AvailabilityGrid(
    dates: List<LocalDate>,
    participants: List<Participant>,
    participantAvailability: Map<String, Set<LocalDate>>,
    bestDates: Set<LocalDate>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es"))
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale("es"))

    val cellSize = 56.dp
    val nameWidth = 100.dp
    val availableColor = Color(0xFF4CAF50)
    val unavailableColor = MaterialTheme.colorScheme.surfaceVariant
    val bestColor = Color(0xFFFFD700)

    Column(modifier = modifier.horizontalScroll(rememberScrollState())) {
        // Header row: dates
        Row {
            Box(modifier = Modifier.width(nameWidth))
            dates.forEach { date ->
                Column(
                    modifier = Modifier.width(cellSize),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayFormatter.format(date).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(date),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (date in bestDates) FontWeight.Bold else FontWeight.Normal,
                        color = if (date in bestDates) bestColor else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Participant rows
        participants.forEach { participant ->
            val available = participantAvailability[participant.userId] ?: emptySet()
            Row(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant.name,
                    modifier = Modifier
                        .width(nameWidth)
                        .padding(end = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                dates.forEach { date ->
                    val isAvailable = date in available
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isAvailable) availableColor.copy(alpha = 0.7f)
                                else unavailableColor
                            )
                            .then(
                                if (date in bestDates && isAvailable)
                                    Modifier.border(
                                        2.dp,
                                        bestColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAvailable) {
                            Text(
                                text = "OK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Summary row: count per date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Total",
                modifier = Modifier.width(nameWidth),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            dates.forEach { date ->
                val count = participants.count { p ->
                    (participantAvailability[p.userId] ?: emptySet()).contains(date)
                }
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count/${participants.size}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (date in bestDates) bestColor else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
