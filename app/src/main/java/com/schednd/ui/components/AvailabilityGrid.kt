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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schednd.model.Participant
import com.schednd.ui.detail.AttendanceTier
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val tierGreen = Color(0xFF4CAF50)
private val tierYellow = Color(0xFFFFC107)
private val tierOrange = Color(0xFFFF9800)
private val tierRed = Color(0xFFF44336)

private fun tierColor(tier: AttendanceTier): Color = when (tier) {
    AttendanceTier.FULL -> tierGreen
    AttendanceTier.VIABLE -> tierYellow
    AttendanceTier.LIMITED -> tierOrange
    AttendanceTier.INSUFFICIENT -> tierRed
}

private fun tierIcon(tier: AttendanceTier): ImageVector = when (tier) {
    AttendanceTier.FULL -> Icons.Filled.CheckCircle
    AttendanceTier.VIABLE -> Icons.Filled.ThumbUp
    AttendanceTier.LIMITED -> Icons.Filled.ThumbDown
    AttendanceTier.INSUFFICIENT -> Icons.Filled.Cancel
}

private fun attendanceTier(count: Int, total: Int): AttendanceTier {
    if (total == 0) return AttendanceTier.INSUFFICIENT
    val pct = count.toDouble() / total
    return when {
        pct >= 0.86 -> AttendanceTier.FULL
        pct >= 0.71 -> AttendanceTier.VIABLE
        pct >= 0.57 -> AttendanceTier.LIMITED
        else -> AttendanceTier.INSUFFICIENT
    }
}

@Composable
fun AvailabilityGrid(
    dates: List<LocalDate>,
    participants: List<Participant>,
    participantAvailability: Map<String, Set<LocalDate>>,
    modifier: Modifier = Modifier
) {
    val total = participants.size
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es"))
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale("es"))

    val cellSize = 56.dp
    val nameWidth = 100.dp
    val availableColor = Color(0xFF4CAF50)
    val unavailableColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier.horizontalScroll(rememberScrollState())) {
        // Header row: dates with colored borders and tier icons
        Row {
            Box(modifier = Modifier.width(nameWidth))
            dates.forEach { date ->
                val count = participants.count { p ->
                    date in (participantAvailability[p.userId] ?: emptySet())
                }
                val tier = attendanceTier(count, total)
                val color = tierColor(tier)
                val icon = tierIcon(tier)

                Column(
                    modifier = Modifier
                        .width(cellSize)
                        .padding(2.dp)
                        .border(2.dp, color, RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
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
                    val count = participants.count { p ->
                        date in (participantAvailability[p.userId] ?: emptySet())
                    }
                    val tier = attendanceTier(count, total)
                    val borderColor = tierColor(tier)

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
                                if (isAvailable)
                                    Modifier.border(1.5.dp, borderColor, RoundedCornerShape(4.dp))
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
                val tier = attendanceTier(count, total)
                val color = tierColor(tier)
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count/$total",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = color
                    )
                }
            }
        }
    }
}
