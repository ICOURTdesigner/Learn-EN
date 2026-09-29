package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TamizhaApp
import com.example.audio.SpeechRecognitionHelper
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.GrammarQuestion
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.PronunciationExercise
import com.example.data.model.PronunciationFeedback
import com.example.data.model.UserProgressEntity
import com.example.data.model.WeakSpotEntity
import com.example.data.repository.CurriculumData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val titleTamil: String, val titleEnglish: String) {
    VOICE_CLASS("குரல் வகுப்பு", "Voice Class"),
    GRAMMAR("இலக்கணம்", "Grammar Drills"),
    PRONUNCIATION("உச்சரிப்பு", "Pronunciation"),
    ANALYTICS("முன்னேற்றம்", "AI Analytics")
}

class MainViewModel : ViewModel() {

    private val app = TamizhaApp.instance
    private val repository = app.repository
    private val ttsManager = app.ttsManager
    private val aiService = app.aiService

    // Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.VOICE_CLASS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Room Database Flows
    val userProgress: StateFlow<UserProgressEntity?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentSessions: StateFlow<List<PracticeSessionEntity>> = repository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weakSpots: StateFlow<List<WeakSpotEntity>> = repository.weakSpots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // TTS state
    val isTtsSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    // Speech Recognizer
    private var speechRecognizerHelper: SpeechRecognitionHelper? = null
    private val _isMicListening = MutableStateFlow(false)
    val isMicListening: StateFlow<Boolean> = _isMicListening.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _liveSpokenText = MutableStateFlow("")
    val liveSpokenText: StateFlow<String> = _liveSpokenText.asStateFlow()

    // Feedback Delivery Language (Tamil vs English)
    private val _feedbackLanguage = MutableStateFlow(com.example.data.model.FeedbackLanguage.TAMIL)
    val feedbackLanguage: StateFlow<com.example.data.model.FeedbackLanguage> = _feedbackLanguage.asStateFlow()

    // -------------------------------------------------------------
    // VOICE CLASSROOM STATE
    // -------------------------------------------------------------
    val scenarios = listOf(
        "உள்ளூர் ஆட்டோ & வாடகை வண்டி (Auto & Cab)",
        "உள்ளூர் கடைகள் & சந்தை (Kirana & Market)",
        "தெரு வழிகேட்டல் (Local Directions & Landmarks)",
        "டீக்கடை & உணவகம் (Tea Stall & Cafe)",
        "குடியிருப்பு சங்கம் (Neighborhood Society)",
        "தினசரி அறிமுகம் (Daily Chitchat)",
        "வேலை நேர்முகத் தேர்வு (Job Interview)",
        "அலுவலக ஆங்கிலம் (Office Talk)",
        "இலக்கண சந்தேகங்கள் (Grammar Doubts)"
    )

    private val _selectedScenario = MutableStateFlow(scenarios[0])
    val selectedScenario: StateFlow<String> = _selectedScenario.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = ChatSender.TEACHER,
                englishText = "Vanakkam! I am Pavi Teacher. I am here to help you speak fluent English with confidence. How can I assist you today?",
                tamilTranslation = "வணக்கம்! நான் உங்கள் பவி டீச்சர். நீங்கள் எவ்வித தயக்கமுமின்றி சரளமாக ஆங்கிலம் பேச நான் உதவுவேன். இன்று நாம் எதைப் பற்றி பேசலாம்?",
                grammarTipTamil = "ஆங்கிலத்தில் பேசும்போது வாக்கியத்தின் தொடக்கத்தை எப்போதும் தெளிவாக வைத்திருங்கள்.",
                pronunciationTipTamil = "'Fluently' - ஃப்ளூ-வன்ட்-லி (F ஒலியை உதட்டில் பல் படும்படி சொல்லவும்)."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTeacherThinking = MutableStateFlow(false)
    val isTeacherThinking: StateFlow<Boolean> = _isTeacherThinking.asStateFlow()

    private val _manualInputText = MutableStateFlow("")
    val manualInputText: StateFlow<String> = _manualInputText.asStateFlow()

    // -------------------------------------------------------------
    // GRAMMAR PRACTICE STATE
    // -------------------------------------------------------------
    private val _grammarQuestions = MutableStateFlow(CurriculumData.grammarExercises)
    val grammarQuestions: StateFlow<List<GrammarQuestion>> = _grammarQuestions.asStateFlow()

    private val _currentGrammarIndex = MutableStateFlow(0)
    val currentGrammarIndex: StateFlow<Int> = _currentGrammarIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _hasSubmittedGrammar = MutableStateFlow(false)
    val hasSubmittedGrammar: StateFlow<Boolean> = _hasSubmittedGrammar.asStateFlow()

    private val _grammarScore = MutableStateFlow(0)
    val grammarScore: StateFlow<Int> = _grammarScore.asStateFlow()

    // -------------------------------------------------------------
    // PRONUNCIATION LAB STATE
    // -------------------------------------------------------------
    private val _pronunciationExercises = MutableStateFlow(CurriculumData.pronunciationExercises)
    val pronunciationExercises: StateFlow<List<PronunciationExercise>> = _pronunciationExercises.asStateFlow()

    private val _currentPronIndex = MutableStateFlow(0)
    val currentPronIndex: StateFlow<Int> = _currentPronIndex.asStateFlow()

    private val _pronFeedback = MutableStateFlow<PronunciationFeedback?>(null)
    val pronFeedback: StateFlow<PronunciationFeedback?> = _pronFeedback.asStateFlow()

    private val _isEvaluatingPron = MutableStateFlow(false)
    val isEvaluatingPron: StateFlow<Boolean> = _isEvaluatingPron.asStateFlow()

    init {
        // Automatically speak welcome greeting in English
        speakTeacherEnglish("Vanakkam! I am Pavi Teacher. Let us learn English together.")
    }

    fun initSpeechHelper(context: Context) {
        if (speechRecognizerHelper == null) {
            speechRecognizerHelper = SpeechRecognitionHelper(
                context = context,
                onResult = { recognized ->
                    _liveSpokenText.value = recognized
                    when (_currentTab.value) {
                        AppTab.VOICE_CLASS -> handleUserVoiceMessage(recognized)
                        AppTab.PRONUNCIATION -> evaluatePronunciationVoice(recognized)
                        else -> {}
                    }
                },
                onError = { error ->
                    _isMicListening.value = false
                }
            )
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        stopAudio()
    }

    fun setScenario(scenario: String) {
        _selectedScenario.value = scenario
        val prompt = "Let's practice: $scenario"
        sendMessageToTeacher(prompt, isSpoken = false)
    }

    fun updateManualInput(text: String) {
        _manualInputText.value = text
    }

    fun sendManualMessage() {
        val text = _manualInputText.value.trim()
        if (text.isNotBlank()) {
            _manualInputText.value = ""
            sendMessageToTeacher(text, isSpoken = false)
        }
    }

    private fun handleUserVoiceMessage(spokenText: String) {
        _isMicListening.value = false
        if (spokenText.isNotBlank()) {
            sendMessageToTeacher(spokenText, isSpoken = true)
        }
    }

    private fun sendMessageToTeacher(userText: String, isSpoken: Boolean) {
        val userMsg = ChatMessage(
            sender = ChatSender.USER,
            englishText = userText,
            tamilTranslation = "நீங்கள் பேசியது: '$userText'"
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isTeacherThinking.value = true
            val teacherReply = aiService.getTeacherResponse(
                userMessage = userText,
                scenario = _selectedScenario.value,
                recentMessages = _chatMessages.value,
                feedbackLanguage = _feedbackLanguage.value
            )
            _isTeacherThinking.value = false
            _chatMessages.value = _chatMessages.value + teacherReply

            // Record session in Room
            repository.recordVoiceSession(
                topic = _selectedScenario.value,
                score = 90,
                summaryTamil = teacherReply.tamilTranslation,
                summaryEnglish = teacherReply.englishText
            )

            // Deliver instructional audio feedback according to selected feedback language
            val isTamil = _feedbackLanguage.value == com.example.data.model.FeedbackLanguage.TAMIL
            val guidanceText = if (isTamil) {
                teacherReply.tamilTranslation
            } else {
                teacherReply.grammarTipTamil ?: "Listen carefully and practice saying this sentence."
            }
            ttsManager.speakBilingualInstruction(
                englishPhrase = teacherReply.englishText,
                instructionPrompt = guidanceText,
                isTamilInstruction = isTamil
            )
        }
    }

    fun toggleFeedbackLanguage() {
        if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.TAMIL) {
            _feedbackLanguage.value = com.example.data.model.FeedbackLanguage.ENGLISH
            speakTeacherEnglish("Feedback language switched to English. Pavi will now guide you in English.")
        } else {
            _feedbackLanguage.value = com.example.data.model.FeedbackLanguage.TAMIL
            speakTeacherTamil("விளக்க மொழி தமிழுக்கு மாற்றப்பட்டது. பவி டீச்சர் தமிழில் விளக்குவார்.")
        }
    }

    fun setFeedbackLanguage(lang: com.example.data.model.FeedbackLanguage) {
        _feedbackLanguage.value = lang
        if (lang == com.example.data.model.FeedbackLanguage.ENGLISH) {
            speakTeacherEnglish("Feedback language set to English.")
        } else {
            speakTeacherTamil("விளக்க மொழி தமிழ்.")
        }
    }

    fun toggleMicListening(forPronunciation: Boolean = false) {
        if (_isMicListening.value) {
            speechRecognizerHelper?.stopListening()
            _isMicListening.value = false
            _audioRms.value = 0f
        } else {
            stopAudio()
            _liveSpokenText.value = ""
            _isMicListening.value = true
            speechRecognizerHelper?.startListening("en-US")

            // Collect RMS for animation
            viewModelScope.launch {
                speechRecognizerHelper?.audioRms?.collect { rms ->
                    _audioRms.value = rms
                }
            }
        }
    }

    fun speakTeacherEnglish(text: String, slow: Boolean = false) {
        ttsManager.speakEnglish(text, slowSpeed = slow)
    }

    fun speakTeacherTamil(text: String) {
        ttsManager.speakTamil(text)
    }

    fun stopAudio() {
        ttsManager.stop()
        speechRecognizerHelper?.stopListening()
        _isMicListening.value = false
        _audioRms.value = 0f
    }

    // -------------------------------------------------------------
    // GRAMMAR HANDLERS
    // -------------------------------------------------------------
    fun selectGrammarOption(index: Int) {
        if (!_hasSubmittedGrammar.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun submitGrammarAnswer() {
        val selected = _selectedOptionIndex.value ?: return
        val currentQ = _grammarQuestions.value.getOrNull(_currentGrammarIndex.value) ?: return

        _hasSubmittedGrammar.value = true
        val isCorrect = selected == currentQ.correctIndex
        if (isCorrect) {
            _grammarScore.value += 1
            if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                speakTeacherEnglish("Excellent! That is the correct answer.")
            } else {
                speakTeacherTamil("மிக அருமை! சரியான விடை.")
            }
        } else {
            if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                speakTeacherEnglish("Incorrect. Please read the explanation carefully.")
            } else {
                speakTeacherTamil("தவறான விடை. விளக்கத்தைக் கவனியுங்கள்.")
            }
        }

        viewModelScope.launch {
            repository.recordGrammarResult(
                isCorrect = isCorrect,
                category = currentQ.category,
                mistakeTitle = currentQ.category,
                mistakeSentence = currentQ.options[selected],
                correction = currentQ.options[currentQ.correctIndex],
                adviceTamil = currentQ.explanationTamil
            )
        }
    }

    fun nextGrammarQuestion() {
        val nextIdx = (_currentGrammarIndex.value + 1) % _grammarQuestions.value.size
        _currentGrammarIndex.value = nextIdx
        _selectedOptionIndex.value = null
        _hasSubmittedGrammar.value = false
    }

    // -------------------------------------------------------------
    // PRONUNCIATION HANDLERS
    // -------------------------------------------------------------
    private fun evaluatePronunciationVoice(spokenText: String) {
        val currentEx = _pronunciationExercises.value.getOrNull(_currentPronIndex.value) ?: return
        _isEvaluatingPron.value = true

        viewModelScope.launch {
            val feedback = aiService.evaluatePronunciation(
                targetPhrase = currentEx.phrase,
                spokenTranscript = spokenText
            )
            _pronFeedback.value = feedback
            _isEvaluatingPron.value = false

            // Save in database
            repository.recordPronunciationResult(
                phrase = currentEx.phrase,
                score = feedback.overallScore,
                feedbackTamil = feedback.praiseTamil,
                adviceTamil = feedback.improvementTamil
            )

            // Voice feedback adapting to chosen language
            if (feedback.overallScore >= 80) {
                if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                    speakTeacherEnglish("Awesome! Accuracy is ${feedback.overallScore} percent. Native-like pronunciation!")
                } else {
                    speakTeacherTamil("அருமை! துல்லியம் ${feedback.overallScore} சதவீதம். ${feedback.praiseTamil}")
                }
            } else {
                if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                    speakTeacherEnglish("Good attempt! Accuracy is ${feedback.overallScore} percent. Listen to the slow audio guide and try once more.")
                } else {
                    speakTeacherTamil("நல்ல முயற்சி! துல்லியம் ${feedback.overallScore} சதவீதம். ${feedback.improvementTamil}")
                }
            }
        }
    }

    fun manualPronunciationTest(simulatedSpoken: String) {
        evaluatePronunciationVoice(simulatedSpoken)
    }

    fun nextPronunciationExercise() {
        val nextIdx = (_currentPronIndex.value + 1) % _pronunciationExercises.value.size
        _currentPronIndex.value = nextIdx
        _pronFeedback.value = null
        _liveSpokenText.value = ""
    }

    fun resolveWeakSpot(id: Int) {
        viewModelScope.launch {
            repository.markWeakSpotResolved(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizerHelper?.destroy()
    }
}
