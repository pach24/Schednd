package com.schednd.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schednd.model.Participant
import com.schednd.ui.theme.CalendarCellShape
import com.schednd.ui.theme.VerticalSquircleShape
import com.schednd.ui.theme.SchedndTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun getHeatmapColor(count: Int, total: Int): Color {
    val isDark = isSystemInDarkTheme()
    val heatmapGreens = if (isDark) {
        listOf(
            Color(0xFF161B22),
            Color(0xFF0E4429),
            Color(0xFF006D32),
            Color(0xFF26A641),
            Color(0xFF39D353),
            Color(0xFF53E06C),
            Color(0xFF7CF08D)
        )
    } else {
        listOf(
            Color(0xFFEBEDF0),
            Color(0xFF9BE9A8),
            Color(0xFF40C463),
            Color(0xFF30A14E),
            Color(0xFF216E39),
            Color(0xFF19532A),
            Color(0xFF0D3D1E)
        )
    }
    if (total <= 0 || count == 0) return heatmapGreens[0]
    val ratio = count.toFloat() / total
    val index = (ratio * (heatmapGreens.size - 1)).toInt().coerceIn(0, heatmapGreens.size - 1)
    return heatmapGreens[index]
}

@Composable
fun AvailabilityGrid(
    dates: List<LocalDate>,
    participants: List<Participant>,
    participantAvailability: Map<String, Set<LocalDate>>,
    modifier: Modifier = Modifier
) {
    val total = participants.size
    val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", Locale("es"))
    val dayNumFormatter = DateTimeFormatter.ofPattern("d", Locale("es"))
    val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale("es"))

    val cellSize = 48.dp
    val nameWidth = 85.dp
    val unavailableColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    val bestAlpha = 0.12f

    // Control de la franja (Píldora)
    val expansionDp = 0.dp // Ancho extra por lado
    val expansionY = 12.dp // Alto extra SOLO hacia arriba

    val dateCounts = remember(dates, participants, participantAvailability) {
        dates.associateWith { date ->
            participants.count { p -> date in (participantAvailability[p.userId] ?: emptySet()) }
        }
    }
    val maxCount = dateCounts.values.maxOrNull() ?: 0
    val bestDates = if (maxCount > 0) dateCounts.filterValues { it == maxCount }.keys else emptySet()

    val gridAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { gridAlpha.animateTo(1f, tween(800, easing = EaseOutBack)) }

    Column(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .graphicsLayer { alpha = gridAlpha.value }
            .padding(16.dp)
    ) {
        // --- HEADER ROW (DATES) ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(nameWidth))
            dates.forEach { date ->
                val isBest = date in bestDates
                // Aumentamos la altura de la celda del header específicamente
                Box(
                    modifier = Modifier.size(width = cellSize, height = cellSize + 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBest) {
                        // FRANJA: Expansión hacia arriba y hacia los lados
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .layout { measurable, constraints ->
                                    val expX = expansionDp.roundToPx()
                                    val expY = expansionY.roundToPx()
                                    val targetWidth = constraints.maxWidth + (expX * 2)
                                    val targetHeight = constraints.maxHeight + expY
                                    val placeable = measurable.measure(
                                        constraints.copy(
                                            minWidth = targetWidth, maxWidth = targetWidth,
                                            minHeight = targetHeight, maxHeight = targetHeight
                                        )
                                    )
                                    layout(constraints.maxWidth, constraints.maxHeight) {
                                        placeable.place(-expX, -expY)
                                    }
                                }
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = bestAlpha),
                                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            // Usamos el shape solicitado
                            .clip(VerticalSquircleShape(cornerRadius = 14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dayNameFormatter.format(date).take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, lineHeight = 8.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dayNumFormatter.format(date),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = monthFormatter.format(date).take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )

                        // El punto solo aparece si es uno de los mejores días
                        if (isBest) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF0082F3))
                            )
                        }
                    }
                }
            }
        }


        // --- PARTICIPANT ROWS ---
        participants.forEach { participant ->
            val available = participantAvailability[participant.userId] ?: emptySet()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = participant.name,
                    modifier = Modifier
                        .width(nameWidth)
                        .padding(end = 6.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                dates.forEach { date ->
                    val isAvailable = date in available
                    val count = dateCounts[date] ?: 0
                    val isBest = date in bestDates
                    Box(
                        modifier = Modifier.size(cellSize),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBest) {
                            // FRANJA: Cuerpo central recto, solo expansión lateral
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .layout { measurable, constraints ->
                                        val expX = expansionDp.roundToPx()
                                        val targetWidth = constraints.maxWidth + (expX * 2)
                                        val placeable = measurable.measure(
                                            constraints.copy(
                                                minWidth = targetWidth,
                                                maxWidth = targetWidth
                                            )
                                        )
                                        layout(constraints.maxWidth, constraints.maxHeight) {
                                            placeable.place(-expX, 0)
                                        }
                                    }
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = bestAlpha))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(2.dp)
                                .clip(CalendarCellShape)
                                .background(
                                    if (isAvailable) getHeatmapColor(
                                        count,
                                        total
                                    ) else unavailableColor
                                )
                        )
                    }
                }
            }
        }

        // --- SUMMARY ROW ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Total",
                modifier = Modifier.width(nameWidth),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
            )
            dates.forEach { date ->
                val count = dateCounts[date] ?: 0
                val isBest = date in bestDates
                Box(
                    modifier = Modifier.size(cellSize),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBest) {
                        // FRANJA: Parte inferior. Expansión lateral, pero NO hacia abajo (expY = 0)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .layout { measurable, constraints ->
                                    val expX = expansionDp.roundToPx()
                                    val targetWidth = constraints.maxWidth + (expX * 2)
                                    val placeable = measurable.measure(
                                        constraints.copy(
                                            minWidth = targetWidth,
                                            maxWidth = targetWidth,
                                            minHeight = constraints.maxHeight, // Altura exacta de la celda
                                            maxHeight = constraints.maxHeight
                                        )
                                    )
                                    layout(constraints.maxWidth, constraints.maxHeight) {
                                        placeable.place(-expX, 0)
                                    }
                                }
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = bestAlpha),
                                    shape = RoundedCornerShape(
                                        bottomStart = 16.dp,
                                        bottomEnd = 16.dp
                                    )
                                )
                        )
                    }
                    Text(
                        text = "$count/$total",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.ExtraBold),
                        color = if (count > 0) {
                            if (isSystemInDarkTheme()) Color.White else Color.Black
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Light Mode", showBackground = true)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AvailabilityGridPreview() {
    SchedndTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val today = remember { LocalDate.now() }
            val dates = remember { (0..5).map { today.plusDays(it.toLong()) } }
            val participants = remember {
                listOf(
                    Participant(userId = "1", name = "Alex"),
                    Participant(userId = "2", name = "Beatriz"),
                    Participant(userId = "3", name = "Carlos"),
                    Participant(userId = "4", name = "Diana"),
                    Participant(userId = "5", name = "Eduardo"),
                    Participant(userId = "6", name = "Fabiola")
                )
            }
            val availability = remember {
                mapOf(
                    "1" to setOf(dates[0], dates[1], dates[2]),
                    "2" to setOf(dates[0], dates[2], dates[4]),
                    "3" to setOf(dates[0], dates[1]),
                    "4" to setOf(dates[0], dates[3], dates[5]),
                    "5" to setOf(dates[0], dates[1], dates[2], dates[3]),
                    "6" to setOf(dates[0], dates[1], dates[2], dates[3], dates[4], dates[5])
                )
            }
            Box(modifier = Modifier.padding(16.dp)) {
                AvailabilityGrid(dates = dates, participants = participants, participantAvailability = availability)
            }
        }
    }
}