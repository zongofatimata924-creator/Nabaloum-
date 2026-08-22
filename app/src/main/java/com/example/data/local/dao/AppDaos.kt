package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AutomationEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE projectId = :projectId ORDER BY isPinned DESC, updatedAt DESC")
    fun getConversationsByProject(projectId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE conversations SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: String, title: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM conversations")
    suspend fun clearAllConversations()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automations ORDER BY runCount DESC, title ASC")
    fun getAllAutomations(): Flow<List<AutomationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomations(automations: List<AutomationEntity>)

    @Query("UPDATE automations SET runCount = runCount + 1 WHERE id = :id")
    suspend fun incrementRunCount(id: String)
}

@Dao
interface GeneratedImageDao {
    @Query("SELECT * FROM generated_images ORDER BY createdAt DESC")
    fun getAllGeneratedImages(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedImage(image: GeneratedImageEntity)

    @Query("DELETE FROM generated_images WHERE id = :id")
    suspend fun deleteGeneratedImage(id: String)
}
