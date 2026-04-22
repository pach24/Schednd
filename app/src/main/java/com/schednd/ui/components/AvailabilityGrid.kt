package com.schednd.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.getValue
import com.schednd.ui.theme.CalendarCellShape
import com.schednd.ui.theme.SchedndTheme


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
fun getHeatmapColor(count: Int, total: Int): Color {
    val isDark = isSystemInDarkTheme()

    // Paleta oficial de contribuciones de GitHub
    val heatmapGreens = if (isDark) {
        listOf(
            Color(0xFF161B22), // Nivel 0: Fondo vacío (GitHub Dark)
            Color(0xFF0E4429), // Nivel 1: Verde muy oscuro
            Color(0xFF006D32), // Nivel 2
            Color(0xFF26A641), // Nivel 3
            Color(0xFF39D353), // Nivel 4: Verde brillante
            Color(0xFF53E06C), // Nivel 5
            Color(0xFF7CF08D)  // Nivel 6: Verde máximo (Vibrante)
        )
    } else {
        listOf(
            Color(0xFFEBEDF0), // Nivel 0: Gris/Verde tenue (GitHub Light)
            Color(0xFF9BE9A8), // Nivel 1: Verde muy claro
            Color(0xFF40C463), // Nivel 2
            Color(0xFF30A14E), // Nivel 3
            Color(0xFF216E39), // Nivel 4: Verde bosque
            Color(0xFF19532A), // Nivel 5
            Color(0xFF0D3D1E)  // Nivel 6: Verde máximo (Oscuro)
        )
    }

    if (total <= 0 || count == 0) return heatmapGreens[0]

    val ratio = count.toFloat() / total
    // Mapeo proporcional a los 7 niveles
    val index = (ratio * (heatmapGreens.size - 1)).toInt().coerceIn(0, heatmapGreens.size - 1)
    return heatmapGreens[index]
}

// Paletas de glow independientes por tema: ajustables por separado.
// Nivel 0 = sin disponibilidad → nunca se pinta (el halo sólo se dibuja si shouldPulse).
private val GlowGreensDark = listOf(
    Color(0xFF0E4429),
    Color(0xFF006D32),
    Color(0xFF26A641),
    Color(0xFF39D353),
    Color(0xFF53E06C),
    Color(0xFF7CF08D)
)

// Color del anillo pulsante en modo claro (borde sólido, no glow).
private val LightPulseRingColor = Color(0xFF2196F3)

private fun getGlowColor(count: Int, total: Int): Color {
    val palette = GlowGreensDark
    if (total <= 0 || count == 0) return palette[0]
    val ratio = count.toFloat() / total
    val index = (ratio * (palette.size - 1)).toInt().coerceIn(0, palette.size - 1)
    return palette[index]
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

    val cellSize = 40.dp
    val nameWidth = 75.dp
    val unavailableColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)

    val dateCounts = remember(dates, participants, participantAvailability) {
        dates.associateWith { date ->
            participants.count { p -> date in (participantAvailability[p.userId] ?: emptySet()) }
        }
    }
    val maxCount = dateCounts.values.maxOrNull() ?: 0
    val bestDates = if (maxCount > 0) {
        dateCounts.filterValues { it == maxCount }.keys
    } else {
        emptySet()
    }

    // Animación de entrada de la rejilla completa
    val gridAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { gridAlpha.animateTo(1f, tween(800, easing = EaseOutBack)) }

    // Transición infinita para el efecto "juicy"
    val infiniteTransition = rememberInfiniteTransition(label = "juicyPulse")

    Column(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .graphicsLayer { alpha = gridAlpha.value }
            .padding(16.dp)
    ) {
        // --- HEADER ROW (DATES) - SIN PULSO ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(nameWidth))
            dates.forEach { date ->
                Column(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(2.dp)
                        .clip(CalendarCellShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayNameFormatter.format(date).take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, lineHeight = 7.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dayNumFormatter.format(date),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = monthFormatter.format(date).take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, fontWeight = FontWeight.Bold),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }
        }

        // --- PARTICIPANT ROWS - CON PULSO "JUICY" ---
        participants.forEachIndexed { rowIndex, participant ->
            val available = participantAvailability[participant.userId] ?: emptySet()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = participant.name,
                    modifier = Modifier.width(nameWidth).padding(end = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                dates.forEach { date ->
                    val isAvailable = date in available
                    val count = dateCounts[date] ?: 0
                    val cellIntensityColor = getHeatmapColor(count, total)
                    val shouldPulse = isAvailable && date in bestDates

                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 0.95f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(rowIndex * 120)
                        ),
                        label = "cellPulse"
                    )

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (shouldPulse) {
                            if (isSystemInDarkTheme()) {
                                val glowColor = getGlowColor(count, total)
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .graphicsLayer {
                                            scaleX = pulseScale * 1.6f
                                            scaleY = pulseScale * 1.6f
                                        }
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    glowColor.copy(alpha = 0.75f),
                                                    glowColor.copy(alpha = 0.35f),
                                                    Color.Transparent
                                                )
                                            ),
                                            shape = CalendarCellShape
                                        )
                                )
                            } else {
                                val fraction = ((pulseScale - 0.6f) / 0.35f).coerceIn(0f, 1f)
                                val ringScale = .95f + 0.20f * fraction
                                val borderAlpha = 1f - 0.35f * fraction
                                val borderWidth = lerp(2.5.dp, 1.5.dp, fraction)
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .graphicsLayer {
                                            scaleX = ringScale
                                            scaleY = ringScale
                                        }
                                        .border(
                                            width = borderWidth,
                                            color = LightPulseRingColor.copy(alpha = borderAlpha),
                                            shape = CalendarCellShape
                                        )
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CalendarCellShape)
                                .background(if (isAvailable) cellIntensityColor else unavailableColor)
                        )
                    }
                }
            }
        }

        // --- SUMMARY ROW (TOTAL) - SIN PULSO ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = "Total",
                modifier = Modifier.width(nameWidth),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            dates.forEach { date ->
                val count = dateCounts[date] ?: 0
                val color = getHeatmapColor(count, total)
                Box(
                    modifier = Modifier.size(cellSize).padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count/$total",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold),
                        color = if (count > 0) color else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Light Mode",
    showBackground = true
)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AvailabilityGridPreview() {
    // Usamos el tema real de tu aplicación
    SchedndTheme {
        // Surface aplica automáticamente DarkBackground o LightBackground de tu Theme.kt
        androidx.compose.material3.Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val today = remember { LocalDate.now() }
            // Generamos una semana de ejemplo
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
                AvailabilityGrid(
                    dates = dates,
                    participants = participants,
                    participantAvailability = availability
                )
            }
        }
    }
}
