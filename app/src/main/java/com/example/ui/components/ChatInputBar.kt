package com.example.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isSending: Boolean,
    modelMode: String,
    onModelModeChange: (String) -> Unit,
    attachedBitmap: Bitmap?,
    onImageSelected: (Bitmap?, String?) -> Unit,
    onClearAttachedImage: () -> Unit,
    onOpenLiveVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showModeDropdown by remember { mutableStateOf(false) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var isDictating by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                onImageSelected(bitmap, it.toString())
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            onImageSelected(it, null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("chat_input_bar"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Attached Image Preview
        AnimatedVisibility(
            visible = attachedBitmap != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            attachedBitmap?.let { bitmap ->
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Image jointe",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BlueAccent, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.85f))
                            .clickable { onClearAttachedImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Retirer image",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // 1. Quick Selector Pills: "Rapide" & "Brainstorming"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Selector Pill (Dropdown)
            Box {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, CircleShape)
                        .clickable { showModeDropdown = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("pill_model_mode"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (modelMode == "thinking") Icons.Filled.Psychology else Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = if (modelMode == "thinking") PurpleAccent else BlueLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (modelMode == "thinking") "Réfléchi" else "Rapide",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = TextWhite.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }

                DropdownMenu(
                    expanded = showModeDropdown,
                    onDismissRequest = { showModeDropdown = false },
                    modifier = Modifier
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Rapide (Gemini 3.5 Flash)", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Réponses instantanées pour les requêtes quotidiennes", color = TextMutedGray, fontSize = 11.sp)
                            }
                        },
                        leadingIcon = { Icon(Icons.Filled.Bolt, contentDescription = null, tint = BlueLight) },
                        onClick = {
                            onModelModeChange("fast")
                            showModeDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Réfléchi (Gemini 3.1 Pro)", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Raisonnement avancé, analyse de code & logique", color = TextMutedGray, fontSize = 11.sp)
                            }
                        },
                        leadingIcon = { Icon(Icons.Filled.Psychology, contentDescription = null, tint = PurpleAccent) },
                        onClick = {
                            onModelModeChange("thinking")
                            showModeDropdown = false
                        }
                    )
                }
            }

            // Brainstorming Pill
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkCardBorder, CircleShape)
                    .clickable {
                        onInputTextChange("Donne-moi 5 idées créatives et innovantes pour ")
                    }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("pill_brainstorming"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Brainstorming",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                )
            }
        }

        // 2. Input Box (bg-[#1a1a1a] rounded-[24px] border border-white/10 p-2)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Attach button (+)
                Box {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White.copy(alpha = 0.1f))
                            ) { showAttachMenu = true }
                            .testTag("btn_attach"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Joindre un fichier",
                            tint = TextWhite.copy(alpha = 0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showAttachMenu,
                        onDismissRequest = { showAttachMenu = false },
                        modifier = Modifier
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Prendre une photo", color = TextWhite, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = BlueLight) },
                            onClick = {
                                showAttachMenu = false
                                cameraLauncher.launch(null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Galerie d'images", color = TextWhite, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null, tint = PurpleAccent) },
                            onClick = {
                                showAttachMenu = false
                                imagePickerLauncher.launch("image/*")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Text Input Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp, max = 120.dp)
                        .padding(horizontal = 4.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty() && !isDictating) {
                        Text(
                            text = "Message...",
                            style = TextStyle(
                                color = TextDarkGray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }

                    if (isDictating) {
                        Text(
                            text = "Écoute en cours...",
                            style = TextStyle(
                                color = AmberAccent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    } else {
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputTextChange,
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 15.sp,
                                lineHeight = 21.sp
                            ),
                            cursorBrush = SolidColor(Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_text_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Voice dictation mic button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White.copy(alpha = 0.1f))
                        ) {
                            isDictating = !isDictating
                            if (isDictating) {
                                onInputTextChange(if (inputText.isEmpty()) "Explique-moi les concepts clés de l'IA" else "$inputText (dicté)")
                                isDictating = false
                            }
                        }
                        .testTag("btn_mic_dictation"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Dictée vocale",
                        tint = if (isDictating) AmberAccent else TextWhite.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3. Action Buttons Row: White "Parler" Button & Indigo Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // White "Parler" Voice Mode Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.Black.copy(alpha = 0.1f))
                    ) { onOpenLiveVoice() }
                    .testTag("btn_live_voice"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardVoice,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Parler",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            // Indigo Send Button (w-14 h-14 bg-indigo-600 text-white rounded-2xl)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = IndigoAccent,
                        spotColor = IndigoAccent
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (inputText.isNotBlank() || attachedBitmap != null) IndigoAccent else IndigoAccent.copy(alpha = 0.7f))
                    .clickable(
                        enabled = (inputText.isNotBlank() || attachedBitmap != null) && !isSending,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(alpha = 0.2f))
                    ) { onSendMessage() }
                    .testTag("btn_send_message"),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = "Envoyer",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

