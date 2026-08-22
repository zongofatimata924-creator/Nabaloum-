package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val projectId: String? = null,
    val modelMode: String = "fast", // "fast" (gemini-3.5-flash) or "thinking" (gemini-3.1-pro-preview)
    val isPinned: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isThinking: Boolean = false,
    val thinkingProcess: String? = null,
    val latencyMs: Long = 0L,
    val tokenCount: Int = 0
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val icon: String = "folder",
    val colorHex: String = "#00E5FF",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val icon: String,
    val promptTemplate: String,
    val category: String,
    val runCount: Int = 0
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val imageUri: String? = null,
    val drawableResName: String? = null,
    val aspectRatio: String = "1:1",
    val stylePreset: String = "Réaliste",
    val isVideo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
