package com.schednd.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schednd.domain.model.AttendanceTier
import com.schednd.domain.model.computeAttendanceTier
import com.schednd.model.Participant
import com.schednd.ui.theme.SquircleCellShape
import com.schednd.ui.theme.SquircleHeaderShape
import com.schednd.ui.theme.TierFull
import com.schednd.ui.theme.TierInsufficient
import com.schednd.ui.theme.TierLimited
import com.schednd.ui.theme.TierViable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun tierColor(tier: AttendanceTier): Color = when (tier) {
    AttendanceTier.FULL -> TierFull
    AttendanceTier.VIABLE -> TierViable
    AttendanceTier.LIMITED -> TierLimited
    AttendanceTier.INSUFFICIENT -> TierInsufficient
}

private fun tierIcon(tier: AttendanceTier): ImageVector = when (tier) {
    AttendanceTier.FULL -> Icons.Filled.CheckCircle
    AttendanceTier.VIABLE -> Icons.Filled.ThumbUp
    AttendanceTier.LIMITED -> Icons.Filled.ThumbDown
    AttendanceTier.INSUFFICIENT -> Icons.Filled.Cancel
}

private val CellShape = SquircleCellShape
private val HeaderShape = SquircleHeaderShape

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
    val unavailableColor = MaterialTheme.colorScheme.surfaceVariant

    // Grid fade-in animation
    val gridAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        gridAlpha.animateTo(1f, tween(500))
    }

    Column(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .graphicsLayer { alpha = gridAlpha.value }
    ) {
        // Header row
        Row {
            Box(modifier = Modifier.width(nameWidth))
            dates.forEachIndexed { colIndex, date ->
                val count = participants.count { p ->
                    date in (participantAvailability[p.userId] ?: emptySet())
                }
                val tier = computeAttendanceTier(count, total)
                val color = tierColor(tier)
                val icon = tierIcon(tier)

                // Staggered fade-in for columns
                val colAlpha = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(colIndex * 40L)
                    colAlpha.animateTo(1f, tween(350))
                }

                Column(
                    modifier = Modifier
                        .width(cellSize)
                        .padding(2.dp)
                        .graphicsLayer { alpha = colAlpha.value }
                        .border(2.dp, color.copy(alpha = 0.75f), HeaderShape)
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
        participants.forEachIndexed { rowIndex, participant ->
            val available = participantAvailability[participant.userId] ?: emptySet()

            // Staggered row fade-in
            val rowAlpha = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(rowIndex * 50L)
                rowAlpha.animateTo(1f, tween(350))
            }

            Row(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .graphicsLayer { alpha = rowAlpha.value },
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
                    val tier = computeAttendanceTier(count, total)
                    val borderColor = tierColor(tier)

                    val cellBorderColor = if (isAvailable)
                        borderColor.copy(alpha = 0.75f)
                    else
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(2.dp)
                            .clip(CellShape)
                            .background(
                                if (isAvailable) TierFull.copy(alpha = 0.15f)
                                else unavailableColor
                            )
                            .border(2.dp, cellBorderColor, CellShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAvailable) {
                            Text(
                                text = "OK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TierFull
                            )
                        }
                    }
                }
            }
        }

        // Summary row
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
                val tier = computeAttendanceTier(count, total)
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
