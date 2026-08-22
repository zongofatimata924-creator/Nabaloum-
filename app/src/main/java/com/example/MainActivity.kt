package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChatViewModel
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.AppTopTabBar
import com.example.ui.components.AutomationsDialog
import com.example.ui.components.ImageDetailDialog
import com.example.ui.components.LiveVoiceCameraModal
import com.example.ui.components.OtpAuthDialog
import com.example.ui.components.ProjectsDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SubscriptionDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ImagineScreen
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: ChatViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val selectedTab by viewModel.selectedTab.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val allConversations by viewModel.allConversations.collectAsState()
    val currentConvId by viewModel.currentConversationId.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val automations by viewModel.automations.collectAsState()
    val selectedProjectFilter by viewModel.selectedProjectFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Dialog overlay states
    val isLiveVoiceOpen by viewModel.isLiveVoiceOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isSubscriptionOpen by viewModel.isSubscriptionOpen.collectAsState()
    val isOtpAuthOpen by viewModel.isOtpAuthOpen.collectAsState()
    val isAutomationsOpen by viewModel.isAutomationsOpen.collectAsState()
    val isProjectsOpen by viewModel.isProjectsOpen.collectAsState()
    val selectedImageDetail by viewModel.selectedImageDetail.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSurface,
                modifier = Modifier.width(320.dp)
            ) {
                AppDrawerContent(
                    userSettings = userSettings,
                    conversations = allConversations,
                    selectedConversationId = currentConvId,
                    projects = projects,
                    automations = automations,
                    searchQuery = searchQuery,
                    selectedProjectFilter = selectedProjectFilter,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onSelectConversation = { convId ->
                        viewModel.selectConversation(convId)
                        scope.launch { drawerState.close() }
                    },
                    onNewConversation = {
                        viewModel.startNewConversation()
                        scope.launch { drawerState.close() }
                    },
                    onTogglePin = { conv ->
                        viewModel.togglePinned(conv)
                    },
                    onRenameConversation = { convId, newTitle ->
                        viewModel.renameConversation(convId, newTitle)
                    },
                    onDeleteConversation = { convId ->
                        viewModel.deleteConversation(convId)
                    },
                    onFilterByProject = { projId ->
                        viewModel.filterByProject(projId)
                    },
                    onOpenAutomations = {
                        viewModel.setAutomationsOpen(true)
                        scope.launch { drawerState.close() }
                    },
                    onOpenProjects = {
                        viewModel.setProjectsOpen(true)
                        scope.launch { drawerState.close() }
                    },
                    onOpenSettings = {
                        viewModel.setSettingsOpen(true)
                        scope.launch { drawerState.close() }
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            containerColor = DarkBg
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBg)
            ) {
                // Sleek Header
                SleekAppHeader(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onOpenSettings = { viewModel.setSettingsOpen(true) },
                    userInitial = userSettings.userName.take(2).uppercase(),
                    aiName = userSettings.aiName
                )

                // Sleek Top Nav Tabs
                AppTopTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )

                // Screen Content: Animated Tab Switch
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screenTabTransition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> ChatScreen(
                                viewModel = viewModel,
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onOpenSettings = { viewModel.setSettingsOpen(true) }
                            )
                            1 -> ImagineScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Overlays
    if (isLiveVoiceOpen) {
        LiveVoiceCameraModal(
            onClose = { viewModel.setLiveVoiceOpen(false) }
        )
    }

    if (isSettingsOpen) {
        SettingsDialog(
            userSettings = userSettings,
            onAiNameChange = { viewModel.userSettingsRepo.setAiName(it) },
            onModelChange = { viewModel.userSettingsRepo.setPreferredModel(it) },
            onVoiceChange = { viewModel.userSettingsRepo.setPreferredVoice(it) },
            onCurrencyChange = { viewModel.userSettingsRepo.setPreferredCurrency(it) },
            onCustomInstructionsChange = { viewModel.userSettingsRepo.setCustomInstructions(it) },
            onOpenSubscriptionDialog = {
                viewModel.setSettingsOpen(false)
                viewModel.setSubscriptionOpen(true)
            },
            onOpenOtpDialog = {
                viewModel.setSettingsOpen(false)
                viewModel.setOtpAuthOpen(true)
            },
            onToggleWebSearch = { viewModel.userSettingsRepo.toggleWebSearch() },
            onToggleGoogleDrive = { viewModel.userSettingsRepo.toggleGoogleDrive() },
            onToggleGithub = { viewModel.userSettingsRepo.toggleGithub() },
            onToggleNotion = { viewModel.userSettingsRepo.toggleNotion() },
            onClearHistory = { viewModel.clearAllChatHistory() },
            onDismiss = { viewModel.setSettingsOpen(false) }
        )
    }

    if (isSubscriptionOpen) {
        SubscriptionDialog(
            userSettings = userSettings,
            onSelectCurrency = { viewModel.userSettingsRepo.setPreferredCurrency(it) },
            onActivateStripe = { plan, currency ->
                viewModel.activateStripe(plan, currency)
            },
            onCreateBankTransfer = { plan, currency ->
                viewModel.createBankTransfer(plan, currency)
            },
            onDismiss = { viewModel.setSubscriptionOpen(false) }
        )
    }

    if (isOtpAuthOpen) {
        OtpAuthDialog(
            currentEmail = userSettings.userEmail,
            aiName = userSettings.aiName,
            onVerifyOtp = { email, otp ->
                viewModel.verifyOtp(email, otp)
            },
            onDismiss = { viewModel.setOtpAuthOpen(false) }
        )
    }

    if (isAutomationsOpen) {
        AutomationsDialog(
            automations = automations,
            onRunAutomation = { auto ->
                viewModel.runAutomation(auto)
            },
            onDismiss = { viewModel.setAutomationsOpen(false) }
        )
    }

    if (isProjectsOpen) {
        ProjectsDialog(
            projects = projects,
            onCreateProject = { name, desc, icon, color ->
                viewModel.createProject(name, desc, icon, color)
            },
            onDeleteProject = { id ->
                viewModel.deleteProject(id)
            },
            onDismiss = { viewModel.setProjectsOpen(false) }
        )
    }

    val selectedImg = selectedImageDetail
    if (selectedImg != null) {
        ImageDetailDialog(
            image = selectedImg,
            onDelete = { id ->
                viewModel.deleteGeneratedImage(id)
            },
            onDismiss = { viewModel.openImageDetail(null) }
        )
    }
}

@Composable
private fun SleekAppHeader(
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    userInitial: String,
    aiName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Menu Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Color.White.copy(alpha = 0.15f))
                ) { onOpenDrawer() }
                .testTag("btn_header_menu"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu latéral",
                tint = TextWhite,
                modifier = Modifier.size(22.dp)
            )
        }

        // Center: App Title with AI Name
        Text(
            text = "${aiName.uppercase()} AI",
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                color = TextWhite.copy(alpha = 0.6f)
            )
        )

        // Right: Profile Avatar Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Color.White.copy(alpha = 0.15f))
                ) { onOpenSettings() }
                .testTag("btn_header_profile"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(BlueAccent, IndigoAccent)
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitial,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
