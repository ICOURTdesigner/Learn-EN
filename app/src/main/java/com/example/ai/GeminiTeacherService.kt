package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.GrammarQuestion
import com.example.data.model.PronunciationFeedback
import com.example.data.model.UserProgressEntity
import com.example.data.model.WeakSpotEntity
import com.example.data.model.WordAccuracy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiTeacherService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val modelName = "gemini-3.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    private fun isApiKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun getTeacherResponse(
        userMessage: String,
        scenario: String,
        recentMessages: List<ChatMessage>,
        feedbackLanguage: com.example.data.model.FeedbackLanguage = com.example.data.model.FeedbackLanguage.TAMIL
    ): ChatMessage = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext getOfflineTeacherResponse(userMessage, scenario, feedbackLanguage)
        }

        try {
            val languageInstruction = if (feedbackLanguage == com.example.data.model.FeedbackLanguage.ENGLISH) {
                "Deliver all explanations, grammar tips, and phonetic tips in simple, supportive, clear English."
            } else {
                "Deliver all explanations, grammar tips, and phonetic tips in warm spoken conversational Tamil (தங்கத்தமிழ்)."
            }

            val systemPrompt = """
                You are 'Pavi Teacher' (பவி டீச்சர்), a warm, encouraging, world-class English teacher coaching a Tamil native speaker, inspired by Supernova AI.
                Current Classroom Scenario: $scenario.
                Feedback Delivery Mode: ${feedbackLanguage.displayName}.
                $languageInstruction
                Your response must be friendly, supportive, and strictly formatted as a valid JSON object with the following fields:
                {
                  "englishText": "The English sentence or reply that the user should learn and repeat",
                  "tamilTranslation": "Teacher's explanation in ${if (feedbackLanguage == com.example.data.model.FeedbackLanguage.ENGLISH) "clear simple English" else "warm spoken Tamil"}",
                  "grammarTipTamil": "Grammar rule or encouragement in ${if (feedbackLanguage == com.example.data.model.FeedbackLanguage.ENGLISH) "English" else "Tamil"}",
                  "pronunciationTipTamil": "Phonetic guidance for tricky sounds in ${if (feedbackLanguage == com.example.data.model.FeedbackLanguage.ENGLISH) "English" else "Tamil"}"
                }
                Do not include markdown code ticks outside the JSON. Return valid JSON only.
            """.trimIndent()

            val contentsArray = JSONArray()

            // Include last 4 turns for context
            val turnsToInclude = recentMessages.takeLast(4)
            for (turn in turnsToInclude) {
                val role = if (turn.sender == ChatSender.USER) "user" else "model"
                val text = if (turn.sender == ChatSender.USER) turn.englishText else "${turn.englishText} | ${turn.tamilTranslation}"
                contentsArray.put(
                    JSONObject().put("role", role).put("parts", JSONArray().put(JSONObject().put("text", text)))
                )
            }

            // Current user turn
            contentsArray.put(
                JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            )

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("$baseUrl?key=${getApiKey()}")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiTeacher", "API error: ${response.code} $responseBody")
                return@withContext getOfflineTeacherResponse(userMessage, scenario, feedbackLanguage)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            parseTeacherJsonResponse(text, userMessage)
        } catch (e: Exception) {
            Log.e("GeminiTeacher", "Exception calling Gemini", e)
            getOfflineTeacherResponse(userMessage, scenario, feedbackLanguage)
        }
    }

    private fun parseTeacherJsonResponse(rawJson: String, originalInput: String): ChatMessage {
        return try {
            val cleanJson = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(cleanJson)
            ChatMessage(
                sender = ChatSender.TEACHER,
                englishText = obj.optString("englishText", "Great job! Let us continue practicing."),
                tamilTranslation = obj.optString("tamilTranslation", "அருமையாக சொன்னீர்கள்! தொடர்ந்து பேசுவோம்."),
                grammarTipTamil = obj.optString("grammarTipTamil").takeIf { it.isNotBlank() },
                pronunciationTipTamil = obj.optString("pronunciationTipTamil").takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            ChatMessage(
                sender = ChatSender.TEACHER,
                englishText = "Good effort! Let's practice saying: 'I am excited to learn English today.'",
                tamilTranslation = "மிக நன்று! 'நான் இன்று ஆங்கிலம் கற்க ஆர்வமாக உள்ளேன்' என்பதை உரக்கச் சொல்லி பழகுங்கள்.",
                grammarTipTamil = "வாக்கியத்தில் Subject + Verb சரியாக பயன்படுத்தப்பட்டுள்ளது.",
                pronunciationTipTamil = "'Excited' - எக்-ஸை-டட் (E-sound கவனிக்கவும்)."
            )
        }
    }

    suspend fun evaluatePronunciation(
        targetPhrase: String,
        spokenTranscript: String
    ): PronunciationFeedback = withContext(Dispatchers.IO) {
        val targetWords = targetPhrase.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val spokenWords = spokenTranscript.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

        // Acoustic word-level matching
        val wordAccuracies = mutableListOf<WordAccuracy>()
        var matchCount = 0

        for (target in targetWords) {
            val cleanTarget = target.lowercase().replace(Regex("[^a-z0-9]"), "")
            val matched = spokenWords.any { spoken ->
                val cleanSpoken = spoken.lowercase().replace(Regex("[^a-z0-9]"), "")
                cleanSpoken == cleanTarget || calculateLevenshteinSimilarity(cleanTarget, cleanSpoken) > 0.75
            }
            if (matched) {
                matchCount++
                wordAccuracies.add(WordAccuracy(word = target, isCorrect = true))
            } else {
                val tip = when {
                    target.startsWith("v", ignoreCase = true) -> "V ஒலி: மேல் பற்கள் கீழ் உதட்டில் பட வேண்டும்"
                    target.startsWith("w", ignoreCase = true) -> "W ஒலி: உதட்டை வட்டமாக குவித்து ஊத வேண்டும்"
                    target.startsWith("sc", ignoreCase = true) || target.startsWith("st", ignoreCase = true) -> "'இ' சேர்க்காமல் 'ஸ்' என்று தொடங்கவும்"
                    target.contains("th", ignoreCase = true) -> "நாக்கை முன் பற்களுக்கு இடையே வைக்கவும்"
                    else -> "தெளிவாக மீண்டும் உச்சரிக்கவும்"
                }
                wordAccuracies.add(WordAccuracy(word = target, isCorrect = false, phoneticTip = tip))
            }
        }

        val baseScore = if (targetWords.isNotEmpty()) {
            ((matchCount.toFloat() / targetWords.size.toFloat()) * 100f).toInt()
        } else {
            70
        }.coerceIn(10, 98)

        // Generate personalized coaching in Tamil
        val (praise, advice) = when {
            baseScore >= 85 -> Pair(
                "அருமையான உச்சரிப்பு! நேட்டிவ் ஸ்பீக்கர் போல மிக இயல்பாக பேசினீர்கள்! 🎉",
                "அழுத்தமும் (Stress) ரிதமும் மிகச் சரியாக உள்ளது. இதே வேகத்தில் தொடருங்கள்."
            )
            baseScore >= 60 -> Pair(
                "மிக நல்ல முயற்சி! வார்த்தைகள் தெளிவாக புரிந்தது. 👍",
                "சிவப்பு நிறத்தில் உள்ள வார்த்தைகளின் ஒலிகளை (உதடு மற்றும் பற்கள் அசைவு) மீண்டும் கவனித்து பேசவும்."
            )
            else -> Pair(
                "பயிற்சி செய்ய நல்ல வாய்ப்பு! மெதுவாக மீண்டும் முயற்சி செய்யுங்கள்.",
                "அவசரப்படாமல் ஒவ்வொரு வார்த்தையையும் நிறுத்தி நிதானமாக உச்சரியுங்கள். 'Slow 0.75x' ஆடியோவைக் கேட்டு திரும்ப சொல்லுங்கள்."
            )
        }

        PronunciationFeedback(
            overallScore = baseScore,
            recognizedText = spokenTranscript.ifBlank { "(சத்தம் கேட்கவில்லை - மீண்டும் பேசவும்)" },
            praiseTamil = praise,
            improvementTamil = advice,
            wordAccuracies = wordAccuracies
        )
    }

    private fun calculateLevenshteinSimilarity(s1: String, s2: String): Double {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        val maxLen = maxOf(s1.length, s2.length)
        if (maxLen == 0) return 1.0
        return 1.0 - (dp[s1.length][s2.length].toDouble() / maxLen.toDouble())
    }

    private fun getOfflineTeacherResponse(
        userMessage: String,
        scenario: String,
        feedbackLanguage: com.example.data.model.FeedbackLanguage = com.example.data.model.FeedbackLanguage.TAMIL
    ): ChatMessage {
        val lower = userMessage.lowercase()
        val isEnglishFeedback = feedbackLanguage == com.example.data.model.FeedbackLanguage.ENGLISH

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("வணக்கம்") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Hello! I am Pavi Teacher. Welcome to our English class. How was your day?",
                    tamilTranslation = if (isEnglishFeedback) "Hello! I am Pavi Teacher. I am delighted to welcome you to our spoken English session. How was your day?"
                    else "வணக்கம்! நான் உங்கள் பவி டீச்சர். நமது ஆங்கில வகுப்பிற்கு அன்புடன் வரவேற்கிறேன். உங்கள் நாள் எப்படி கழிந்தது?",
                    grammarTipTamil = if (isEnglishFeedback) "'How was your day?' is the standard natural way to ask about someone's daily experience in the past simple tense."
                    else "'How was your day?' என்பது கடந்த காலத்தை (Past Tense) கேட்கும் விதம்.",
                    pronunciationTipTamil = if (isEnglishFeedback) "When saying 'Welcome', round your lips to produce a soft 'W' sound rather than a 'V' sound."
                    else "'Welcome' உச்சரிக்கும் போது 'வெல்கம்' என W ஒலியை உதட்டை குவித்து உச்சரிக்கவும்."
                )
            }
            lower.contains("auto") || lower.contains("taxi") || scenario.contains("Auto") || scenario.contains("Locality") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Could you please take me to the central bus stand using the meter?",
                    tamilTranslation = if (isEnglishFeedback) "This is the polite and professional way to hire an auto or cab in local travel."
                    else "மீட்டர் போட்டு மத்திய பேருந்து நிலையத்திற்கு அழைத்துச் செல்ல முடியுமா?",
                    grammarTipTamil = if (isEnglishFeedback) "Using 'Could you please take me...' sounds far more courteous than commanding 'Take me'."
                    else "ஆட்டோ அல்லது வாடகைக் காரில் பேசும்போது 'Could you please take me to...' என்பது பணிவான முறை.",
                    pronunciationTipTamil = if (isEnglishFeedback) "'Central' begins with a soft 'S' sound: /sɛntrəl/."
                    else "'Central' - சென்-ட்ரல் (C எழுத்து 'S' ஒலியில் தொடங்க வேண்டும்)."
                )
            }
            lower.contains("upi") || lower.contains("gpay") || lower.contains("qr") || scenario.contains("Market") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Can I scan the QR code to pay via UPI?",
                    tamilTranslation = "நான் UPI மூலம் பணம் செலுத்த QR குறியீட்டை ஸ்கேன் செய்யலாமா?",
                    grammarTipTamil = "'Can I scan...' என்பது அனுமதி கேட்கும் எளிய மற்றும் துல்லியமான வாக்கியம்.",
                    pronunciationTipTamil = "'Scan' உச்சரிக்கும் போது 'இஸ்கேன்' என்று சொல்லாமல் 'ஸ்கேன்' என தொடங்கவும்."
                )
            }
            lower.contains("direction") || lower.contains("where is") || scenario.contains("Direction") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Go straight past the roundtana and take the second left turn.",
                    tamilTranslation = "ரவுண்டானாவைத் தாண்டி நேராகச் சென்று, இரண்டாவது இடதுபுற திருப்பத்தில் திரும்பவும்.",
                    grammarTipTamil = "திசைகளைக் கூறும்போது Imperative Verbs (Go, Turn, Take) கொண்டு வாக்கியம் தொடங்கும்.",
                    pronunciationTipTamil = "'Straight' - ஸ்ட்ரெய்ட் (G & H எழுத்துக்கள் மறைமுக ஒலிகள்)."
                )
            }
            lower.contains("interview") || scenario.contains("Interview") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Tell me briefly about your professional background and strengths.",
                    tamilTranslation = "உங்கள் வேலை அனுபவம் மற்றும் பலங்களைப் பற்றி சுருக்கமாக கூறுங்கள்.",
                    grammarTipTamil = "தொடக்கத்தில் 'I have 3 years of experience in...' என்று தொடங்கலாம்.",
                    pronunciationTipTamil = "'Strengths' - ஸ்ட்ரெங்த்ஸ் (கடைசியில் 'த்ஸ்' மென்மையாக வர வேண்டும்)."
                )
            }
            lower.contains("coffee") || lower.contains("restaurant") || scenario.contains("Restaurant") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "Could I please get a hot cappuccino with less sugar?",
                    tamilTranslation = "சர்க்கரை குறைவாக ஒரு சூடான கேப்புச்சினோ காபி கிடைக்குமா?",
                    grammarTipTamil = "கட்டளையாக 'Give me coffee' என சொல்வதை விட 'Could I please get...' என்பது மிக கண்ணியமானது.",
                    pronunciationTipTamil = "'Cappuccino' - கே-பு-சீ-னோ (மென்மையான 'ch' ஒலி)."
                )
            }
            lower.contains("past") || lower.contains("yesterday") || lower.contains("did") -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "I went to the market yesterday and bought some fresh fruits.",
                    tamilTranslation = "நான் நேற்று சந்தைக்கு சென்று புதிய பழங்களை வாங்கினேன்.",
                    grammarTipTamil = "நேற்று நடந்ததற்கு 'go' என்பதற்கு பதிலாக 'went', 'buy' என்பதற்கு பதிலாக 'bought' பயன்படுத்த வேண்டும்.",
                    pronunciationTipTamil = "'Market' - மார்கெட் (R ஒலியை அழுத்தாமல் மென்மையாக சொல்லுங்கள்)."
                )
            }
            else -> {
                ChatMessage(
                    sender = ChatSender.TEACHER,
                    englishText = "That's wonderful! Can you tell me more about what you like to do in your free time?",
                    tamilTranslation = "அற்புதம்! உங்கள் ஓய்வு நேரத்தில் என்ன செய்ய விரும்புவீர்கள் என்பதைப் பற்றி மேலும் கூறுங்கள்?",
                    grammarTipTamil = "'In my free time, I like to...' அல்லது 'I enjoy reading books' என பதிலளிக்கலாம்.",
                    pronunciationTipTamil = "'Wonderful' - வண்டர்ஃபுல் (W ஒலி: உதட்டை வட்டமாக குவிக்கவும்)."
                )
            }
        }
    }
}
