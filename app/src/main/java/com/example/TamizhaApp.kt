package com.example

import android.app.Application
import androidx.room.Room
import com.example.ai.GeminiTeacherService
import com.example.audio.TtsManager
import com.example.data.db.AppDatabase
import com.example.data.repository.TamizhaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TamizhaApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: TamizhaRepository
        private set

    lateinit var ttsManager: TtsManager
        private set

    lateinit var aiService: GeminiTeacherService
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tamizha_english_db"
        ).fallbackToDestructiveMigration(true).build()

        repository = TamizhaRepository(
            database.userProgressDao(),
            database.practiceSessionDao(),
            database.weakSpotDao(),
            database.grammarDao()
        )

        ttsManager = TtsManager(this)
        aiService = GeminiTeacherService()

        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeDefaultData()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
    }

    companion object {
        lateinit var instance: TamizhaApp
            private set
    }
}
