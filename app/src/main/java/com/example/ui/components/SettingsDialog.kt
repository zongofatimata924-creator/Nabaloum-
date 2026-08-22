package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.AiSaasConstants
import com.example.data.repository.UserSettings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGoldEnd
import com.example.ui.theme.PremiumGoldStart
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun SettingsDialog(
    userSettings: UserSettings,
    onAiNameChange: (String) -> Unit = {},
    onModelChange: (String) -> Unit = {},
    onVoiceChange: (String) -> Unit = {},
    onCurrencyChange: (String) -> Unit = {},
    onCustomInstructionsChange: (String) -> Unit = {},
    onOpenSubscriptionDialog: () -> Unit = {},
    onOpenOtpDialog: () -> Unit = {},
    onToggleWebSearch: () -> Unit = {},
    onToggleGoogleDrive: () -> Unit = {},
    onToggleGithub: () -> Unit = {},
    onToggleNotion: () -> Unit = {},
    onClearHistory: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var aiNameText by remember { mutableStateOf(userSettings.aiName) }
    var instructionsText by remember { mutableStateOf(userSettings.customInstructions) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .testTag("settings_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Paramètres & Personnalité",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Fermer", tint = TextMutedGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Compte & Abonnement SaaS
                SectionTitle(title = "Compte & Abonnement", icon = Icons.Filled.AccountCircle, iconColor = IndigoAccent)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userSettings.userName.take(2).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userSettings.userName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = userSettings.userEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMutedGray
                                )
                            }

                            Button(
                                onClick = onOpenSubscriptionDialog,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (userSettings.isSubscribed) EmeraldAccent.copy(alpha = 0.2f) else IndigoAccent,
                                    contentColor = if (userSettings.isSubscribed) EmeraldAccent else Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (userSettings.isSubscribed) "PRO ILLIMITÉ" else "UPGRADE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick actions: OTP Login & Currency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .clickable { onOpenOtpDialog() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.VpnKey, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Connexion OTP", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextLightGray))
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .clickable { onOpenSubscriptionDialog() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Devise : ${userSettings.preferredCurrency.uppercase()}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = AmberAccent, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: Personnalité de l'IA (Nom + Modèles)
                SectionTitle(title = "Personnalité de l'IA (Aria)", icon = Icons.Filled.Psychology, iconColor = PurpleAccent)

                // Nom de l'IA
                Text(
                    text = "Nom de l'IA personnalisée",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = TextLightGray),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = aiNameText,
                    onValueChange = {
                        aiNameText = it
                        onAiNameChange(it)
                    },
                    singleLine = true,
                    placeholder = { Text("Aria", color = TextDarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_ai_name"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5 Models Persona
                Text(
                    text = "Modèle de comportement",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = TextLightGray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AiSaasConstants.AI_MODELS.forEach { model ->
                        val isSelected = userSettings.preferredModel == model.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) PurpleAccent else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onModelChange(model.id) },
                            color = if (isSelected) PurpleAccent.copy(alpha = 0.18f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = model.label,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PurpleAccent else TextWhite
                                        )
                                    )
                                    Text(
                                        text = model.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextMutedGray
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = PurpleAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Instructions
                Text(
                    text = "Instructions personnalisées",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = TextLightGray),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = instructionsText,
                    onValueChange = {
                        instructionsText = it
                        onCustomInstructionsChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: 5 Voice Styles
                SectionTitle(title = "Synthèse Vocale & Mode Direct", icon = Icons.Filled.RecordVoiceOver, iconColor = AmberAccent)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AiSaasConstants.VOICE_STYLES.forEach { voice ->
                        val isSelected = userSettings.preferredVoice == voice.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) AmberAccent else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onVoiceChange(voice.id) },
                            color = if (isSelected) AmberAccent.copy(alpha = 0.15f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = voice.label,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) AmberAccent else TextWhite
                                        )
                                    )
                                    Text(
                                        text = voice.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextMutedGray
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 4: Sécurité & Chiffrement Fernet
                SectionTitle(title = "Sécurité & Chiffrement", icon = Icons.Filled.Security, iconColor = EmeraldAccent)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Chiffrement AES-128 / Fernet Activé",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Données confidentielles et clés protégées de bout en bout",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextMutedGray)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 5: Connecteurs & Outils
                SectionTitle(title = "Connecteurs & Outils", icon = Icons.Filled.Extension, iconColor = CyanAccent)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    ConnectorRow("Recherche Web en direct", "Recherche Google temps réel", userSettings.webSearchEnabled, onToggleWebSearch)
                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                    ConnectorRow("Google Drive", "Accès aux documents et fiches", userSettings.googleDriveConnected, onToggleGoogleDrive)
                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                    ConnectorRow("GitHub", "Analyse de repositories & code", userSettings.githubConnected, onToggleGithub)
                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                    ConnectorRow("Notion", "Synchronisation des pages et wikis", userSettings.notionConnected, onToggleNotion)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Effacer l'historique
                Button(
                    onClick = onClearHistory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkAccent.copy(alpha = 0.15f),
                        contentColor = PinkAccent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Effacer tout l'historique des discussions")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aria SaaS Engine • v2.5.0",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextDarkGray
                    )
                    Row(
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null, tint = TextMutedGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fermer",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMutedGray, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        )
    }
}

@Composable
private fun ConnectorRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold), color = TextWhite)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = TextMutedGray)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanAccent,
                checkedTrackColor = CyanAccent.copy(alpha = 0.3f),
                uncheckedThumbColor = TextDarkGray,
                uncheckedTrackColor = DarkSurface
            )
        )
    }
}
