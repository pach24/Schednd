package com.schednd.ui.join

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schednd.ui.components.CalendarGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinEventScreen(
    onJoined: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: JoinEventViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onJoined(uiState.code)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unirse a evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Phase 1: Enter code and name
            OutlinedTextField(
                value = uiState.code,
                onValueChange = viewModel::onCodeChanged,
                label = { Text("Codigo del evento") },
                placeholder = { Text("ABC123") },
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.participantName,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Tu nombre") },
                placeholder = { Text("Ej: Gandalf") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.event == null) {
                // Look up button
                Button(
                    onClick = viewModel::onLookUp,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.code.length == 6
                            && uiState.participantName.isNotBlank()
                            && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text("Buscar evento")
                }
            } else {
                // Phase 2: Event found, select availability
                Text(
                    text = uiState.event!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Selecciona los dias que puedes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${uiState.selectedDates.size} dia(s) seleccionado(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                CalendarGrid(
                    selectedDates = uiState.selectedDates,
                    onDateToggled = viewModel::onDateToggled,
                    dateAttendeeCount = uiState.dateAttendeeCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.selectedDates.isNotEmpty() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text("Confirmar disponibilidad")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
