package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TamizhaApp
import com.example.data.model.DailyGrammarExerciseEntity
import com.example.data.model.DailyGrammarModuleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.ExperimentalCoroutinesApi

data class GrammarOverallStats(
    val totalModules: Int = 0,
    val completedModules: Int = 0,
    val totalExercises: Int = 0,
    val completedExercises: Int = 0,
    val overallPercentage: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class GrammarViewModel : ViewModel() {

    private val app = TamizhaApp.instance
    private val repository = app.repository
    private val ttsManager = app.ttsManager

    // Daily Modules from Room
    val modules: StateFlow<List<DailyGrammarModuleEntity>> = repository.dailyModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Progress calculation
    val overallStats: StateFlow<GrammarOverallStats> = modules.combine(MutableStateFlow(Unit)) { modList, _ ->
        val totalMods = modList.size
        val completedMods = modList.count { it.isCompleted }
        val totalEx = modList.sumOf { it.totalExercises }
        val compEx = modList.sumOf { it.completedExercises }
        val avgScore = if (completedMods > 0) {
            modList.filter { it.isCompleted }.map { it.scorePercentage }.average().toInt()
        } else if (compEx > 0) {
            val totalCorrect = modList.sumOf { it.correctAnswersCount }
            ((totalCorrect.toFloat() / compEx.toFloat()) * 100).toInt()
        } else 0

        GrammarOverallStats(
            totalModules = totalMods,
            completedModules = completedMods,
            totalExercises = totalEx,
            completedExercises = compEx,
            overallPercentage = avgScore
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GrammarOverallStats())

    // Active Module State
    private val _selectedModuleId = MutableStateFlow<String?>(null)
    val selectedModuleId: StateFlow<String?> = _selectedModuleId.asStateFlow()

    val currentModule: StateFlow<DailyGrammarModuleEntity?> = _selectedModuleId
        .flatMapLatest { id ->
            if (id != null) repository.getModuleById(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentModuleExercises: StateFlow<List<DailyGrammarExerciseEntity>> = _selectedModuleId
        .flatMapLatest { id ->
            if (id != null) repository.getExercisesForModule(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Interactive Quiz / Exercise Runner State
    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _hasCheckedAnswer = MutableStateFlow(false)
    val hasCheckedAnswer: StateFlow<Boolean> = _hasCheckedAnswer.asStateFlow()

    private val _isModuleCelebration = MutableStateFlow(false)
    val isModuleCelebration: StateFlow<Boolean> = _isModuleCelebration.asStateFlow()

    fun openModule(moduleId: String) {
        _selectedModuleId.value = moduleId
        _currentExerciseIndex.value = 0
        _selectedOption.value = null
        _hasCheckedAnswer.value = false
        _isModuleCelebration.value = false

        // Check if there is an unattempted exercise to resume at
        viewModelScope.launch {
            val exercises = repository.getExercisesForModule(moduleId)
            exercises.collect { list ->
                if (_selectedModuleId.value == moduleId && !_hasCheckedAnswer.value) {
                    val firstUnattempted = list.indexOfFirst { !it.isAttempted }
                    if (firstUnattempted >= 0) {
                        _currentExerciseIndex.value = firstUnattempted
                    }
                }
            }
        }
    }

    fun backToModules() {
        _selectedModuleId.value = null
        _selectedOption.value = null
        _hasCheckedAnswer.value = false
        _isModuleCelebration.value = false
        ttsManager.stop()
    }

    fun selectOption(optionIndex: Int) {
        if (!_hasCheckedAnswer.value) {
            _selectedOption.value = optionIndex
        }
    }

    // Feedback Delivery Language
    private val _feedbackLanguage = MutableStateFlow(com.example.data.model.FeedbackLanguage.TAMIL)
    val feedbackLanguage: StateFlow<com.example.data.model.FeedbackLanguage> = _feedbackLanguage.asStateFlow()

    fun toggleFeedbackLanguage() {
        if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.TAMIL) {
            _feedbackLanguage.value = com.example.data.model.FeedbackLanguage.ENGLISH
            ttsManager.speakEnglish("Grammar explanation language set to English.")
        } else {
            _feedbackLanguage.value = com.example.data.model.FeedbackLanguage.TAMIL
            ttsManager.speakTamil("இலக்கண விளக்க மொழி தமிழ்.")
        }
    }

    fun setFeedbackLanguage(lang: com.example.data.model.FeedbackLanguage) {
        _feedbackLanguage.value = lang
    }

    fun checkAnswer() {
        val selected = _selectedOption.value ?: return
        val currentMod = currentModule.value ?: return
        val exercises = currentModuleExercises.value
        val currentEx = exercises.getOrNull(_currentExerciseIndex.value) ?: return

        _hasCheckedAnswer.value = true
        val isCorrect = selected == currentEx.correctOptionIndex

        if (isCorrect) {
            if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                ttsManager.speakEnglish("Correct answer! ${currentEx.explanationEnglish}")
            } else {
                ttsManager.speakTamil("சரியான விடை! ${currentEx.explanationTamil}")
            }
        } else {
            if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                ttsManager.speakEnglish("Incorrect. ${currentEx.explanationEnglish}")
            } else {
                ttsManager.speakTamil("தவறான விடை. ${currentEx.explanationTamil}")
            }
        }

        viewModelScope.launch {
            repository.recordModuleExerciseAnswer(
                moduleId = currentMod.moduleId,
                exerciseId = currentEx.exerciseId,
                selectedOption = selected,
                isCorrect = isCorrect,
                explanationTamil = currentEx.explanationTamil
            )
        }
    }

    fun nextExercise() {
        val exercises = currentModuleExercises.value
        val nextIdx = _currentExerciseIndex.value + 1

        if (nextIdx < exercises.size) {
            _currentExerciseIndex.value = nextIdx
            _selectedOption.value = null
            _hasCheckedAnswer.value = false
        } else {
            // Module finished!
            _isModuleCelebration.value = true
            if (_feedbackLanguage.value == com.example.data.model.FeedbackLanguage.ENGLISH) {
                ttsManager.speakEnglish("Congratulations! You have completed this grammar module successfully.")
            } else {
                ttsManager.speakTamil("வாழ்த்துகள்! இந்த இலக்கணத் தொகுதியை வெற்றிகரமாக முடித்துவிட்டீர்கள்.")
            }
        }
    }

    fun retakeModule() {
        val modId = _selectedModuleId.value ?: return
        viewModelScope.launch {
            repository.resetModule(modId)
            _currentExerciseIndex.value = 0
            _selectedOption.value = null
            _hasCheckedAnswer.value = false
            _isModuleCelebration.value = false
        }
    }

    fun speakEnglish(text: String) {
        ttsManager.speakEnglish(text)
    }

    fun speakTamil(text: String) {
        ttsManager.speakTamil(text)
    }
}
