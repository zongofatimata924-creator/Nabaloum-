package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.ChatViewModel
import com.example.ui.components.AiLoadingMessageItem
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.EmptyChatView
import com.example.ui.theme.DarkBg

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.currentMessages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isSending by viewModel.isSendingMessage.collectAsState()
    val modelMode by viewModel.modelMode.collectAsState()
    val attachedBitmap by viewModel.attachedBitmap.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message or when AI begins generating with shimmer
    LaunchedEffect(messages.size, isSending) {
        val totalCount = messages.size + if (isSending) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("chat_screen")
    ) {
        // Main Content Area: Empty View vs Message Stream
        Box(modifier = Modifier.weight(1f)) {
            if (messages.isEmpty() && !isSending) {
                com.example.ui.components.AriaActionMenu(
                    onSelectAction = { actionType, prompt ->
                        if (actionType == "image") {
                            viewModel.setImaginePrompt(prompt)
                            viewModel.selectTab(1)
                            viewModel.generateImagineContent(prompt = prompt)
                        } else {
                            viewModel.sendMessage(prompt)
                        }
                    },
                    onOpenImagineTab = {
                        viewModel.selectTab(1)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            onRegenerate = {
                                viewModel.sendMessage(it)
                            }
                        )
                    }

                    // Shimmer loading item displayed when AI is generating its response
                    if (isSending) {
                        item(key = "ai_generating_shimmer_loading") {
                            AiLoadingMessageItem(
                                modelMode = modelMode,
                                aiName = userSettings.aiName
                            )
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        ChatInputBar(
            inputText = inputText,
            onInputTextChange = { viewModel.setInputText(it) },
            onSendMessage = { viewModel.sendMessage() },
            isSending = isSending,
            modelMode = modelMode,
            onModelModeChange = { viewModel.setModelMode(it) },
            attachedBitmap = attachedBitmap,
            onImageSelected = { bitmap, uri -> viewModel.setAttachedImage(bitmap, uri) },
            onClearAttachedImage = { viewModel.clearAttachedImage() },
            onOpenLiveVoice = { viewModel.setLiveVoiceOpen(true) }
        )
    }
}
