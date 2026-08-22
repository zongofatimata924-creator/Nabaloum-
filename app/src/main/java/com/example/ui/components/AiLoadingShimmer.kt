package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiBubbleBg
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderFocus
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.ThinkingBg

/**
 * Creates an animated linear shimmer brush tailored for dark AI theme.
 * The brush sweeps diagonally across UI elements creating an elegant glowing shimmer.
 */
@Composable
fun rememberShimmerBrush(
    durationMillis: Int = 1300,
    shimmerColors: List<Color> = listOf(
        Color(0xFF1E212E),
        Color(0xFF2B3145),
        Color(0xFF4F46E5).copy(alpha = 0.38f),
        Color(0xFF818CF8).copy(alpha = 0.45f),
        Color(0xFF2B3145),
        Color(0xFF1E212E)
    )
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 350f, y = translateAnim - 350f),
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/**
 * Custom modifier extension to add loading shimmer effect to any composable.
 */
fun Modifier.shimmerLoading(
    shape: Shape = RoundedCornerShape(8.dp),
    durationMillis: Int = 1300
): Modifier = composed {
    val brush = rememberShimmerBrush(durationMillis = durationMillis)
    this
        .clip(shape)
        .background(brush)
}

/**
 * A single skeleton line representing shimmering text while loading.
 */
@Composable
fun ShimmerTextLine(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    height: Dp = 14.dp,
    shape: Shape = RoundedCornerShape(6.dp),
    brush: Brush = rememberShimmerBrush()
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(shape)
            .background(brush)
    )
}

/**
 * Multi-line paragraph skeleton with staggered widths simulating dynamic AI text generation.
 */
@Composable
fun ShimmerTextParagraph(
    modifier: Modifier = Modifier,
    lineCount: Int = 3,
    lineHeight: Dp = 14.dp,
    lineSpacing: Dp = 8.dp,
    brush: Brush = rememberShimmerBrush()
) {
    val widthFractions = when (lineCount) {
        1 -> listOf(0.85f)
        2 -> listOf(0.95f, 0.65f)
        3 -> listOf(0.96f, 0.88f, 0.58f)
        4 -> listOf(0.98f, 0.90f, 0.82f, 0.52f)
        else -> listOf(0.98f, 0.92f, 0.88f, 0.74f, 0.45f)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(lineSpacing)
    ) {
        widthFractions.forEach { fraction ->
            ShimmerTextLine(
                widthFraction = fraction,
                height = lineHeight,
                brush = brush
            )
        }
    }
}

/**
 * Reusable text component with built-in loading shimmer state.
 */
@Composable
fun ShimmerText(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    color: Color = TextWhite,
    fontSize: Dp = 14.dp,
    placeholderWidthFraction: Float = 0.75f,
    placeholderHeight: Dp = 16.dp
) {
    if (isLoading) {
        ShimmerTextLine(
            modifier = modifier,
            widthFraction = placeholderWidthFraction,
            height = placeholderHeight
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Dedicated shimmer container for the reasoning process when Gemini 3.1 Pro Thinking mode is active.
 */
@Composable
fun ThinkingShimmerBox(
    modifier: Modifier = Modifier,
    shimmerBrush: Brush = rememberShimmerBrush(
        shimmerColors = listOf(
            Color(0xFF231C38),
            Color(0xFF382B57),
            PurpleAccent.copy(alpha = 0.35f),
            Color(0xFF382B57),
            Color(0xFF231C38)
        )
    )
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, PurpleAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        color = ThinkingBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Raisonnement en cours...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PurpleAccent
                        )
                    )
                }

                // Pulsing badge
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerTextLine(widthFraction = 0.92f, height = 10.dp, brush = shimmerBrush)
                ShimmerTextLine(widthFraction = 0.80f, height = 10.dp, brush = shimmerBrush)
                ShimmerTextLine(widthFraction = 0.60f, height = 10.dp, brush = shimmerBrush)
            }
        }
    }
}

/**
 * Full AI Loading message item that displays during AI response generation in the chat stream.
 * Features an avatar with pulsing halo, shimmering status badges, and multi-line skeleton text.
 */
@Composable
fun AiLoadingMessageItem(
    modelMode: String = "fast",
    aiName: String = "ARIA",
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberShimmerBrush()
    val isThinking = modelMode == "thinking"

    // Infinite breathing glow for avatar
    val glowTransition = rememberInfiniteTransition(label = "glow_transition")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_glow"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("ai_loading_shimmer_item"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // AI Avatar with pulsating aura
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            IndigoAccent.copy(alpha = glowAlpha),
                            PurpleAccent.copy(alpha = glowAlpha),
                            BlueAccent.copy(alpha = glowAlpha)
                        )
                    )
                )
                .border(
                    1.dp,
                    BlueLight.copy(alpha = glowAlpha * 0.7f),
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isThinking) Icons.Filled.Psychology else Icons.Filled.AutoAwesome,
                contentDescription = "Génération IA",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Shimmer Bubble Content
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 340.dp)
        ) {
            // Thinking Mode Shimmer Box (if thinking mode active)
            if (isThinking) {
                ThinkingShimmerBox(
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Main Message Shimmer Bubble
            Surface(
                color = AiBubbleBg,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 18.dp
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorderFocus.copy(alpha = glowAlpha * 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Header with animated model indicator and generating status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isThinking) PurpleAccent else IndigoLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isThinking) "$aiName • Gemini 3.1 Pro" else "$aiName • Gemini 3.5 Flash",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isThinking) PurpleAccent else BlueLight
                                )
                            )
                        }

                        // Shimmering generation pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(BlueAccent.copy(alpha = glowAlpha))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isThinking) "Raisonnement..." else "Génération...",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = TextMutedGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // Multi-line Shimmering Text Skeleton Lines
                    ShimmerTextParagraph(
                        lineCount = if (isThinking) 4 else 3,
                        lineHeight = 13.dp,
                        lineSpacing = 9.dp,
                        brush = shimmerBrush
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary shorter text line for natural paragraph feel
                    ShimmerTextLine(
                        widthFraction = 0.42f,
                        height = 11.dp,
                        brush = shimmerBrush
                    )
                }
            }
        }
    }
}
