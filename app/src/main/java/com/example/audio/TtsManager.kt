package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpokenText = MutableStateFlow<String?>(null)
    val currentSpokenText: StateFlow<String?> = _currentSpokenText.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    private val utteranceCallbacks = java.util.concurrent.ConcurrentHashMap<String, () -> Unit>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSpokenText.value = null
                    if (utteranceId != null) {
                        val callback = utteranceCallbacks.remove(utteranceId)
                        callback?.invoke()
                    }
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSpokenText.value = null
                    if (utteranceId != null) {
                        utteranceCallbacks.remove(utteranceId)
                    }
                }
            })
        } else {
            Log.e("TtsManager", "TTS initialization failed with status: $status")
        }
    }

    fun speakEnglish(text: String, slowSpeed: Boolean = false, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) return
        stop()

        val result = tts?.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.getDefault())
        }

        tts?.setSpeechRate(if (slowSpeed) 0.72f else 0.92f)
        tts?.setPitch(1.02f)

        _currentSpokenText.value = text
        val utteranceId = "en_${System.currentTimeMillis()}"
        if (onDone != null) {
            utteranceCallbacks[utteranceId] = onDone
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun speakTamil(text: String, slowSpeed: Boolean = false, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) return
        stop()

        // Try Tamil locale (ta_IN)
        val tamilLocale = Locale.Builder().setLanguage("ta").setRegion("IN").build()
        val result = tts?.setLanguage(tamilLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // If Tamil voice is missing on device, fallback cleanly to default/Indian English
            val inLocale = Locale.Builder().setLanguage("en").setRegion("IN").build()
            tts?.setLanguage(inLocale)
        }

        tts?.setSpeechRate(if (slowSpeed) 0.8f else 0.95f)
        tts?.setPitch(1.05f)

        _currentSpokenText.value = text
        val utteranceId = "ta_${System.currentTimeMillis()}"
        if (onDone != null) {
            utteranceCallbacks[utteranceId] = onDone
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun speakBilingualInstruction(
        englishPhrase: String,
        instructionPrompt: String,
        isTamilInstruction: Boolean
    ) {
        if (instructionPrompt.isBlank()) {
            speakEnglish(englishPhrase)
            return
        }
        speakEnglish(englishPhrase, slowSpeed = false) {
            if (isTamilInstruction) {
                speakTamil(instructionPrompt)
            } else {
                speakEnglish(instructionPrompt)
            }
        }
    }

    fun stop() {
        utteranceCallbacks.clear()
        tts?.stop()
        _isSpeaking.value = false
        _currentSpokenText.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
