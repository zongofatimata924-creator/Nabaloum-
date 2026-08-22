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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun AriaActionMenu(
    onSelectAction: (actionType: String, prompt: String) -> Unit,
    onOpenImagineTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAction by remember { mutableStateOf<String?>(null) }
    var actionInput by remember { mutableStateOf("") }

    val actionPlaceholder = when (selectedAction) {
        "image" -> "Décrivez l'image que vous voulez créer…"
        "write" -> "Que voulez-vous écrire ou modifier ?"
        "search" -> "Que voulez-vous rechercher sur le Web ?"
        else -> "Demander à ARIA…"
    }

    // Floating pulse animation for the top logo badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sparkle")
    val sparkleGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("aria_action_menu"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Center Title & Animated Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(IndigoAccent.copy(alpha = sparkleGlowAlpha * 0.4f))
                .border(1.dp, IndigoAccent.copy(alpha = sparkleGlowAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Aria Sparkle",
                tint = IndigoLight,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ARIA AI",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = TextWhite
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Intelligence artificielle directe, précise et polyvalente.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = TextMutedGray
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        // Action Menu Buttons (3 cartes avec animations dédiées)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AriaAnimatedActionButton(
                icon = Icons.Filled.Image,
                iconColor = PurpleAccent,
                label = "Créer une image",
                description = "Génération visuelle & modèles photoréalistes",
                isSelected = selectedAction == "image",
                animationType = IconAnimationType.FLOAT,
                onClick = {
                    selectedAction = "image"
                    actionInput = ""
                },
                tag = "btn_aria_create_image"
            )

            AriaAnimatedActionButton(
                icon = Icons.Filled.Edit,
                iconColor = BlueAccent,
                label = "Écrire ou modifier",
                description = "Rédaction, synthèse, correction & code",
                isSelected = selectedAction == "write",
                animationType = IconAnimationType.PULSE_GLOW,
                onClick = {
                    selectedAction = "write"
                    actionInput = ""
                },
                tag = "btn_aria_write_edit"
            )

            AriaAnimatedActionButton(
                icon = Icons.Filled.Language,
                iconColor = PinkAccent,
                label = "Rechercher sur le Web",
                description = "Informations en direct et actualités mondiales",
                isSelected = selectedAction == "search",
                animationType = IconAnimationType.BOUNCE,
                onClick = {
                    selectedAction = "search"
                    actionInput = ""
                },
                tag = "btn_aria_web_search"
            )
        }

        // Animated Input Zone upon Action Selection
        AnimatedVisibility(
            visible = selectedAction != null,
            enter = fadeIn(tween(250)) + expandVertically(tween(300)),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(250))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, IndigoAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (selectedAction) {
                                "image" -> "CRÉATION D'IMAGE"
                                "write" -> "RÉDACTION / MODIFICATION"
                                "search" -> "RECHERCHE WEB"
                                else -> "ACTION ARIA"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IndigoAccent,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            )
                        )

                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Annuler",
                            tint = TextMutedGray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { selectedAction = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = actionInput,
                        onValueChange = { actionInput = it },
                        placeholder = {
                            Text(
                                text = actionPlaceholder,
                                color = TextDarkGray,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg,
                            focusedBorderColor = IndigoAccent,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("aria_action_text_input"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedAction = null },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Annuler", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val action = selectedAction ?: return@Button
                                val prompt = actionInput.trim()
                                if (prompt.isNotBlank()) {
                                    if (action == "image") {
                                        onSelectAction("image", prompt)
                                    } else if (action == "write") {
                                        onSelectAction("write", "Écris et optimise le texte suivant de façon claire, nette et structurée : \n\n$prompt")
                                    } else {
                                        onSelectAction("search", "Recherche web et synthèse en direct sur le sujet suivant : $prompt")
                                    }
                                    selectedAction = null
                                }
                            },
                            enabled = actionInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IndigoAccent,
                                disabledContainerColor = DarkSurface
                            ),
                            modifier = Modifier
                                .weight(2f)
                                .testTag("btn_aria_send_action")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Envoyer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

enum class IconAnimationType {
    FLOAT,
    PULSE_GLOW,
    BOUNCE
}

@Composable
private fun AriaAnimatedActionButton(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    description: String,
    isSelected: Boolean,
    animationType: IconAnimationType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_anim_${label}")

    // Dynamic animations according to CSS specs:
    // .icon-image -> float (translateY -4dp)
    // .icon-write -> pulse-glow / scale
    // .icon-search -> bounce (translateY -6dp)
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_offset"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isSelected) IndigoAccent else DarkCardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .then(if (tag != null) Modifier.testTag(tag) else Modifier),
        color = if (isSelected) IndigoAccent.copy(alpha = 0.15f) else DarkSurfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.14f))
                    .border(1.dp, iconColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                val iconModifier = when (animationType) {
                    IconAnimationType.FLOAT -> Modifier.offset(y = floatOffset.dp)
                    IconAnimationType.PULSE_GLOW -> Modifier.scale(pulseScale)
                    IconAnimationType.BOUNCE -> Modifier.offset(y = bounceOffset.dp)
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = iconModifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextMutedGray
                    )
                )
            }
        }
    }
}
