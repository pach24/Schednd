package com.schednd.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.schednd.R
import com.schednd.ui.theme.CardShape
import com.schednd.ui.theme.FadeIn
import com.schednd.ui.theme.FullRoundShape
import com.schednd.ui.theme.PhaseEnterTransition
import com.schednd.ui.theme.PhaseExitTransition
import com.schednd.ui.theme.pressScale
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onCreateEvent: () -> Unit,
    onJoinEvent: () -> Unit,
    onOpenEvent: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        viewModel.refresh()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            FadeIn(delayMs = 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "S&D",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Organiza tus sesiones de D&D",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Animated loading/content transition — Trade Republic scale+fade
            AnimatedContent(
                targetState = uiState.isAuthReady || uiState.error != null,
                transitionSpec = { PhaseEnterTransition togetherWith PhaseExitTransition },
                label = "AuthContent"
            ) { isReady ->
                if (!isReady) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                } else if (uiState.error != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FadeIn(delayMs = 150) {
                            val createInteraction = remember { MutableInteractionSource() }
                            Button(
                                onClick = onCreateEvent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressScale(createInteraction),
                                shape = FullRoundShape,
                                interactionSource = createInteraction,
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text("Crear sesion", modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FadeIn(delayMs = 250) {
                            val joinInteraction = remember { MutableInteractionSource() }
                            OutlinedButton(
                                onClick = onJoinEvent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressScale(joinInteraction),
                                shape = FullRoundShape,
                                interactionSource = joinInteraction
                            ) {
                                Text("Unirse a sesion", modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }

                        if (uiState.recentEvents.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(40.dp))

                            FadeIn(delayMs = 350) {
                                Text(
                                    text = "Mis sesiones",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            uiState.recentEvents.forEachIndexed { index, event ->
                                // Staggered card animation
                                val cardAlpha = remember { Animatable(0f) }
                                val cardOffsetY = remember { Animatable(40f) }
                                LaunchedEffect(Unit) {
                                    kotlinx.coroutines.delay((350 + index * 70).toLong())
                                    launch { cardAlpha.animateTo(1f, tween(400)) }
                                    cardOffsetY.animateTo(
                                        0f,
                                        spring(
                                            dampingRatio = 0.72f,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }

                                val cardInteraction = remember { MutableInteractionSource() }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .graphicsLayer {
                                            alpha = cardAlpha.value
                                            translationY = cardOffsetY.value
                                        }
                                        .pressScale(cardInteraction)
                                        .clickable(
                                            interactionSource = cardInteraction,
                                            indication = null
                                        ) { onOpenEvent(event.code) },
                                    shape = CardShape,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = event.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = event.code,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    letterSpacing = 2.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
