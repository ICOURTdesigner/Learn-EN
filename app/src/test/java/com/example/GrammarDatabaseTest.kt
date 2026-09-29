package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.db.GrammarDao
import com.example.data.model.DailyGrammarExerciseEntity
import com.example.data.model.DailyGrammarModuleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GrammarDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var grammarDao: GrammarDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        grammarDao = db.grammarDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveDailyGrammarModules() = runBlocking {
        val module = DailyGrammarModuleEntity(
            moduleId = "module_test_1",
            dayNumber = 1,
            titleEnglish = "Test Tenses",
            titleTamil = "நாள் 1: காலங்கள்",
            descriptionTamil = "விளக்கம்",
            category = "Tenses",
            totalExercises = 2,
            completedExercises = 0,
            correctAnswersCount = 0,
            isCompleted = false,
            scorePercentage = 0
        )

        grammarDao.insertModules(listOf(module))

        val modules = grammarDao.getAllModules().first()
        assertEquals(1, modules.size)
        assertEquals("module_test_1", modules[0].moduleId)
        assertEquals("Test Tenses", modules[0].titleEnglish)
    }

    @Test
    fun recordExerciseAnswerAndModuleProgress() = runBlocking {
        val module = DailyGrammarModuleEntity(
            moduleId = "module_day_1",
            dayNumber = 1,
            titleEnglish = "Common Traps",
            titleTamil = "பொதுவான தவறுகள்",
            descriptionTamil = "விளக்கம்",
            category = "Common Traps",
            totalExercises = 1,
            completedExercises = 0,
            correctAnswersCount = 0,
            isCompleted = false,
            scorePercentage = 0
        )
        val exercise = DailyGrammarExerciseEntity(
            exerciseId = 101,
            moduleId = "module_day_1",
            orderIndex = 1,
            questionPromptEnglish = "I ______ two brothers.",
            questionTamilContext = "எனக்கு இரண்டு சகோதரர்கள் இருக்கிறார்கள்:",
            optionA = "am having",
            optionB = "have",
            optionC = "had have",
            optionD = "having",
            correctOptionIndex = 1,
            explanationTamil = "have வர வேண்டும்",
            explanationEnglish = "Use simple present have"
        )

        grammarDao.insertModules(listOf(module))
        grammarDao.insertExercises(listOf(exercise))

        // Record answer
        grammarDao.recordExerciseAnswer(
            exerciseId = 101,
            selectedOption = 1,
            isCorrect = true,
            timestamp = System.currentTimeMillis()
        )

        // Update module progress
        grammarDao.updateModuleProgress(
            moduleId = "module_day_1",
            completedCount = 1,
            correctCount = 1,
            scorePercent = 100,
            isCompleted = true,
            timestamp = System.currentTimeMillis()
        )

        val updatedModule = grammarDao.getModuleById("module_day_1").first()
        assertTrue(updatedModule?.isCompleted == true)
        assertEquals(100, updatedModule?.scorePercentage)
        assertEquals(1, updatedModule?.completedExercises)

        val updatedExercises = grammarDao.getExercisesForModule("module_day_1").first()
        assertEquals(1, updatedExercises[0].userSelectedOption)
        assertEquals(true, updatedExercises[0].isAnsweredCorrectly)
    }
}
