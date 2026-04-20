package com.schednd.ui.detail

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schednd.ui.components.AppleTextField
import com.schednd.ui.components.CalendarGrid
import com.schednd.ui.components.LoadingDots
import com.schednd.ui.theme.FadeIn
import com.schednd.ui.theme.FullRoundShape
import com.schednd.ui.theme.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAvailabilityScreen(
    viewModel: EventDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSavingAvailability) {
        // Nothing: saving feedback is rendered in-place via the button spinner.
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Mi disponibilidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            FadeIn(delayMs = 0) {
                Text(
                    text = "Marca los dias en los que puedes",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            FadeIn(delayMs = 80) {
                AppleTextField(
                    value = uiState.myName,
                    onValueChange = viewModel::onMyNameChanged,
                    label = "Tu nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            FadeIn(delayMs = 150) {
                Text(
                    text = "${uiState.myDraftDates.size} dia(s) seleccionado(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            FadeIn(delayMs = 220) {
                val attendeeCounts = uiState.participantAvailability.values
                    .flatMap { it }
                    .groupingBy { it }
                    .eachCount()
                Box(modifier = Modifier.fillMaxWidth()) {
                    CalendarGrid(
                        selectedDates = uiState.myDraftDates,
                        onDateToggled = viewModel::onMyDateToggled,
                        dateAttendeeCount = attendeeCounts,
                        mySavedDates = uiState.mySavedDates
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FadeIn(delayMs = 320) {
                val saveInteraction = remember { MutableInteractionSource() }
                Button(
                    onClick = {
                        viewModel.saveMyAvailability()
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(saveInteraction),
                    shape = FullRoundShape,
                    interactionSource = saveInteraction,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    enabled = uiState.myName.isNotBlank()
                            && uiState.myDraftDates.isNotEmpty()
                            && !uiState.isSavingAvailability
                ) {
                    if (uiState.isSavingAvailability) {
                        LoadingDots(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                    Text("Guardar")
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
