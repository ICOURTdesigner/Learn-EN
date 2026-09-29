package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.UserProgressEntity
import com.example.data.model.WeakSpotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET dayStreak = dayStreak + 1, lastActiveDate = :now WHERE id = 1")
    suspend fun incrementStreak(now: Long)

    @Query("UPDATE user_progress SET totalGrammarAnswered = totalGrammarAnswered + 1, totalGrammarCorrect = totalGrammarCorrect + :correctDelta WHERE id = 1")
    suspend fun recordGrammarResult(correctDelta: Int)

    @Query("UPDATE user_progress SET totalPronunciationsDone = totalPronunciationsDone + 1, avgPronunciationScore = ((avgPronunciationScore * totalPronunciationsDone) + :newScore) / (totalPronunciationsDone + 1) WHERE id = 1")
    suspend fun recordPronunciationResult(newScore: Float)
}

@Dao
interface PracticeSessionDao {
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentSessions(): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity)
}

@Dao
interface WeakSpotDao {
    @Query("SELECT * FROM weak_spots ORDER BY frequency DESC, lastDetected DESC")
    fun getAllWeakSpots(): Flow<List<WeakSpotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeakSpot(weakSpot: WeakSpotEntity)

    @Query("UPDATE weak_spots SET isResolved = 1 WHERE id = :id")
    suspend fun markResolved(id: Int)
}

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_modules ORDER BY dayNumber ASC")
    fun getAllModules(): Flow<List<com.example.data.model.DailyGrammarModuleEntity>>

    @Query("SELECT * FROM grammar_modules WHERE moduleId = :moduleId LIMIT 1")
    fun getModuleById(moduleId: String): Flow<com.example.data.model.DailyGrammarModuleEntity?>

    @Query("SELECT * FROM grammar_exercises WHERE moduleId = :moduleId ORDER BY orderIndex ASC")
    fun getExercisesForModule(moduleId: String): Flow<List<com.example.data.model.DailyGrammarExerciseEntity>>

    @Query("SELECT COUNT(*) FROM grammar_modules")
    suspend fun getModulesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<com.example.data.model.DailyGrammarModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<com.example.data.model.DailyGrammarExerciseEntity>)

    @Query("UPDATE grammar_exercises SET userSelectedOption = :selectedOption, isAnsweredCorrectly = :isCorrect, isAttempted = 1, answeredTimestamp = :timestamp WHERE exerciseId = :exerciseId")
    suspend fun recordExerciseAnswer(exerciseId: Int, selectedOption: Int, isCorrect: Boolean, timestamp: Long)

    @Query("UPDATE grammar_modules SET completedExercises = :completedCount, correctAnswersCount = :correctCount, scorePercentage = :scorePercent, isCompleted = :isCompleted, lastAttemptedTimestamp = :timestamp WHERE moduleId = :moduleId")
    suspend fun updateModuleProgress(moduleId: String, completedCount: Int, correctCount: Int, scorePercent: Int, isCompleted: Boolean, timestamp: Long)

    @Query("UPDATE grammar_exercises SET userSelectedOption = NULL, isAnsweredCorrectly = NULL, isAttempted = 0, answeredTimestamp = NULL WHERE moduleId = :moduleId")
    suspend fun resetModuleExercises(moduleId: String)

    @Query("UPDATE grammar_modules SET completedExercises = 0, correctAnswersCount = 0, scorePercentage = 0, isCompleted = 0 WHERE moduleId = :moduleId")
    suspend fun resetModuleEntity(moduleId: String)
}

@Database(
    entities = [
        UserProgressEntity::class,
        PracticeSessionEntity::class,
        WeakSpotEntity::class,
        com.example.data.model.DailyGrammarModuleEntity::class,
        com.example.data.model.DailyGrammarExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun weakSpotDao(): WeakSpotDao
    abstract fun grammarDao(): GrammarDao
}
