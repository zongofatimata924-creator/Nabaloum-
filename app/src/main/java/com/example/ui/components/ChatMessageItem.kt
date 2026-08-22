package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.MessageEntity
import com.example.ui.theme.AiBubbleBg
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.CodeBlockBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.ThinkingBg
import com.example.ui.theme.UserBubbleBg

@Composable
fun ChatMessageItem(
    message: MessageEntity,
    onRegenerate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUser = message.role == "user"
    var showThinking by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(if (isUser) "msg_user_${message.id}" else "msg_ai_${message.id}"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // AI Avatar
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                IndigoAccent,
                                PurpleAccent,
                                BlueAccent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Token,
                    contentDescription = "Nexus AI",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Message Content Box
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 340.dp)
        ) {
            // Attached Image if available
            if (message.imageUri != null) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp)),
                    color = DarkSurfaceVariant
                ) {
                    AsyncImage(
                        model = message.imageUri,
                        contentDescription = "Image analysée",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                }
            }

            // Thinking Process Box for Thinking mode
            if (!isUser && message.thinkingProcess != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, PurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                    color = ThinkingBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showThinking = !showThinking },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
                                    text = "Raisonnement (Gemini 3.1 Pro)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = PurpleAccent
                                    )
                                )
                            }
                            Icon(
                                imageVector = if (showThinking) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = TextMutedGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = showThinking,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Text(
                                text = message.thinkingProcess,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                ),
                                color = TextMutedGray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Main Message Bubble
            Surface(
                color = if (isUser) UserBubbleBg else AiBubbleBg,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (!isUser && message.content.isBlank()) {
                        ShimmerTextParagraph(lineCount = 3, lineHeight = 13.dp, lineSpacing = 8.dp)
                    } else {
                        // Render parsed Markdown blocks
                        RenderRichContent(content = message.content, isUser = isUser)
                    }
                }
            }

            // Assistant Action & Telemetry Bar
            if (!isUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left action buttons: Copy, TTS, Regenerate
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Nexus AI", message.content)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copié dans le presse-papiers", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copier",
                                tint = TextDarkGray,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                isSpeaking = !isSpeaking
                                Toast.makeText(
                                    context,
                                    if (isSpeaking) "Lecture vocale (Voix Kore)..." else "Lecture arrêtée",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Écouter",
                                tint = if (isSpeaking) BlueLight else TextDarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onRegenerate(message.content) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Régénérer",
                                tint = TextDarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Right metadata: Tokens & Latency
                    if (message.tokenCount > 0 || message.latencyMs > 0) {
                        Text(
                            text = "${message.tokenCount} tokens • ${message.latencyMs}ms",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = TextDarkGray
                            )
                        )
                    }
                }
            }
        }

        // User Avatar
        if (isUser) {
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Utilisateur",
                    tint = TextWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun RenderRichContent(content: String, isUser: Boolean) {
    val context = LocalContext.current
    val parts = content.split("```")

    for (i in parts.indices) {
        val part = parts[i]
        if (i % 2 == 1) {
            // Code block
            val lines = part.trim().lines()
            val language = if (lines.firstOrNull()?.contains(" ") == false && lines.first().length < 15) {
                lines.first()
            } else "code"
            val codeBody = if (language != "code" && lines.size > 1) {
                lines.drop(1).joinToString("\n")
            } else part.trim()

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                color = CodeBlockBg
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BlueLight,
                                fontSize = 11.sp
                            )
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Code", codeBody))
                                    Toast.makeText(context, "Code copié !", Toast.LENGTH_SHORT).show()
                                }
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copier le code",
                                tint = TextMutedGray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Copier",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMutedGray,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = codeBody,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            // Standard Text / Markdown formatting
            if (part.isNotBlank()) {
                val annotatedString = buildAnnotatedString {
                    val segments = part.split("**")
                    for (j in segments.indices) {
                        if (j % 2 == 1) {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) TextWhite else Color.White
                                )
                            ) {
                                append(segments[j])
                            }
                        } else {
                            withStyle(
                                style = SpanStyle(
                                    color = if (isUser) TextWhite else TextLightGray
                                )
                            ) {
                                append(segments[j])
                            }
                        }
                    }
                }

                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                )
            }
        }
    }
}

