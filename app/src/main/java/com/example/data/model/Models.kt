package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val dayStreak: Int = 3,
    val totalPracticeMinutes: Int = 45,
    val totalGrammarAnswered: Int = 28,
    val totalGrammarCorrect: Int = 24,
    val totalPronunciationsDone: Int = 19,
    val avgPronunciationScore: Float = 86.5f,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val confidenceLevelTamil: String = "ஆரம்ப நிலை (Beginner to Intermediate)"
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionType: String, // "VOICE_CLASS", "GRAMMAR", "PRONUNCIATION"
    val topicTitle: String,
    val score: Int, // 0 - 100
    val summaryTamil: String,
    val summaryEnglish: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weak_spots")
data class WeakSpotEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "GRAMMAR", "PRONUNCIATION", "VOCABULARY"
    val title: String,
    val mistakePattern: String,
    val correction: String,
    val adviceTamil: String,
    val frequency: Int = 1,
    val isResolved: Boolean = false,
    val lastDetected: Long = System.currentTimeMillis()
)

@Entity(tableName = "grammar_modules")
data class DailyGrammarModuleEntity(
    @PrimaryKey val moduleId: String,
    val dayNumber: Int,
    val titleEnglish: String,
    val titleTamil: String,
    val descriptionTamil: String,
    val category: String, // "Tenses", "Prepositions", "Tamil Common Traps", "Workplace English"
    val level: String = "ஆரம்ப நிலை",
    val totalExercises: Int = 3,
    val completedExercises: Int = 0,
    val correctAnswersCount: Int = 0,
    val isCompleted: Boolean = false,
    val scorePercentage: Int = 0,
    val lastAttemptedTimestamp: Long? = null
)

@Entity(tableName = "grammar_exercises")
data class DailyGrammarExerciseEntity(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    val moduleId: String,
    val orderIndex: Int,
    val questionPromptEnglish: String,
    val questionTamilContext: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val explanationTamil: String,
    val explanationEnglish: String,
    val userSelectedOption: Int? = null,
    val isAnsweredCorrectly: Boolean? = null,
    val isAttempted: Boolean = false,
    val answeredTimestamp: Long? = null
) {
    fun getOptionsList(): List<String> = listOf(optionA, optionB, optionC, optionD)
}

// UI and Interactive Models

enum class FeedbackLanguage(val displayName: String, val labelTamil: String, val promptName: String) {
    TAMIL("தமிழ்", "தமிழ் விளக்கம்", "Tamil"),
    ENGLISH("English", "English Feedback", "English")
}

enum class ChatSender {
    USER,
    TEACHER
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: ChatSender,
    val englishText: String,
    val tamilTranslation: String,
    val grammarTipTamil: String? = null,
    val pronunciationTipTamil: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class GrammarQuestion(
    val id: Int,
    val category: String, // "Tenses", "Prepositions", "Daily Talk", "Tamil Common Errors"
    val questionPromptEnglish: String,
    val questionTamilContext: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationTamil: String,
    val explanationEnglish: String
)

data class WordAccuracy(
    val word: String,
    val isCorrect: Boolean,
    val phoneticTip: String? = null
)

data class PronunciationExercise(
    val id: String,
    val phrase: String,
    val tamilMeaning: String,
    val phoneticTamil: String,
    val soundFocus: String, // e.g., "V vs W sound", "Th sound", "Silent letters"
    val commonTamilMistakeNotice: String
)

data class PronunciationFeedback(
    val overallScore: Int, // 0 to 100
    val recognizedText: String,
    val praiseTamil: String,
    val improvementTamil: String,
    val wordAccuracies: List<WordAccuracy>
)
