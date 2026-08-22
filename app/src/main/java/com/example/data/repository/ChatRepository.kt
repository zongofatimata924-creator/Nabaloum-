package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AutomationEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatRepository(
    private val database: AppDatabase,
    private val geminiClient: GeminiClient = GeminiClient()
) {
    val conversations: Flow<List<ConversationEntity>> = database.chatDao().getAllConversations()
    val projects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()
    val automations: Flow<List<AutomationEntity>> = database.automationDao().getAllAutomations()
    val generatedImages: Flow<List<GeneratedImageEntity>> = database.generatedImageDao().getAllGeneratedImages()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return database.chatDao().getMessagesForConversation(conversationId)
    }

    suspend fun createConversation(
        title: String = "Nouvelle discussion",
        projectId: String? = null,
        modelMode: String = "fast"
    ): ConversationEntity = withContext(Dispatchers.IO) {
        val conv = ConversationEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            projectId = projectId,
            modelMode = modelMode,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        database.chatDao().insertConversation(conv)
        conv
    }

    suspend fun sendMessage(
        conversationId: String,
        userPrompt: String,
        bitmap: Bitmap? = null,
        imageUri: String? = null,
        modelMode: String = "fast",
        systemPrompt: String? = null
    ): MessageEntity = withContext(Dispatchers.IO) {
        // 1. Insert user message
        val userMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "user",
            content = userPrompt,
            imageUri = imageUri,
            timestamp = System.currentTimeMillis()
        )
        database.chatDao().insertMessage(userMessage)

        // 2. Fetch history for context
        val existingMessages = database.chatDao().getMessagesForConversation(conversationId).first()
        val historyList = existingMessages.map { Pair(it.role, it.content) }

        // 3. Update conversation title if first message
        if (existingMessages.size <= 1) {
            val shortTitle = if (userPrompt.length > 30) userPrompt.take(27) + "..." else userPrompt
            database.chatDao().updateTitle(conversationId, shortTitle, System.currentTimeMillis())
        } else {
            database.chatDao().updateTitle(
                conversationId,
                existingMessages.firstOrNull()?.content?.take(25) ?: "Discussion",
                System.currentTimeMillis()
            )
        }

        // 4. Call Gemini API
        val result = geminiClient.generateContent(
            history = historyList,
            newPrompt = userPrompt,
            bitmap = bitmap,
            mode = modelMode,
            systemPrompt = systemPrompt
        )

        // 5. Insert AI response
        val assistantMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "assistant",
            content = result.text,
            timestamp = System.currentTimeMillis(),
            isThinking = modelMode == "thinking",
            thinkingProcess = result.thinkingProcess,
            latencyMs = result.latencyMs,
            tokenCount = result.tokenCount
        )
        database.chatDao().insertMessage(assistantMessage)

        assistantMessage
    }

    suspend fun setPinned(conversationId: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        database.chatDao().setPinned(conversationId, isPinned)
    }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.IO) {
        database.chatDao().deleteMessagesForConversation(conversationId)
        database.chatDao().deleteConversation(conversationId)
    }

    suspend fun renameConversation(conversationId: String, newTitle: String) = withContext(Dispatchers.IO) {
        database.chatDao().updateTitle(conversationId, newTitle)
    }

    suspend fun createProject(name: String, description: String, icon: String, colorHex: String) = withContext(Dispatchers.IO) {
        val proj = ProjectEntity(
            name = name,
            description = description,
            icon = icon,
            colorHex = colorHex
        )
        database.projectDao().insertProject(proj)
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        database.projectDao().deleteProject(projectId)
    }

    suspend fun incrementAutomation(automationId: String) = withContext(Dispatchers.IO) {
        database.automationDao().incrementRunCount(automationId)
    }

    suspend fun saveGeneratedImage(
        prompt: String,
        drawableResName: String? = null,
        imageUri: String? = null,
        aspectRatio: String = "1:1",
        stylePreset: String = "Réaliste",
        isVideo: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val entity = GeneratedImageEntity(
            prompt = prompt,
            drawableResName = drawableResName,
            imageUri = imageUri,
            aspectRatio = aspectRatio,
            stylePreset = stylePreset,
            isVideo = isVideo
        )
        database.generatedImageDao().insertGeneratedImage(entity)
        entity
    }

    suspend fun deleteGeneratedImage(id: String) = withContext(Dispatchers.IO) {
        database.generatedImageDao().deleteGeneratedImage(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        database.chatDao().clearAllMessages()
        database.chatDao().clearAllConversations()
    }
}
