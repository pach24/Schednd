package com.schednd.ui.detail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schednd.domain.model.AttendanceTier
import com.schednd.ui.components.AvailabilityGrid
import com.schednd.ui.components.CalendarGrid
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditAvailability by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onBack()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Borrar sesión") },
            text = { Text("¿Seguro que quieres borrar esta sesión? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteEvent()
                }) {
                    Text("Borrar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.event?.name ?: "Sesión") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (uiState.isCreator) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar sesión",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = uiState.event?.code ?: "",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 4.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            IconButton(onClick = {
                                val code = uiState.event?.code ?: return@IconButton
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Unete a mi sesion de D&D en Schednd con el codigo: $code"
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
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Participants count
                    Text(
                        text = "Disponibilidad (${uiState.participants.size} participante(s))",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.participants.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Aun no hay participantes. Comparte el codigo para que se unan.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Availability grid
                        AvailabilityGrid(
                            dates = uiState.datesAsLocal,
                            participants = uiState.participants,
                            participantAvailability = uiState.participantAvailability
                        )

                        // Date summaries card (exclude INSUFFICIENT)
                        val visibleSummaries = uiState.dateSummaries
                            .filter { it.tier != AttendanceTier.INSUFFICIENT }
                        if (visibleSummaries.isNotEmpty()) {
                            val dateFormat = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val optimal = visibleSummaries.filter {
                                        it.tier == AttendanceTier.FULL || it.tier == AttendanceTier.VIABLE
                                    }
                                    val others = visibleSummaries.filter {
                                        it.tier == AttendanceTier.LIMITED
                                    }

                                    if (optimal.isNotEmpty()) {
                                        Text(
                                            text = "Fechas recomendadas",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        optimal.forEach { s ->
                                            val label = if (s.absentNames.isEmpty())
                                                "Asistencia completa · ${s.count}/${s.total}"
                                            else
                                                "Asisten ${s.count}/${s.total} · Falta: ${s.absentNames.joinToString(", ")}"
                                            Text(
                                                text = "• ${dateFormat.format(s.date)}  –  $label",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (others.isNotEmpty()) {
                                        if (optimal.isNotEmpty()) Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Otras opciones",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        others.forEach { s ->
                                            val label = "Asisten ${s.count}/${s.total} · Faltan: ${s.absentNames.joinToString(", ")}"
                                            Text(
                                                text = "• ${dateFormat.format(s.date)}  –  $label",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit my availability
                    if (!showEditAvailability) {
                        OutlinedButton(
                            onClick = { showEditAvailability = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar mi disponibilidad")
                        }
                    } else {
                        Text(
                            text = "Mi disponibilidad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.myName,
                            onValueChange = viewModel::onMyNameChanged,
                            label = { Text("Tu nombre") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${uiState.myDraftDates.size} dia(s) seleccionado(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val attendeeCounts = uiState.participantAvailability.values
                            .flatMap { it }
                            .groupingBy { it }
                            .eachCount()
                        CalendarGrid(
                            selectedDates = uiState.myDraftDates,
                            onDateToggled = viewModel::onMyDateToggled,
                            dateAttendeeCount = attendeeCounts,
                            mySavedDates = uiState.mySavedDates
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.saveMyAvailability()
                                showEditAvailability = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.myName.isNotBlank()
                                    && uiState.myDraftDates.isNotEmpty()
                                    && !uiState.isSavingAvailability
                        ) {
                            if (uiState.isSavingAvailability) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Text("Guardar")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        TextButton(
                            onClick = { showEditAvailability = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Share button
                    OutlinedButton(
                        onClick = {
                            val code = uiState.event?.code ?: return@OutlinedButton
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Unete a mi sesion de D&D en Schednd con el codigo: $code"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, "Compartir codigo")
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
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
