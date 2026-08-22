package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.repository.AiSaasConstants
import com.example.data.repository.GenerationJob
import com.example.ui.ChatViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

data class ImagineTemplate(
    val id: String,
    val title: String,
    val description: String,
    val promptTemplate: String,
    val drawableRes: Int,
    val drawableName: String
)

@Composable
fun ImagineScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isVideo by viewModel.imagineIsVideo.collectAsState()
    val selectedImageModel by viewModel.selectedImageModel.collectAsState()
    val selectedVideoModel by viewModel.selectedVideoModel.collectAsState()
    val prompt by viewModel.imaginePrompt.collectAsState()
    val aspectRatio by viewModel.imagineAspectRatio.collectAsState()
    val quantity by viewModel.selectedQuantity.collectAsState()
    val duration by viewModel.selectedDuration.collectAsState()
    val stylePreset by viewModel.imagineStylePreset.collectAsState()
    val saveAsDefault by viewModel.saveAsDefault.collectAsState()
    val isGenerating by viewModel.isGeneratingImagine.collectAsState()
    val activeJobs by viewModel.activeJobs.collectAsState()
    val creations by viewModel.generatedImages.collectAsState()
    val attachedBitmap by viewModel.attachedBitmap.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    val currentModelLabel = if (isVideo) {
        AiSaasConstants.VIDEO_MODELS[selectedVideoModel]?.label ?: "Omni Flash"
    } else {
        AiSaasConstants.IMAGE_MODELS[selectedImageModel]?.label ?: "Nano Banana 2"
    }

    val currentCreditsCost = if (isVideo) {
        val base = AiSaasConstants.VIDEO_MODELS[selectedVideoModel]?.creditsPerVideo ?: 12
        base * quantity
    } else {
        0
    }

    val templates = remember {
        listOf(
            ImagineTemplate(
                id = "studio_prod",
                title = "Studio Produit",
                description = "Éclairage studio 8k commercial",
                promptTemplate = "Photo produit professionnelle en studio, fond épuré, éclairage doux 8k, photoréaliste",
                drawableRes = R.drawable.template_product_studio_1787301913290,
                drawableName = "template_product_studio_1787301913290"
            ),
            ImagineTemplate(
                id = "chibi",
                title = "Chibi 3D",
                description = "Figurine mignonne Pixar/Clay",
                promptTemplate = "Personnage 3D miniature chibi mignon style Pixar Disney, argile lisse, rendu octane raytracing",
                drawableRes = R.drawable.template_chibi_3d_1787301926049,
                drawableName = "template_chibi_3d_1787301926049"
            ),
            ImagineTemplate(
                id = "cyberpunk",
                title = "Cyberpunk Néon",
                description = "Futuriste lumières néon pluie",
                promptTemplate = "Ville cyberpunk dystopique sous la pluie nocturne, enseignes néon violettes et cyan, reflets au sol",
                drawableRes = R.drawable.template_cyberpunk_neon_1787301938998,
                drawableName = "template_cyberpunk_neon_1787301938998"
            ),
            ImagineTemplate(
                id = "cinematic",
                title = "Cinématique Art",
                description = "Plan large dramatique 35mm",
                promptTemplate = "Scène cinématographique épique, cadrage anamorphique 35mm, éclairage volumétrique dramatique",
                drawableRes = R.drawable.template_cinematic_art_1787301949960,
                drawableName = "template_cinematic_art_1787301949960"
            )
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                viewModel.setAttachedImage(bitmap, uri.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("imagine_screen")
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // Mode Switcher: Image vs Video
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        ModeToggleButton(
                            title = "Image",
                            icon = Icons.Filled.Image,
                            isSelected = !isVideo,
                            onClick = { viewModel.setImagineIsVideo(false) },
                            modifier = Modifier.testTag("toggle_mode_image")
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        ModeToggleButton(
                            title = "Vidéo IA",
                            icon = Icons.Filled.Movie,
                            isSelected = isVideo,
                            badge = "Pro",
                            onClick = { viewModel.setImagineIsVideo(true) },
                            modifier = Modifier.testTag("toggle_mode_video")
                        )
                    }
                }
            }

            // AI Generation Engine Models Selector (ai_generation_engine.py v5.1.0)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isVideo) "MOTEURS VIDÉO" else "MOTEURS D'IMAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkGray,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )

                    // Credits badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (userSettings.isSubscribed) IndigoAccent.copy(alpha = 0.2f) else DarkSurfaceVariant)
                            .border(1.dp, if (userSettings.isSubscribed) IndigoAccent.copy(alpha = 0.4f) else DarkCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (userSettings.isSubscribed) "⚡ Pro Illimité (0 crédit)" else if (isVideo) "🪙 $currentCreditsCost crédits" else "✨ Gratuit (0 crédit)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userSettings.isSubscribed) IndigoLight else if (isVideo) AmberAccent else EmeraldAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Models horizontal chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    if (!isVideo) {
                        items(AiSaasConstants.IMAGE_MODELS.values.toList(), key = { it.id }) { modelInfo ->
                            val isSelected = selectedImageModel == modelInfo.id
                            ModelSelectorChip(
                                label = modelInfo.label,
                                subtitle = "${modelInfo.maxResolution} • ${modelInfo.quality}",
                                isSelected = isSelected,
                                onClick = { viewModel.setSelectedImageModel(modelInfo.id) },
                                tag = "chip_model_${modelInfo.id}"
                            )
                        }
                    } else {
                        items(AiSaasConstants.VIDEO_MODELS.values.toList(), key = { it.id }) { modelInfo ->
                            val isSelected = selectedVideoModel == modelInfo.id
                            ModelSelectorChip(
                                label = modelInfo.label,
                                subtitle = "${modelInfo.maxDuration}s max • ${modelInfo.creditsPerVideo} crédits",
                                isSelected = isSelected,
                                isPro = true,
                                onClick = { viewModel.setSelectedVideoModel(modelInfo.id) },
                                tag = "chip_model_${modelInfo.id}"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active / In-progress generation banner
            val latestActiveJob = activeJobs.firstOrNull { it.status == "processing" }
            if (latestActiveJob != null) {
                ActiveJobBanner(
                    job = latestActiveJob,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Templates Carousel
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TEMPLATES PRÉDÉFINIS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkGray,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${templates.size} styles",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BlueLight,
                            fontSize = 11.sp
                        )
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(templates, key = { it.id }) { item ->
                        TemplateCard(
                            template = item,
                            onClick = {
                                viewModel.setImaginePrompt(item.promptTemplate)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent Creations Grid Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CRÉATIONS RÉCENTES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkGray,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${creations.size} créations",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMutedGray,
                            fontSize = 11.sp
                        )
                    )
                }

                if (creations.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkCardBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = IndigoLight,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucune création pour l'instant",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                            )
                            Text(
                                text = "Décrivez une idée ci-dessous avec $currentModelLabel",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMutedGray,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(((creations.size + 1) / 2 * 180).coerceAtMost(540).dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        userScrollEnabled = false
                    ) {
                        items(creations, key = { it.id }) { imageEntity ->
                            CreationCard(
                                item = imageEntity,
                                onClick = { viewModel.openImageDetail(imageEntity) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Generation Parameters & Prompt Input Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("imagine_prompt_container"),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Parameters Row 1: Aspect Ratio & Quantité & Video Duration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aspect ratio chips (16:9, 9:16, 4:3, 1:1, 3:4)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ratio:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = TextDarkGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        AiSaasConstants.ASPECT_RATIOS.forEach { ratio ->
                            val isSelected = aspectRatio == ratio
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) IndigoAccent else DarkSurface)
                                    .border(1.dp, if (isSelected) Color.Transparent else DarkCardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setImagineAspectRatio(ratio) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = ratio,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextMutedGray
                                    )
                                )
                            }
                        }
                    }

                    // Quantities x1, x2, x3, x4
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AiSaasConstants.QUANTITIES.forEach { qty ->
                            val isSelected = quantity == qty
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) BlueAccent else DarkSurface)
                                    .border(1.dp, if (isSelected) Color.Transparent else DarkCardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setSelectedQuantity(qty) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "x$qty",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextMutedGray
                                    )
                                )
                            }
                        }
                    }
                }

                // Parameters Row 2: Video Duration (if video) or Style Presets + Save Default Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isVideo) {
                        val supportedDurations = AiSaasConstants.VIDEO_MODELS[selectedVideoModel]?.supportedDurations 
                            ?: listOf(4, 6, 8, 10)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Durée:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextDarkGray,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            supportedDurations.forEach { dur ->
                                val isSelected = duration == dur
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) AmberAccent.copy(alpha = 0.3f) else DarkSurface)
                                        .border(1.dp, if (isSelected) AmberAccent else DarkCardBorder, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.setSelectedDuration(dur) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${dur}s",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) AmberAccent else TextMutedGray
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        // Style Presets selector
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(AiSaasConstants.STYLE_PRESETS.take(4)) { style ->
                                val isSelected = stylePreset == style
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) IndigoAccent.copy(alpha = 0.25f) else DarkSurface)
                                        .border(1.dp, if (isSelected) IndigoLight else DarkCardBorder, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.setImagineStylePreset(style) }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = style,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = if (isSelected) IndigoLight else TextMutedGray
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Reference photo button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                            .clickable { photoPickerLauncher.launch("image/*") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddPhotoAlternate,
                            contentDescription = "Ajouter image source",
                            tint = BlueLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (attachedBitmap != null) "Source OK" else "Réf.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if (attachedBitmap != null) EmeraldAccent else TextMutedGray
                            )
                        )
                        if (attachedBitmap != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Supprimer",
                                tint = TextMutedGray,
                                modifier = Modifier
                                    .size(11.dp)
                                    .clickable { viewModel.clearAttachedImage() }
                            )
                        }
                    }
                }

                // Prompt Input & Generate Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.setImaginePrompt(it) },
                        placeholder = {
                            Text(
                                text = if (isVideo) "Décrivez la vidéo ($currentModelLabel)..." else "Décrivez l'image ($currentModelLabel)...",
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
                            unfocusedTextColor = TextWhite,
                            cursorColor = IndigoLight
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("input_imagine_prompt"),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Big Action Button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isGenerating && prompt.isNotBlank()) {
                                viewModel.generateImagineContent(
                                    prompt = prompt,
                                    aspectRatio = aspectRatio,
                                    stylePreset = stylePreset,
                                    isVideo = isVideo
                                )
                            }
                            .testTag("btn_imagine_generate"),
                        color = if (prompt.isNotBlank()) IndigoAccent else DarkSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            if (prompt.isNotBlank()) Color.Transparent else DarkCardBorder
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isVideo) Icons.Filled.Movie else Icons.Filled.AutoAwesome,
                                    contentDescription = "Générer",
                                    tint = if (prompt.isNotBlank()) Color.White else TextDarkGray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelSelectorChip(
    label: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPro: Boolean = false,
    tag: String? = null
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isSelected) IndigoAccent else DarkCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .then(if (tag != null) Modifier.testTag(tag) else Modifier),
        color = if (isSelected) IndigoAccent.copy(alpha = 0.18f) else DarkSurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) IndigoLight else TextDarkGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                    if (isPro) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberAccent
                                )
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMutedGray,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ActiveJobBanner(
    job: GenerationJob,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, IndigoAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassTop,
                        contentDescription = null,
                        tint = IndigoLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Génération ${job.modelLabel} en cours...",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            fontSize = 12.sp
                        )
                    )
                }
                Text(
                    text = "~${job.estimatedTimeSeconds}s",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMutedGray,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = IndigoLight,
                trackColor = DarkCardBorder
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Job ID: ${job.id.take(8)}... • ${job.prompt}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMutedGray,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModeToggleButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) IndigoAccent else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else TextMutedGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextMutedGray
                )
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: ImagineTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Image(
                    painter = painterResource(id = template.drawableRes),
                    contentDescription = template.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, DarkSurfaceVariant.copy(alpha = 0.8f))
                            )
                        )
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    ),
                    maxLines = 1
                )
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextMutedGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CreationCard(
    item: GeneratedImageEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resolvedDrawableRes = remember(item.drawableResName) {
        item.drawableResName?.let { name ->
            val id = context.resources.getIdentifier(name, "drawable", context.packageName)
            if (id != 0) id else null
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item.imageUri != null) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = item.prompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (resolvedDrawableRes != null) {
                Image(
                    painter = painterResource(id = resolvedDrawableRes),
                    contentDescription = item.prompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = TextDarkGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Video indicator badge
            if (item.isVideo) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Vidéo",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Bottom title overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = item.prompt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextWhite
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
