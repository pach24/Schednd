package com.schednd.ui.detail

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schednd.domain.model.AttendanceTier
import com.schednd.ui.components.AvailabilityGrid
import com.schednd.ui.components.getHeatmapColor
import com.schednd.ui.theme.CardShape
import com.schednd.ui.theme.FadeIn
import com.schednd.ui.theme.FullRoundShape
import com.schednd.ui.theme.PhaseEnterTransition
import com.schednd.ui.theme.PhaseExitTransition
import com.schednd.ui.theme.SquircleCellShape
import com.schednd.ui.theme.SquircleMiniShape
import com.schednd.ui.theme.pressScale
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel,
    onBack: () -> Unit,
    onEditAvailability: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val hazeState = remember { HazeState() }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {

    Scaffold(
        modifier = Modifier.hazeSource(state = hazeState),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(uiState.event?.name ?: "Sesion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, "Volver")
                    }
                },
                actions = {
                    if (uiState.isCreator) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar sesion",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        // Animated content states — Trade Republic style scale+fade
        AnimatedContent(
            targetState = when {
                uiState.isLoading -> "loading"
                uiState.error != null -> "error"
                else -> "content"
            },
            transitionSpec = { PhaseEnterTransition togetherWith PhaseExitTransition },
            label = "DetailContent"
        ) { state ->
            when (state) {
                "loading" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
                "error" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Event code card
                        FadeIn(delayMs = 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CardShape,
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Codigo de la sesión",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = uiState.event?.code ?: "",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                letterSpacing = 4.sp
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(onClick = {
                                        clipboardManager.setText(
                                            AnnotatedString(uiState.event?.code ?: "")
                                        )
                                    }) {
                                        Icon(
                                            Icons.Filled.ContentCopy,
                                            "Copiar codigo",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = {
                                        val code = uiState.event?.code ?: return@IconButton
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "¡Únete a mi sesión de D&D en Schednd!\n\nEntra directamente aquí y solo pon tu nombre:\nschednd://join?code=$code\n\nO usa el código: $code"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(sendIntent, "Compartir codigo")
                                        )
                                    }) {
                                        Icon(
                                            Icons.Filled.Share,
                                            "Compartir",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Participants count
                        FadeIn(delayMs = 100) {
                            Text(
                                text = "Disponibilidad (${uiState.participants.size} participante(s))",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.participants.isEmpty()) {
                            FadeIn(delayMs = 200) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = CardShape,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(
                                        text = "Aun no hay participantes. Comparte el codigo para que se unan.",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            FadeIn(delayMs = 200) {
                                AvailabilityGrid(
                                    dates = uiState.datesAsLocal,
                                    participants = uiState.participants,
                                    participantAvailability = uiState.participantAvailability
                                )
                            }

                            val recommended = uiState.dateSummaries.filter {
                                it.tier == AttendanceTier.FULL || it.tier == AttendanceTier.VIABLE
                            }


// LEYENDA ESTILO GITHUB ACTUALIZADA POR UX
                            FadeIn(delayMs = 250) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Etiqueta principal
                                    Text(
                                        text = "Disponibilidad:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Texto "Baja"
                                    Text(
                                        text = "Baja",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Heatmap squares
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        repeat(7) { level ->
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp) // Reducido para mayor elegancia
                                                    .clip(SquircleMiniShape)
                                                    .background(getHeatmapColor(level, 6))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Texto "Alta"
                                    Text(
                                        text = "Alta",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (recommended.isNotEmpty()) {
                                val dateFormat = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
                                Spacer(modifier = Modifier.height(16.dp))
                                FadeIn(delayMs = 300) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = CardShape,
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "Fechas recomendadas",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            recommended.forEach { s ->
                                                val label = if (s.absentNames.isEmpty())
                                                    "Asistencia completa · ${s.count}/${s.total}"
                                                else
                                                    "Asisten ${s.count}/${s.total} · Falta: ${s.absentNames.joinToString(", ")}"
                                                Text(
                                                    text = "· ${dateFormat.format(s.date)}  –  $label",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(20.dp))

                        val editInteraction = remember { MutableInteractionSource() }
                        OutlinedButton(
                            onClick = onEditAvailability,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(editInteraction),
                            shape = FullRoundShape,
                            interactionSource = editInteraction
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar mi disponibilidad")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Share button
                        val shareInteraction = remember { MutableInteractionSource() }
                        OutlinedButton(
                            onClick = {
                                val code = uiState.event?.code ?: return@OutlinedButton
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "¡Únete a mi sesión de D&D en Schednd!\n\nEntra directamente aquí y solo pon tu nombre:\nschednd://join?code=$code\n\nO usa el código: $code"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    Intent.createChooser(sendIntent, "Compartir codigo")
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(shareInteraction),
                            shape = FullRoundShape,
                            interactionSource = shareInteraction
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartir con el grupo")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showDeleteDialog,
        enter = fadeIn(tween(220)),
        exit  = fadeOut(tween(200))
    ) {
        val animScope = this
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { showDeleteDialog = false },
            contentAlignment = Alignment.Center
        ) {
            with(animScope) {
                Box(
                    modifier = Modifier.animateEnterExit(
                        enter = scaleIn(
                            initialScale = 0.86f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness    = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(tween(180)),
                        exit = scaleOut(
                            targetScale   = 0.92f,
                            animationSpec = tween(160)
                        ) + fadeOut(tween(160))
                    )
                ) {
                    DeleteSessionDialog(
                        hazeState = hazeState,
                        onConfirm = {
                            showDeleteDialog = false
                            viewModel.deleteEvent()
                        }
                    )
                }
            }
        }
    }

    } // Box
}

@Composable
private fun DeleteSessionDialog(
    hazeState: HazeState,
    onConfirm: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val dialogShape = RoundedCornerShape(28.dp)

    val tintColor = if (isDark)
        Color(0xFF1C1C1E).copy(alpha = 0.82f)
    else
        Color.White.copy(alpha = 0.82f)

    val borderBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.White, Color.Transparent)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .wrapContentHeight()
            .clip(dialogShape)
            .hazeEffect(state = hazeState) {
                blurRadius = 20.dp
                backgroundColor = if (isDark) Color(0xFF1C1C1E) else Color.White
                tints = listOf(HazeTint(tintColor))
            }
            .border(1.dp, borderBrush, dialogShape)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Borrar sesion",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Seguro que quieres borrar esta sesion? Esta accion no se puede deshacer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
                    contentColor = MaterialTheme.colorScheme.error
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text("Borrar sesion", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
