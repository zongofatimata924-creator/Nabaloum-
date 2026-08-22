package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AutomationEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.repository.UserSettings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGoldEnd
import com.example.ui.theme.PremiumGoldStart
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextDarkGray
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppDrawerContent(
    userSettings: UserSettings,
    conversations: List<ConversationEntity>,
    selectedConversationId: String?,
    projects: List<ProjectEntity>,
    automations: List<AutomationEntity>,
    searchQuery: String,
    selectedProjectFilter: String?,
    onSearchQueryChange: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    onNewConversation: () -> Unit,
    onTogglePin: (ConversationEntity) -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onFilterByProject: (String?) -> Unit,
    onOpenAutomations: () -> Unit,
    onOpenProjects: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var renameDialogTarget by remember { mutableStateOf<ConversationEntity?>(null) }
    var renameText by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .testTag("app_drawer_content"),
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 16.dp, bottom = 12.dp, start = 14.dp, end = 14.dp)
        ) {
            // User Profile Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .clickable { onOpenSettings() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar with gradient border
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent)))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userSettings.userName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userSettings.userName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (userSettings.isPro) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.horizontalGradient(listOf(PremiumGoldStart, PremiumGoldEnd)))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = userSettings.userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(32.dp).testTag("drawer_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Paramètres",
                        tint = TextMutedGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // New Conversation Action Button
            Button(
                onClick = {
                    onNewConversation()
                    onCloseDrawer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("drawer_new_chat_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nouvelle discussion",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("drawer_search_input"),
                placeholder = {
                    Text("Rechercher...", style = MaterialTheme.typography.bodyMedium, color = TextDarkGray)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextDarkGray, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Effacer", tint = TextDarkGray, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Sections: Automations, Projects, Grouped Conversations
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section: Automatisations
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Automatisations",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextMutedGray
                            )
                        }
                        Text(
                            text = "Voir tout",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clickable { onOpenAutomations() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        automations.take(2).forEach { auto ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceVariant)
                                    .clickable { onOpenAutomations() }
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = auto.title,
                                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                        color = TextWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = auto.category,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = AmberAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Projets
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null,
                                tint = IndigoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Projets",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextMutedGray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedProjectFilter != null) {
                                Text(
                                    text = "Tous",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyanAccent),
                                    modifier = Modifier
                                        .clickable { onFilterByProject(null) }
                                        .padding(end = 8.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.CreateNewFolder,
                                contentDescription = "Nouveau projet",
                                tint = TextMutedGray,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onOpenProjects() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        projects.forEach { proj ->
                            val isSelected = selectedProjectFilter == proj.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) IndigoAccent.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                    .clickable {
                                        onFilterByProject(if (isSelected) null else proj.id)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(proj.colorHex)))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = proj.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) CyanAccent else TextLightGray,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Section: Premium Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E1435), Color(0xFF0F172A))
                                )
                            )
                            .clickable { onOpenSettings() }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(PurpleAccent, PinkAccent))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nexus Ultra",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextWhite
                                )
                                Text(
                                    text = "Modèles illimités & Vision 4K",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMutedGray
                                )
                            }
                        }
                    }
                }

                // Section: Discussions récentes groupées par date
                item {
                    Text(
                        text = "Discussions récentes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextMutedGray
                    )
                }

                if (conversations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucune discussion trouvée",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDarkGray
                            )
                        }
                    }
                } else {
                    items(conversations, key = { it.id }) { conv ->
                        val isSelected = conv.id == selectedConversationId
                        var showMenu by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    onSelectConversation(conv.id)
                                    onCloseDrawer()
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (conv.isPinned) Icons.Filled.PushPin else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = if (conv.isPinned) AmberAccent else if (isSelected) CyanAccent else TextDarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = conv.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isSelected) TextWhite else TextLightGray,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Options",
                                        tint = TextDarkGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.background(DarkSurfaceVariant)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(if (conv.isPinned) "Détacher" else "Épingler", color = TextWhite) },
                                        leadingIcon = {
                                            Icon(
                                                if (conv.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
                                                contentDescription = null,
                                                tint = AmberAccent
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onTogglePin(conv)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Renommer", color = TextWhite) },
                                        leadingIcon = {
                                            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanAccent)
                                        },
                                        onClick = {
                                            showMenu = false
                                            renameDialogTarget = conv
                                            renameText = conv.title
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Supprimer", color = PinkAccent) },
                                        leadingIcon = {
                                            Icon(Icons.Filled.Delete, contentDescription = null, tint = PinkAccent)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onDeleteConversation(conv.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (renameDialogTarget != null) {
        AlertDialog(
            onDismissRequest = { renameDialogTarget = null },
            title = { Text("Renommer la discussion", color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        renameDialogTarget?.let { onRenameConversation(it.id, renameText) }
                        renameDialogTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameDialogTarget = null }) {
                    Text("Annuler", color = TextMutedGray)
                }
            },
            containerColor = DarkSurface
        )
    }
}
