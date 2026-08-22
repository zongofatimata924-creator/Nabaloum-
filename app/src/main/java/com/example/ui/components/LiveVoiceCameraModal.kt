package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun LiveVoiceCameraModal(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isCameraEnabled by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isSpeakerMuted by remember { mutableStateOf(false) }

    // Live conversation dialogue simulation
    var speechState by remember { mutableStateOf("À l'écoute...") }
    var aiSubtitle by remember {
        mutableStateOf("Je vous écoute et j'analyse le flux visuel en direct. Que souhaitez-vous savoir ?")
    }

    // Audio Waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    LaunchedEffect(Unit) {
        delay(3500)
        speechState = "Aria répond..."
        aiSubtitle = "Je remarque les formes et les couleurs dans votre champ de vision. Les contrastes sont bien définis."
        delay(4000)
        speechState = "À l'écoute..."
        aiSubtitle = "Posez-moi une question sur ce que vous pointez ou décrivez une idée."
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_voice_camera_modal"),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera Preview Stream
            if (isCameraEnabled) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = if (isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                // Camera preview fallback
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Futuristic Holographic Audio Visualizer Canvas when camera is disabled
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF1E1035),
                                    Color(0xFF0A0A10),
                                    Color.Black
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(waveScale1)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(waveScale2)
                            .clip(CircleShape)
                            .background(PurpleAccent.copy(alpha = 0.25f))
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // Dark gradient overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Header: Live Badge & Switch Camera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isMicMuted) PinkAccent else CyanAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE ARIA • $speechState",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                }

                if (isCameraEnabled) {
                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlipCameraAndroid,
                            contentDescription = "Basculer la caméra",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Real-time Subtitle Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp, start = 20.dp, end = 20.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = aiSubtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextWhite,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Bottom Floating Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera Toggle
                ControlButton(
                    icon = if (isCameraEnabled) Icons.Filled.Videocam else Icons.Filled.VideocamOff,
                    isActive = isCameraEnabled,
                    onClick = { isCameraEnabled = !isCameraEnabled },
                    contentDescription = "Caméra"
                )

                // Mic Toggle
                ControlButton(
                    icon = if (!isMicMuted) Icons.Filled.Mic else Icons.Filled.MicOff,
                    isActive = !isMicMuted,
                    onClick = { isMicMuted = !isMicMuted },
                    contentDescription = "Microphone"
                )

                // Large Red "Arrêter" End Session Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(28.dp))
                        .background(PinkAccent)
                        .clickable { onClose() }
                        .padding(horizontal = 22.dp, vertical = 14.dp)
                        .testTag("btn_end_live_session"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CallEnd,
                            contentDescription = "Arrêter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Arrêter",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                // Speaker Toggle
                ControlButton(
                    icon = if (!isSpeakerMuted) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    isActive = !isSpeakerMuted,
                    onClick = { isSpeakerMuted = !isSpeakerMuted },
                    contentDescription = "Haut-parleur"
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isActive) Color.White.copy(alpha = 0.2f) else PinkAccent.copy(alpha = 0.3f))
            .border(1.dp, if (isActive) Color.White.copy(alpha = 0.3f) else PinkAccent, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) Color.White else PinkAccent,
            modifier = Modifier.size(24.dp)
        )
    }
}
