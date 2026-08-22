package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class VoiceStyle(
    val id: String,
    val label: String,
    val gender: String,
    val description: String
)

data class AiModelPersona(
    val id: String,
    val label: String,
    val description: String,
    val systemPromptTemplate: (aiName: String) -> String
)

data class PlanPricing(
    val planKey: String, // "weekly", "monthly", "yearly"
    val label: String,
    val formattedPrice: String,
    val amountCents: Int,
    val badge: String? = null
)

data class BankTransferRequest(
    val id: String = UUID.randomUUID().toString(),
    val reference: String,
    val plan: String,
    val currency: String,
    val amountFormatted: String,
    val amountCents: Int,
    val expiresAt: Long = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L),
    val status: String = "En attente", // "En attente", "Confirmé"
    val iban: String = "FR76 3000 4000 5000 6000 7000 123",
    val bic: String = "BNPAFRPAXXX",
    val beneficiary: String = "Aria AI SaaS Inc."
)

data class UserSettings(
    val userId: Int = 104,
    val userName: String = "Fatima Zongo",
    val userEmail: String = "zongofatimata924@gmail.com",
    val isLoggedIn: Boolean = true,
    val isSubscribed: Boolean = true, // Subscribed -> Unlimited access
    val aiName: String = "Aria", // Default AI name
    val preferredVoice: String = "femme_douce",
    val preferredModel: String = "assistant",
    val preferredCurrency: String = "eur", // "eur", "usd", "xof", "mad"
    val countryCode: String = "FR",
    val customInstructions: String = "Fournis des réponses modernes, concises et bien structurées.",
    val dailyImagesUsed: Int = 0,
    val maxFreeImagesPerDay: Int = 3,
    val webSearchEnabled: Boolean = true,
    val googleDriveConnected: Boolean = true,
    val githubConnected: Boolean = true,
    val notionConnected: Boolean = false,
    val encryptionEnabled: Boolean = true, // Fernet symmetric encryption enabled
    // AI Generation Engine Preferences (ai_generation_engine.py v5.1.0)
    val preferredMediaType: String = "image", // "image" or "video"
    val preferredImageModel: String = "nano_banana_2", // "nano_banana_pro", "nano_banana_2", "nano_banana_2_lite"
    val preferredVideoModel: String = "omni_flash", // "omni_flash", "veo_3_1_lite"
    val preferredAspectRatio: String = "16:9", // "16:9", "9:16", "4:3", "1:1", "3:4"
    val preferredQuantity: Int = 1, // 1..4
    val preferredDuration: Int = 8, // 4, 6, 8, 10
    val preferredStyle: String = "Réaliste",
    val saveAsDefault: Boolean = true
) {
    // Helper for backward compatibility
    val isPro: Boolean get() = isSubscribed
    val aiTone: String get() = preferredModel
    val selectedVoice: String get() = preferredVoice
}

data class ImageModelInfo(
    val id: String,
    val label: String,
    val credits: Int,
    val maxResolution: String,
    val quality: String,
    val description: String
)

data class VideoModelInfo(
    val id: String,
    val label: String,
    val creditsPerVideo: Int,
    val maxDuration: Int,
    val supportedDurations: List<Int>,
    val description: String
)

data class GenerationJob(
    val id: String = UUID.randomUUID().toString(),
    val type: String, // "image" or "video"
    val model: String,
    val modelLabel: String,
    val prompt: String,
    val aspectRatio: String,
    val quantity: Int,
    val duration: Int? = null,
    val voice: String? = null,
    val style: String? = null,
    val status: String = "processing", // "processing", "completed", "failed"
    val creditsUsed: Int = 0,
    val estimatedTimeSeconds: Int = 15,
    val createdAt: Long = System.currentTimeMillis(),
    val resultUrl: String? = null
)

object AiSaasConstants {
    // AI Generation Engine Models (ai_generation_engine.py v5.1.0)
    val IMAGE_MODELS = mapOf(
        "nano_banana_pro" to ImageModelInfo(
            id = "nano_banana_pro",
            label = "Nano Banana Pro",
            credits = 0,
            maxResolution = "1024x1024",
            quality = "high",
            description = "Qualité studio haute fidélité & détails photo avancés"
        ),
        "nano_banana_2" to ImageModelInfo(
            id = "nano_banana_2",
            label = "Nano Banana 2",
            credits = 0,
            maxResolution = "1024x1024",
            quality = "standard",
            description = "Équilibré, standard haute définition et textures nettes"
        ),
        "nano_banana_2_lite" to ImageModelInfo(
            id = "nano_banana_2_lite",
            label = "Nano Banana 2 Lite",
            credits = 0,
            maxResolution = "768x768",
            quality = "fast",
            description = "Génération ultra-rapide optimisée pour les concepts et brouillons"
        )
    )

    val VIDEO_MODELS = mapOf(
        "omni_flash" to VideoModelInfo(
            id = "omni_flash",
            label = "Omni Flash",
            creditsPerVideo = 12,
            maxDuration = 10,
            supportedDurations = listOf(4, 6, 8, 10),
            description = "Moteur cinématique haute dynamique jusqu'à 10s"
        ),
        "veo_3_1_lite" to VideoModelInfo(
            id = "veo_3_1_lite",
            label = "Veo 3.1 - Lite",
            creditsPerVideo = 10,
            maxDuration = 8,
            supportedDurations = listOf(4, 6, 8),
            description = "Animation fluide et rendu stylisé jusqu'à 8s"
        )
    )

    val ASPECT_RATIOS = listOf("16:9", "9:16", "4:3", "1:1", "3:4")
    val QUANTITIES = listOf(1, 2, 3, 4)
    val VIDEO_DURATIONS = listOf(4, 6, 8, 10)

    val STYLE_PRESETS = listOf(
        "Réaliste",
        "Cinématique",
        "Cyberpunk",
        "Chibi 3D",
        "Studio Commercial",
        "Anime / Manga",
        "Aquarelle",
        "Minimaliste"
    )
    val VOICE_STYLES = listOf(
        VoiceStyle(
            id = "femme_douce",
            label = "Femme douce",
            gender = "female",
            description = "Voix féminine chaleureuse et calme"
        ),
        VoiceStyle(
            id = "femme_energique",
            label = "Femme énergique",
            gender = "female",
            description = "Voix féminine dynamique et motivante"
        ),
        VoiceStyle(
            id = "homme_calme",
            label = "Homme calme",
            gender = "male",
            description = "Voix masculine posée et rassurante"
        ),
        VoiceStyle(
            id = "homme_professionnel",
            label = "Homme professionnel",
            gender = "male",
            description = "Voix masculine claire et formelle"
        ),
        VoiceStyle(
            id = "neutre",
            label = "Neutre",
            gender = "neutral",
            description = "Voix neutre, ni homme ni femme"
        )
    )

    val AI_MODELS = listOf(
        AiModelPersona(
            id = "assistant",
            label = "Assistant classique",
            description = "Utile, précis et poli pour toutes les tâches du quotidien",
            systemPromptTemplate = { name -> "Tu es $name, une assistante IA utile, précise et polie." }
        ),
        AiModelPersona(
            id = "creatif",
            label = "Créatif",
            description = "Imaginatif, inspirant et axé sur l'art et l'innovation",
            systemPromptTemplate = { name -> "Tu es $name, une IA très créative, imaginative et inspirante." }
        ),
        AiModelPersona(
            id = "coach",
            label = "Coach motivant",
            description = "Énergique, stimulant et bienveillant pour vos objectifs",
            systemPromptTemplate = { name -> "Tu es $name, un coach motivant, énergique et bienveillant." }
        ),
        AiModelPersona(
            id = "expert",
            label = "Expert technique",
            description = "Rigueur scientifique, code de haute qualité et pédagogie",
            systemPromptTemplate = { name -> "Tu es $name, une experte technique, précise et pédagogique." }
        ),
        AiModelPersona(
            id = "ami",
            label = "Ami proche",
            description = "Chaleureux, empathique et à l'écoute bienveillante",
            systemPromptTemplate = { name -> "Tu es $name, un ami proche, chaleureux et empathique." }
        )
    )

    val PRICING_BY_CURRENCY = mapOf(
        "eur" to listOf(
            PlanPricing("weekly", "Hebdomadaire", "3,55 €", 355, null),
            PlanPricing("monthly", "Mensuel", "15 €", 1500, "Populaire"),
            PlanPricing("yearly", "Annuel", "45 €", 4500, "-75% Économie")
        ),
        "usd" to listOf(
            PlanPricing("weekly", "Hebdomadaire", "$3.99", 399, null),
            PlanPricing("monthly", "Mensuel", "$16.99", 1699, "Populaire"),
            PlanPricing("yearly", "Annuel", "$49.99", 4999, "-75% Save")
        ),
        "xof" to listOf(
            PlanPricing("weekly", "Hebdomadaire", "2 300 FCFA", 230000, null),
            PlanPricing("monthly", "Mensuel", "10 000 FCFA", 1000000, "Populaire"),
            PlanPricing("yearly", "Annuel", "30 000 FCFA", 3000000, "-75% Réduction")
        ),
        "mad" to listOf(
            PlanPricing("weekly", "Hebdomadaire", "39 MAD", 3900, null),
            PlanPricing("monthly", "Mensuel", "160 MAD", 16000, "Populaire"),
            PlanPricing("yearly", "Annuel", "480 MAD", 48000, "-75% Économie")
        )
    )

    val CURRENCIES = listOf(
        "eur" to "EUR (€)",
        "usd" to "USD ($)",
        "xof" to "XOF (FCFA)",
        "mad" to "MAD (DH)"
    )
}

class UserSettingsRepository {
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _bankTransfers = MutableStateFlow<List<BankTransferRequest>>(emptyList())
    val bankTransfers: StateFlow<List<BankTransferRequest>> = _bankTransfers.asStateFlow()

    fun updateSettings(newSettings: UserSettings) = _settings.update { newSettings }
    fun updateUserName(name: String) = _settings.update { it.copy(userName = name) }
    fun updateUserEmail(email: String) = _settings.update { it.copy(userEmail = email) }
    fun togglePro() = _settings.update { it.copy(isSubscribed = !it.isSubscribed) }
    fun setSubscribed(subscribed: Boolean) = _settings.update { it.copy(isSubscribed = subscribed) }

    fun setAiName(name: String) = _settings.update { it.copy(aiName = name.ifBlank { "Aria" }) }
    fun setPreferredVoice(voiceId: String) = _settings.update { it.copy(preferredVoice = voiceId) }
    fun setPreferredModel(modelId: String) = _settings.update { it.copy(preferredModel = modelId) }
    fun setPreferredCurrency(currency: String) = _settings.update { it.copy(preferredCurrency = currency) }
    fun setCountryCode(code: String) = _settings.update { it.copy(countryCode = code) }
    fun setCustomInstructions(instructions: String) = _settings.update { it.copy(customInstructions = instructions) }

    // Compat helpers
    fun updateTone(tone: String) = setPreferredModel(tone.lowercase().let {
        when {
            it.contains("ami") -> "ami"
            it.contains("créa") -> "creatif"
            it.contains("coach") -> "coach"
            it.contains("expert") -> "expert"
            else -> "assistant"
        }
    })
    fun updateVoice(voice: String) = setPreferredVoice(voice.lowercase().let {
        when {
            it.contains("douce") || it.contains("kore") -> "femme_douce"
            it.contains("énergique") || it.contains("puck") -> "femme_energique"
            it.contains("calme") || it.contains("charon") -> "homme_calme"
            it.contains("prof") || it.contains("fenrir") -> "homme_professionnel"
            else -> "neutre"
        }
    })
    fun updateCustomInstructions(inst: String) = setCustomInstructions(inst)

    fun toggleWebSearch() = _settings.update { it.copy(webSearchEnabled = !it.webSearchEnabled) }
    fun toggleGoogleDrive() = _settings.update { it.copy(googleDriveConnected = !it.googleDriveConnected) }
    fun toggleGithub() = _settings.update { it.copy(githubConnected = !it.githubConnected) }
    fun toggleNotion() = _settings.update { it.copy(notionConnected = !it.notionConnected) }

    fun incrementImageQuota(): Boolean {
        val current = _settings.value
        if (current.isSubscribed) return true // Unlimited
        if (current.dailyImagesUsed >= current.maxFreeImagesPerDay) return false
        _settings.update { it.copy(dailyImagesUsed = it.dailyImagesUsed + 1) }
        return true
    }

    fun loginWithOtp(email: String, name: String = "Fatima Zongo") {
        _settings.update {
            it.copy(
                userEmail = email,
                userName = name,
                isLoggedIn = true
            )
        }
    }

    fun logout() {
        _settings.update {
            it.copy(
                isLoggedIn = false
            )
        }
    }

    fun saveUserPreferences(
        mediaType: String? = null,
        imageModel: String? = null,
        videoModel: String? = null,
        aspectRatio: String? = null,
        quantity: Int? = null,
        duration: Int? = null,
        style: String? = null,
        saveAsDefault: Boolean? = null
    ) {
        _settings.update { current ->
            current.copy(
                preferredMediaType = mediaType ?: current.preferredMediaType,
                preferredImageModel = imageModel ?: current.preferredImageModel,
                preferredVideoModel = videoModel ?: current.preferredVideoModel,
                preferredAspectRatio = aspectRatio ?: current.preferredAspectRatio,
                preferredQuantity = quantity ?: current.preferredQuantity,
                preferredDuration = duration ?: current.preferredDuration,
                preferredStyle = style ?: current.preferredStyle,
                saveAsDefault = saveAsDefault ?: current.saveAsDefault
            )
        }
    }

    fun calculateCredits(mediaType: String, model: String, quantity: Int = 1, duration: Int = 0): Int {
        return if (mediaType == "image") {
            val base = AiSaasConstants.IMAGE_MODELS[model]?.credits ?: 0
            base * quantity
        } else {
            val base = AiSaasConstants.VIDEO_MODELS[model]?.creditsPerVideo ?: 12
            base * quantity
        }
    }

    fun createBankTransferRequest(planKey: String, currency: String): BankTransferRequest {
        val plans = AiSaasConstants.PRICING_BY_CURRENCY[currency.lowercase()] 
            ?: AiSaasConstants.PRICING_BY_CURRENCY["eur"]!!
        val plan = plans.find { it.planKey == planKey } ?: plans.first()
        
        val randomHex = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        val reference = "AI-${_settings.value.userId}-$randomHex"

        val request = BankTransferRequest(
            reference = reference,
            plan = plan.label,
            currency = currency.uppercase(),
            amountFormatted = plan.formattedPrice,
            amountCents = plan.amountCents
        )

        _bankTransfers.update { listOf(request) + it }
        return request
    }
}
