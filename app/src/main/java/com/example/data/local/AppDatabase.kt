package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AutomationDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.GeneratedImageDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.AutomationEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ProjectEntity::class,
        AutomationEntity::class,
        GeneratedImageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun projectDao(): ProjectDao
    abstract fun automationDao(): AutomationDao
    abstract fun generatedImageDao(): GeneratedImageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexus_ai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Initial Projects
            val project1 = ProjectEntity(
                id = "proj_work",
                name = "Travail & Entreprise",
                description = "Projets pro, analyse de marché, rédactions d'emails",
                icon = "work",
                colorHex = "#00E5FF"
            )
            val project2 = ProjectEntity(
                id = "proj_dev",
                name = "Code & Développement",
                description = "Architecture, snippets Kotlin, debugging et scripts",
                icon = "code",
                colorHex = "#6366F1"
            )
            val project3 = ProjectEntity(
                id = "proj_creative",
                name = "Créativité & Contenu",
                description = "Génération d'idées, storytelling et scripts vidéo",
                icon = "palette",
                colorHex = "#EC4899"
            )
            database.projectDao().insertProject(project1)
            database.projectDao().insertProject(project2)
            database.projectDao().insertProject(project3)

            // Initial Automations
            val automations = listOf(
                AutomationEntity(
                    id = "auto_daily_summary",
                    title = "Synthèse quotidienne",
                    description = "Résume les points clés de la journée en 3 bullet points actionnables.",
                    icon = "summarize",
                    promptTemplate = "Résume de manière concise et structurée les informations suivantes en 3 bullet points avec un plan d'action immédiat :\n\n",
                    category = "Productivité"
                ),
                AutomationEntity(
                    id = "auto_code_optimizer",
                    title = "Optimiseur de Code",
                    description = "Refactore et améliore la complexité algorithmique et la lisibilité du code.",
                    icon = "code",
                    promptTemplate = "Analyse et refactore le code suivant en expliquant les optimisations de performance et de clarté :\n\n```kotlin\n\n```",
                    category = "Développement"
                ),
                AutomationEntity(
                    id = "auto_email_pro",
                    title = "Rédacteur d'Email Pro",
                    description = "Formule un email professionnel percutant et courtois prêt à l'envoi.",
                    icon = "mail",
                    promptTemplate = "Rédige un email professionnel, poli et persuasif pour le contexte suivant :\n\n",
                    category = "Communication"
                ),
                AutomationEntity(
                    id = "auto_idea_generator",
                    title = "Générateur d'Idées Flash",
                    description = "Génère 5 concepts innovants et originaux avec propositions de valeur.",
                    icon = "lightbulb",
                    promptTemplate = "Propose 5 idées novatrices et disruptives pour le sujet suivant, avec leurs points forts respectifs :\n\n",
                    category = "Créativité"
                ),
                AutomationEntity(
                    id = "auto_translator",
                    title = "Traduction & Nuances",
                    description = "Traduit avec finesse linguistique et adaptation culturelle.",
                    icon = "translate",
                    promptTemplate = "Traduis le texte suivant en français/anglais en conservant toutes les nuances culturelles et de ton :\n\n",
                    category = "Langues"
                )
            )
            database.automationDao().insertAutomations(automations)

            // Initial Generated Gallery Images
            val images = listOf(
                GeneratedImageEntity(
                    id = "img_1",
                    prompt = "Flacon de parfum de luxe minimaliste sur socle d'obsidienne noire, éclairage dramatique, studio produit 8k",
                    drawableResName = "template_product_studio_1787301913290",
                    aspectRatio = "1:1",
                    stylePreset = "Studio produit",
                    isVideo = false
                ),
                GeneratedImageEntity(
                    id = "img_2",
                    prompt = "Robot mascotte 3D chibi cybernétique mignon, rendu style Pixar, détails néon turquoise lumineux",
                    drawableResName = "template_chibi_3d_1787301926049",
                    aspectRatio = "1:1",
                    stylePreset = "Chibi 3D",
                    isVideo = false
                ),
                GeneratedImageEntity(
                    id = "img_3",
                    prompt = "Mégalopole cyberpunk futuriste sous la pluie de nuit, reflets néon holographiques, bolide aérodynamique",
                    drawableResName = "template_cyberpunk_neon_1787301938998",
                    aspectRatio = "16:9",
                    stylePreset = "Cyberpunk Neon",
                    isVideo = true
                ),
                GeneratedImageEntity(
                    id = "img_4",
                    prompt = "Portail cosmique sci-fi surplombant nébuleuse stellaire et planète extraterrestre, chef d'oeuvre cinématique",
                    drawableResName = "template_cinematic_art_1787301949960",
                    aspectRatio = "1:1",
                    stylePreset = "Art Cinématique",
                    isVideo = false
                )
            )
            images.forEach { database.generatedImageDao().insertGeneratedImage(it) }

            // Initial Starter Conversation
            val starterConversation = ConversationEntity(
                id = "conv_welcome",
                title = "Bienvenue sur Nexus AI ✨",
                projectId = null,
                modelMode = "fast",
                isPinned = true
            )
            database.chatDao().insertConversation(starterConversation)

            val starterMessageUser = MessageEntity(
                conversationId = starterConversation.id,
                role = "user",
                content = "Bonjour ! Peux-tu me présenter les capacités de Nexus AI ?",
                timestamp = System.currentTimeMillis() - 60000
            )
            val starterMessageAssistant = MessageEntity(
                conversationId = starterConversation.id,
                role = "assistant",
                content = "Bonjour et bienvenue sur **Nexus AI** ! 🚀\n\nJe suis votre assistant IA multimodal de nouvelle génération. Voici un aperçu de ce que nous pouvons faire ensemble :\n\n- 💬 **Chat intelligent** : Réponses ultra-rapides ou mode *Réfléchi* avec raisonnement approfondi pour le code, la science et l'analyse complexe.\n- 👁️ **Vision multimodale** : Joignez une photo pour que je l'analyse, l'explique ou la commente en détail.\n- 🎨 **Onglet Imagine** : Générez des visuels et vidéos époustouflants avec des presets de style (Studio produit, Chibi 3D, Cyberpunk, etc.).\n- 🎙️ **Mode Vocal en Direct** : Discutez vocalement avec vue caméra interactive en temps réel.\n- 📂 **Projets & Automations** : Classez vos discussions dans des dossiers de projets et lancez des synthèses instantanées.\n\nComment puis-je vous aider aujourd'hui ?",
                timestamp = System.currentTimeMillis() - 55000,
                tokenCount = 145,
                latencyMs = 620
            )
            database.chatDao().insertMessage(starterMessageUser)
            database.chatDao().insertMessage(starterMessageAssistant)
        }
    }
}
