package com.schednd.ui.join

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.schednd.ui.components.AppleTextField
import com.schednd.ui.components.CalendarGrid
import com.schednd.ui.components.LoadingDots
import com.schednd.ui.theme.FadeIn
import com.schednd.ui.theme.FullRoundShape
import com.schednd.ui.theme.PhaseEnterTransition
import com.schednd.ui.theme.PhaseExitTransition
import com.schednd.ui.theme.pressScale

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Unirse a sesion") },
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
            FadeIn(delayMs = 0) {
                AppleTextField(
                    value = uiState.code,
                    onValueChange = viewModel::onCodeChanged,
                    label = "Codigo de la sesión",
                    placeholder = "ABC123",
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FadeIn(delayMs = 100) {
                AppleTextField(
                    value = uiState.participantName,
                    onValueChange = viewModel::onNameChanged,
                    label = "Tu nombre",
                    placeholder = "Ej: Gandalf",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated phase transition
            AnimatedContent(
                targetState = uiState.event != null,
                transitionSpec = { PhaseEnterTransition togetherWith PhaseExitTransition },
                label = "JoinPhaseTransition"
            ) { hasEvent ->
                if (!hasEvent) {
                    // Look up button
                    FadeIn(delayMs = 200) {
                        val lookupInteraction = remember { MutableInteractionSource() }
                        Button(
                            onClick = viewModel::onLookUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(lookupInteraction),
                            shape = FullRoundShape,
                            interactionSource = lookupInteraction,
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            enabled = uiState.code.length == 6
                                    && uiState.participantName.isNotBlank()
                                    && !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                LoadingDots(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                            }
                            Text("Buscar sesion", modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                } else {
                    // Phase 2: Event found, select availability
                    Column {
                        FadeIn(delayMs = 0) {
                            Column {
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
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FadeIn(delayMs = 150) {
                            CalendarGrid(
                                selectedDates = uiState.selectedDates,
                                onDateToggled = viewModel::onDateToggled,
                                dateAttendeeCount = uiState.dateAttendeeCount
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        FadeIn(delayMs = 300) {
                            val confirmInteraction = remember { MutableInteractionSource() }
                            Button(
                                onClick = viewModel::onSubmit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressScale(confirmInteraction),
                                shape = FullRoundShape,
                                interactionSource = confirmInteraction,
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                ),
                                enabled = uiState.selectedDates.isNotEmpty() && !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    LoadingDots(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                }
                                Text("Confirmar disponibilidad", modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
