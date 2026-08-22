package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AutomationEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.repository.AiSaasConstants
import com.example.data.repository.BankTransferRequest
import com.example.data.repository.ChatRepository
import com.example.data.repository.GenerationJob
import com.example.data.repository.UserSettings
import com.example.data.repository.UserSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val chatRepository = ChatRepository(database)
    val userSettingsRepo = UserSettingsRepository()

    val userSettings: StateFlow<UserSettings> = userSettingsRepo.settings
    val bankTransfers: StateFlow<List<BankTransferRequest>> = userSettingsRepo.bankTransfers

    // Navigation State
    private val _selectedTab = MutableStateFlow(0) // 0 = Chat, 1 = Imagine
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // Active Conversation
    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    // Model mode: "fast" (Rapide - gemini-3.5-flash) | "thinking" (Réfléchi - gemini-3.1-pro-preview)
    private val _modelMode = MutableStateFlow("fast")
    val modelMode: StateFlow<String> = _modelMode.asStateFlow()

    fun setModelMode(mode: String) {
        _modelMode.value = mode
    }

    // Input state
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun setInputText(text: String) {
        _inputText.value = text
    }

    private val _attachedBitmap = MutableStateFlow<Bitmap?>(null)
    val attachedBitmap: StateFlow<Bitmap?> = _attachedBitmap.asStateFlow()

    private val _attachedImageUri = MutableStateFlow<String?>(null)
    val attachedImageUri: StateFlow<String?> = _attachedImageUri.asStateFlow()

    fun setAttachedImage(bitmap: Bitmap?, uri: String?) {
        _attachedBitmap.value = bitmap
        _attachedImageUri.value = uri
    }

    fun clearAttachedImage() {
        _attachedBitmap.value = null
        _attachedImageUri.value = null
    }

    // Chat Loading State
    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage: StateFlow<Boolean> = _isSendingMessage.asStateFlow()

    // Search query in drawer
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Selected project filter in drawer
    private val _selectedProjectFilter = MutableStateFlow<String?>(null)
    val selectedProjectFilter: StateFlow<String?> = _selectedProjectFilter.asStateFlow()

    fun filterByProject(projectId: String?) {
        _selectedProjectFilter.value = projectId
    }

    // Dialog & Overlay states
    private val _isLiveVoiceOpen = MutableStateFlow(false)
    val isLiveVoiceOpen: StateFlow<Boolean> = _isLiveVoiceOpen.asStateFlow()

    fun setLiveVoiceOpen(open: Boolean) {
        _isLiveVoiceOpen.value = open
    }

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    fun setSettingsOpen(open: Boolean) {
        _isSettingsOpen.value = open
    }

    private val _isSubscriptionOpen = MutableStateFlow(false)
    val isSubscriptionOpen: StateFlow<Boolean> = _isSubscriptionOpen.asStateFlow()

    fun setSubscriptionOpen(open: Boolean) {
        _isSubscriptionOpen.value = open
    }

    private val _isOtpAuthOpen = MutableStateFlow(false)
    val isOtpAuthOpen: StateFlow<Boolean> = _isOtpAuthOpen.asStateFlow()

    fun setOtpAuthOpen(open: Boolean) {
        _isOtpAuthOpen.value = open
    }

    private val _isAutomationsOpen = MutableStateFlow(false)
    val isAutomationsOpen: StateFlow<Boolean> = _isAutomationsOpen.asStateFlow()

    fun setAutomationsOpen(open: Boolean) {
        _isAutomationsOpen.value = open
    }

    private val _isProjectsOpen = MutableStateFlow(false)
    val isProjectsOpen: StateFlow<Boolean> = _isProjectsOpen.asStateFlow()

    fun setProjectsOpen(open: Boolean) {
        _isProjectsOpen.value = open
    }

    private val _selectedImageDetail = MutableStateFlow<GeneratedImageEntity?>(null)
    val selectedImageDetail: StateFlow<GeneratedImageEntity?> = _selectedImageDetail.asStateFlow()

    fun openImageDetail(image: GeneratedImageEntity?) {
        _selectedImageDetail.value = image
    }

    // Imagine Tab State (ai_generation_engine.py v5.1.0)
    private val _imagineIsVideo = MutableStateFlow(false)
    val imagineIsVideo: StateFlow<Boolean> = _imagineIsVideo.asStateFlow()

    fun setImagineIsVideo(isVideo: Boolean) {
        _imagineIsVideo.value = isVideo
        userSettingsRepo.saveUserPreferences(mediaType = if (isVideo) "video" else "image")
    }

    private val _selectedImageModel = MutableStateFlow("nano_banana_2")
    val selectedImageModel: StateFlow<String> = _selectedImageModel.asStateFlow()

    fun setSelectedImageModel(model: String) {
        _selectedImageModel.value = model
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(imageModel = model)
        }
    }

    private val _selectedVideoModel = MutableStateFlow("omni_flash")
    val selectedVideoModel: StateFlow<String> = _selectedVideoModel.asStateFlow()

    fun setSelectedVideoModel(model: String) {
        _selectedVideoModel.value = model
        // Validate duration with model
        val modelInfo = AiSaasConstants.VIDEO_MODELS[model]
        if (modelInfo != null && _selectedDuration.value !in modelInfo.supportedDurations) {
            _selectedDuration.value = modelInfo.supportedDurations.first()
        }
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(videoModel = model)
        }
    }

    private val _imaginePrompt = MutableStateFlow("")
    val imaginePrompt: StateFlow<String> = _imaginePrompt.asStateFlow()

    fun setImaginePrompt(prompt: String) {
        _imaginePrompt.value = prompt
    }

    private val _imagineAspectRatio = MutableStateFlow("16:9")
    val imagineAspectRatio: StateFlow<String> = _imagineAspectRatio.asStateFlow()

    fun setImagineAspectRatio(ratio: String) {
        _imagineAspectRatio.value = ratio
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(aspectRatio = ratio)
        }
    }

    private val _selectedQuantity = MutableStateFlow(1)
    val selectedQuantity: StateFlow<Int> = _selectedQuantity.asStateFlow()

    fun setSelectedQuantity(qty: Int) {
        _selectedQuantity.value = qty.coerceIn(1, 4)
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(quantity = qty)
        }
    }

    private val _selectedDuration = MutableStateFlow(8)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    fun setSelectedDuration(dur: Int) {
        _selectedDuration.value = dur
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(duration = dur)
        }
    }

    private val _imagineStylePreset = MutableStateFlow("Réaliste")
    val imagineStylePreset: StateFlow<String> = _imagineStylePreset.asStateFlow()

    fun setImagineStylePreset(style: String) {
        _imagineStylePreset.value = style
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(style = style)
        }
    }

    private val _saveAsDefault = MutableStateFlow(true)
    val saveAsDefault: StateFlow<Boolean> = _saveAsDefault.asStateFlow()

    fun setSaveAsDefault(save: Boolean) {
        _saveAsDefault.value = save
        userSettingsRepo.saveUserPreferences(saveAsDefault = save)
    }

    private val _isGeneratingImagine = MutableStateFlow(false)
    val isGeneratingImagine: StateFlow<Boolean> = _isGeneratingImagine.asStateFlow()

    private val _activeJobs = MutableStateFlow<List<GenerationJob>>(emptyList())
    val activeJobs: StateFlow<List<GenerationJob>> = _activeJobs.asStateFlow()

    // Reactive Data Sources
    val allConversations: StateFlow<List<ConversationEntity>> = chatRepository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredConversations: StateFlow<List<ConversationEntity>> = combine(
        allConversations,
        _searchQuery,
        _selectedProjectFilter
    ) { convs, query, projId ->
        convs.filter { conv ->
            val matchesQuery = query.isBlank() || conv.title.contains(query, ignoreCase = true)
            val matchesProject = projId == null || conv.projectId == projId
            matchesQuery && matchesProject
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = chatRepository.projects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationEntity>> = chatRepository.automations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedImages: StateFlow<List<GeneratedImageEntity>> = chatRepository.generatedImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<MessageEntity>> = _currentConversationId
        .flatMapLatest { convId ->
            if (convId != null) chatRepository.getMessages(convId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            allConversations.collect { convs ->
                if (_currentConversationId.value == null && convs.isNotEmpty()) {
                    _currentConversationId.value = convs.first().id
                }
            }
        }
    }

    fun selectConversation(conversationId: String) {
        _currentConversationId.value = conversationId
    }

    fun startNewConversation(projectId: String? = null) {
        viewModelScope.launch {
            val newConv = chatRepository.createConversation(
                title = "Nouvelle discussion",
                projectId = projectId,
                modelMode = _modelMode.value
            )
            _currentConversationId.value = newConv.id
            _inputText.value = ""
            clearAttachedImage()
        }
    }

    fun sendMessage(customPrompt: String? = null) {
        val promptToSend = customPrompt ?: _inputText.value
        if (promptToSend.isBlank() && _attachedBitmap.value == null) return

        val currentConvId = _currentConversationId.value
        val bitmap = _attachedBitmap.value
        val uri = _attachedImageUri.value
        val mode = _modelMode.value
        val settings = userSettings.value

        val persona = AiSaasConstants.AI_MODELS.find { it.id == settings.preferredModel }
            ?: AiSaasConstants.AI_MODELS.first()
        val voice = AiSaasConstants.VOICE_STYLES.find { it.id == settings.preferredVoice }
        val personaPrompt = persona.systemPromptTemplate(settings.aiName)
        val systemPrompt = "$personaPrompt Style de voix: ${voice?.label ?: "Femme douce"}. Instructions: ${settings.customInstructions}"

        _inputText.value = ""
        clearAttachedImage()
        _isSendingMessage.value = true

        viewModelScope.launch {
            val convId = if (currentConvId == null) {
                val created = chatRepository.createConversation(
                    title = promptToSend.take(25),
                    modelMode = mode
                )
                _currentConversationId.value = created.id
                created.id
            } else {
                currentConvId
            }

            chatRepository.sendMessage(
                conversationId = convId,
                userPrompt = promptToSend,
                bitmap = bitmap,
                imageUri = uri,
                modelMode = mode,
                systemPrompt = systemPrompt
            )
            _isSendingMessage.value = false
        }
    }

    fun runAutomation(automation: AutomationEntity) {
        viewModelScope.launch {
            chatRepository.incrementAutomation(automation.id)
            _isAutomationsOpen.value = false
            _selectedTab.value = 0 // Switch to chat
            startNewConversation()
            delay(100)
            _inputText.value = automation.promptTemplate
        }
    }

    fun togglePinned(conversation: ConversationEntity) {
        viewModelScope.launch {
            chatRepository.setPinned(conversation.id, !conversation.isPinned)
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            chatRepository.deleteConversation(conversationId)
            if (_currentConversationId.value == conversationId) {
                val remaining = allConversations.value.filter { it.id != conversationId }
                _currentConversationId.value = remaining.firstOrNull()?.id
            }
        }
    }

    fun renameConversation(conversationId: String, newTitle: String) {
        viewModelScope.launch {
            chatRepository.renameConversation(conversationId, newTitle)
        }
    }

    fun createProject(name: String, description: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            chatRepository.createProject(name, description, icon, colorHex)
            _isProjectsOpen.value = false
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            chatRepository.deleteProject(projectId)
        }
    }

    fun generateImagineContent(
        prompt: String,
        aspectRatio: String = _imagineAspectRatio.value,
        stylePreset: String = _imagineStylePreset.value,
        isVideo: Boolean = _imagineIsVideo.value,
        modelOverride: String? = null,
        quantityOverride: Int? = null,
        durationOverride: Int? = null,
        referenceDrawable: String? = null
    ) {
        if (prompt.isBlank()) return

        val mediaType = if (isVideo) "video" else "image"
        val model = modelOverride ?: (if (isVideo) _selectedVideoModel.value else _selectedImageModel.value)
        val quantity = quantityOverride ?: _selectedQuantity.value
        val duration = if (isVideo) (durationOverride ?: _selectedDuration.value) else null
        val settings = userSettings.value

        val creditsNeeded = userSettingsRepo.calculateCredits(mediaType, model, quantity, duration ?: 0)

        // Subscription & Quota verification (ai_generation_engine.py logic)
        if (!settings.isSubscribed && creditsNeeded > 0) {
            _isSubscriptionOpen.value = true
            return
        }

        if (!settings.isSubscribed && !isVideo && !userSettingsRepo.incrementImageQuota()) {
            _isSubscriptionOpen.value = true
            return
        }

        // Save preferences if saveAsDefault is enabled
        if (_saveAsDefault.value) {
            userSettingsRepo.saveUserPreferences(
                mediaType = mediaType,
                imageModel = if (!isVideo) model else null,
                videoModel = if (isVideo) model else null,
                aspectRatio = aspectRatio,
                quantity = quantity,
                duration = duration,
                style = stylePreset,
                saveAsDefault = true
            )
        }

        val modelLabel = if (isVideo) {
            AiSaasConstants.VIDEO_MODELS[model]?.label ?: "Omni Flash"
        } else {
            AiSaasConstants.IMAGE_MODELS[model]?.label ?: "Nano Banana 2"
        }

        val job = GenerationJob(
            type = mediaType,
            model = model,
            modelLabel = modelLabel,
            prompt = prompt,
            aspectRatio = aspectRatio,
            quantity = quantity,
            duration = duration,
            style = stylePreset,
            status = "processing",
            creditsUsed = if (settings.isSubscribed) 0 else creditsNeeded,
            estimatedTimeSeconds = if (isVideo) 45 else 15
        )

        _activeJobs.update { listOf(job) + it }
        _isGeneratingImagine.value = true

        viewModelScope.launch {
            delay(1400) // Simulated creative rendering animation (Celery job simulation)

            val sampleDrawables = listOf(
                "template_product_studio_1787301913290",
                "template_chibi_3d_1787301926049",
                "template_cyberpunk_neon_1787301938998",
                "template_cinematic_art_1787301949960"
            )

            for (i in 1..quantity) {
                val chosenDrawable = referenceDrawable ?: sampleDrawables[(i - 1) % sampleDrawables.size]
                chatRepository.saveGeneratedImage(
                    prompt = if (quantity > 1) "$prompt (Variation #$i)" else prompt,
                    drawableResName = chosenDrawable,
                    aspectRatio = aspectRatio,
                    stylePreset = stylePreset,
                    isVideo = isVideo
                )
            }

            // Update Job status to completed
            _activeJobs.update { currentJobs ->
                currentJobs.map {
                    if (it.id == job.id) it.copy(status = "completed", resultUrl = "https://cdn.aria.ai/$mediaType/${job.id}") else it
                }
            }

            _imaginePrompt.value = ""
            _isGeneratingImagine.value = false
        }
    }

    fun deleteGeneratedImage(id: String) {
        viewModelScope.launch {
            chatRepository.deleteGeneratedImage(id)
            if (_selectedImageDetail.value?.id == id) {
                _selectedImageDetail.value = null
            }
        }
    }

    fun clearAllChatHistory() {
        viewModelScope.launch {
            chatRepository.clearAllHistory()
            _currentConversationId.value = null
        }
    }

    fun activateStripe(planKey: String, currency: String) {
        userSettingsRepo.setSubscribed(true)
        userSettingsRepo.setPreferredCurrency(currency)
    }

    fun createBankTransfer(planKey: String, currency: String): BankTransferRequest {
        return userSettingsRepo.createBankTransferRequest(planKey, currency)
    }

    fun verifyOtp(email: String, otp: String): Boolean {
        if (otp.length == 6) {
            userSettingsRepo.loginWithOtp(email)
            return true
        }
        return false
    }
}
