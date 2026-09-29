package com.example.data.repository

import com.example.data.db.GrammarDao
import com.example.data.db.PracticeSessionDao
import com.example.data.db.UserProgressDao
import com.example.data.db.WeakSpotDao
import com.example.data.model.DailyGrammarExerciseEntity
import com.example.data.model.DailyGrammarModuleEntity
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.UserProgressEntity
import com.example.data.model.WeakSpotEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TamizhaRepository(
    private val progressDao: UserProgressDao,
    private val sessionDao: PracticeSessionDao,
    private val weakSpotDao: WeakSpotDao,
    private val grammarDao: GrammarDao
) {

    val userProgress: Flow<UserProgressEntity?> = progressDao.getUserProgress()
    val recentSessions: Flow<List<PracticeSessionEntity>> = sessionDao.getRecentSessions()
    val weakSpots: Flow<List<WeakSpotEntity>> = weakSpotDao.getAllWeakSpots()
    val dailyModules: Flow<List<DailyGrammarModuleEntity>> = grammarDao.getAllModules()

    fun getModuleById(moduleId: String): Flow<DailyGrammarModuleEntity?> = grammarDao.getModuleById(moduleId)

    fun getExercisesForModule(moduleId: String): Flow<List<DailyGrammarExerciseEntity>> =
        grammarDao.getExercisesForModule(moduleId)

    suspend fun initializeDefaultData() {
        val current = progressDao.getUserProgress().firstOrNull()
        if (current == null) {
            progressDao.insertOrUpdate(
                UserProgressEntity(
                    id = 1,
                    dayStreak = 3,
                    totalPracticeMinutes = 48,
                    totalGrammarAnswered = 32,
                    totalGrammarCorrect = 27,
                    totalPronunciationsDone = 15,
                    avgPronunciationScore = 87.4f,
                    lastActiveDate = System.currentTimeMillis()
                )
            )

            // Seed initial realistic weak spots for Tamil speakers
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "GRAMMAR",
                    title = "Continuous Tense vs Simple Present",
                    mistakePattern = "I am having two brothers",
                    correction = "I have two brothers",
                    adviceTamil = "தமிழில் 'இருக்கிறது' என்பதால் 'having' சொல்லக் கூடாது. சொந்தம்/உடைமையை 'have' என்று மட்டுமே சொல்ல வேண்டும்.",
                    frequency = 3
                )
            )
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "PRONUNCIATION",
                    title = "V and W Consonant Distinction",
                    mistakePattern = "Confusing 'Very' and 'Wary'",
                    correction = "V = upper teeth on lower lip | W = rounded lips",
                    adviceTamil = "தமிழில் உள்ள 'வ' போல இல்லாமல், ஆங்கில 'V' ஒலியில் மேல் பற்கள் கீழ் உதட்டை தொட வேண்டும்.",
                    frequency = 2
                )
            )
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "GRAMMAR",
                    title = "Did + Base Verb Rule",
                    mistakePattern = "Why didn't you came?",
                    correction = "Why didn't you come?",
                    adviceTamil = "did / didn't வந்தால் அதன் பின் வரும் வினைச்சொல் கட்டாயம் முதல் வடிவில் (come) மட்டுமே இருக்க வேண்டும்.",
                    frequency = 4
                )
            )

            // Seed recent practice sessions
            sessionDao.insertSession(
                PracticeSessionEntity(
                    sessionType = "VOICE_CLASS",
                    topicTitle = "Self Introduction & Job Interview",
                    score = 92,
                    summaryTamil = "நேர்முகத் தேர்வு அறிமுகத்தில் சிறந்த உச்சரிப்பு மற்றும் வாக்கிய அமைப்பு.",
                    summaryEnglish = "Excellent flow in introducing background and skills."
                )
            )
            sessionDao.insertSession(
                PracticeSessionEntity(
                    sessionType = "GRAMMAR",
                    topicTitle = "Prepositions: In vs On the Bus",
                    score = 85,
                    summaryTamil = "பொதுப் போக்குவரத்தில் 'on' பயன்படுத்துவது குறித்த தெளிவு கிடைத்தது.",
                    summaryEnglish = "Solid understanding of transport prepositions."
                )
            )
        }

        // Initialize Daily Grammar Exercise Modules in Room if not seeded
        if (grammarDao.getModulesCount() == 0) {
            seedDailyGrammarModules()
        }
    }

    private suspend fun seedDailyGrammarModules() {
        val modules = listOf(
            DailyGrammarModuleEntity(
                moduleId = "module_day_1",
                dayNumber = 1,
                titleEnglish = "Tamil Translation Pitfalls",
                titleTamil = "நாள் 1: நேரடி மொழிபெயர்ப்பு தவறுகள்",
                descriptionTamil = "தமிழில் சிந்தித்து ஆங்கிலத்தில் மொழிபெயர்க்கும்போது ஏற்படும் தவறுகளைக் களையும் பயிற்சி.",
                category = "Tamil Common Traps",
                level = "தொடக்க நிலை (Essential)",
                totalExercises = 3,
                completedExercises = 1,
                correctAnswersCount = 1,
                isCompleted = false,
                scorePercentage = 33
            ),
            DailyGrammarModuleEntity(
                moduleId = "module_day_2",
                dayNumber = 2,
                titleEnglish = "Tenses & Time Markers",
                titleTamil = "நாள் 2: காலங்கள் & நேரக் குறியீடுகள்",
                descriptionTamil = "இறந்த காலம் (Past) மற்றும் நிகழ்காலம் (Present) துல்லியமாக பயன்படுத்தும் முறைகள்.",
                category = "Tenses",
                level = "இடைநிலை (Intermediate)",
                totalExercises = 3,
                completedExercises = 0,
                correctAnswersCount = 0,
                isCompleted = false,
                scorePercentage = 0
            ),
            DailyGrammarModuleEntity(
                moduleId = "module_day_3",
                dayNumber = 3,
                titleEnglish = "Prepositions of Place & Travel",
                titleTamil = "நாள் 3: இடமும் பயணமும் (In, On, At)",
                descriptionTamil = "பேருந்து, கார், விமானம் மற்றும் இடங்களில் Preposition எவ்வாறு மாறும் என்பதை அறிதல்.",
                category = "Prepositions",
                level = "தொடக்க நிலை (Essential)",
                totalExercises = 3,
                completedExercises = 0,
                correctAnswersCount = 0,
                isCompleted = false,
                scorePercentage = 0
            ),
            DailyGrammarModuleEntity(
                moduleId = "module_day_4",
                dayNumber = 4,
                titleEnglish = "Office & Workplace English",
                titleTamil = "நாள் 4: அலுவலக பணிவான உரையாடல்",
                descriptionTamil = "Could you please, Would you mind, I look forward to போன்ற சர்வதேச கண்ணிய வார்த்தைகள்.",
                category = "Workplace English",
                level = "நடைமுறை நிலை (Professional)",
                totalExercises = 3,
                completedExercises = 0,
                correctAnswersCount = 0,
                isCompleted = false,
                scorePercentage = 0
            ),
            DailyGrammarModuleEntity(
                moduleId = "module_day_5",
                dayNumber = 5,
                titleEnglish = "Question Inversion & Auxiliary Verbs",
                titleTamil = "நாள் 5: சரியான கேள்வி அமைக்கும் முறை",
                descriptionTamil = "Do, Does, Did, Why didn't ஆகியவற்றைக் கொண்டு பிழையின்றி கேள்வி கேட்பது எப்படி.",
                category = "Questions",
                level = "இடைநிலை (Intermediate)",
                totalExercises = 3,
                completedExercises = 0,
                correctAnswersCount = 0,
                isCompleted = false,
                scorePercentage = 0
            )
        )

        val exercises = listOf(
            // Module 1 Exercises
            DailyGrammarExerciseEntity(
                moduleId = "module_day_1",
                orderIndex = 1,
                questionPromptEnglish = "Choose the correct sentence to introduce your family:",
                questionTamilContext = "எனக்கு இரண்டு சகோதரர்கள் இருக்கிறார்கள் - இதை ஆங்கிலத்தில் எப்படி சொல்வது?",
                optionA = "I am having two brothers.",
                optionB = "I have two brothers.",
                optionC = "I have had two brothers.",
                optionD = "Two brothers are with me.",
                correctOptionIndex = 1,
                explanationTamil = "உடைமை அல்லது சொந்தத்தை (Possession) குறிக்க 'I have' மட்டுமே வர வேண்டும். 'having' உணவு அருந்தும்போது மட்டுமே வரும்.",
                explanationEnglish = "Use simple present 'I have' for relationships and possession. 'I am having' indicates action like eating.",
                userSelectedOption = 1,
                isAnsweredCorrectly = true,
                isAttempted = true
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_1",
                orderIndex = 2,
                questionPromptEnglish = "Let us ______ the project timeline tomorrow in the meeting.",
                questionTamilContext = "திட்டத்தைப் பற்றி விவாதிக்கலாம் என்று சொல்லும்போது:",
                optionA = "discuss about",
                optionB = "discuss with",
                optionC = "discuss",
                optionD = "discussing to",
                correctOptionIndex = 2,
                explanationTamil = "'discuss' என்ற சொல்லிலேயே 'இதைப் பற்றி' (about) அடங்கியுள்ளது. எனவே 'discuss about' தவறு; 'discuss the project' என்பதே சரி.",
                explanationEnglish = "'Discuss' is a transitive verb that directly takes the object without 'about'."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_1",
                orderIndex = 3,
                questionPromptEnglish = "May I know your ______?",
                questionTamilContext = "ஒருவரின் பெயரை கண்ணியமாக கேட்கும்போது:",
                optionA = "good name",
                optionB = "sweet name",
                optionC = "name",
                optionD = "true name",
                correctOptionIndex = 2,
                explanationTamil = "இந்திய மொழிகளில் 'சுப நாமம்' என்பதால் 'good name' என்கிறோம். சர்வதேச ஆங்கிலத்தில் 'May I know your name?' மட்டுமே சரியானது.",
                explanationEnglish = "In standard English, simply ask 'May I know your name?' Avoid literal translation 'good name'."
            ),

            // Module 2 Exercises
            DailyGrammarExerciseEntity(
                moduleId = "module_day_2",
                orderIndex = 1,
                questionPromptEnglish = "She ______ to the office yesterday morning.",
                questionTamilContext = "அவள் நேற்று காலை அலுவலகத்திற்கு சென்றாள். (Past Simple)",
                optionA = "go",
                optionB = "goes",
                optionC = "went",
                optionD = "has gone",
                correctOptionIndex = 2,
                explanationTamil = "'yesterday' என்பது முடிந்துபோன காலத்தைக் குறிக்கும். எனவே 'go' வின் past tense 'went' வர வேண்டும்.",
                explanationEnglish = "'Went' is the past simple form of 'go', required by the past time marker 'yesterday'."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_2",
                orderIndex = 2,
                questionPromptEnglish = "I have been working here ______ three years.",
                questionTamilContext = "மூன்று வருடங்களாக வேலை செய்து வருகிறேன் (Duration of time):",
                optionA = "since",
                optionB = "for",
                optionC = "from",
                optionD = "during",
                correctOptionIndex = 1,
                explanationTamil = "கால அளவை (Duration: three years, two hours) குறிப்பிடும்போது 'for' பயன்படுத்த வேண்டும். குறிப்பிட்ட தொடக்க புள்ளிக்கு (since 2020) மட்டுமே 'since' வரும்.",
                explanationEnglish = "Use 'for' with a duration/period of time (for three years), and 'since' with a specific point in time (since 2021)."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_2",
                orderIndex = 3,
                questionPromptEnglish = "The express train ______ at 6:00 PM every evening.",
                questionTamilContext = "ரயில் புறப்படும் அட்டவணை (Scheduled Timetable):",
                optionA = "leaves",
                optionB = "is leaving",
                optionC = "will be leave",
                optionD = "left",
                correctOptionIndex = 0,
                explanationTamil = "அட்டவணைப்படுத்தப்பட்ட நிகழ்வுகளுக்கு (Fixed schedules) Simple Present 'leaves' பயன்படுத்த வேண்டும்.",
                explanationEnglish = "Use Simple Present for fixed schedules, timetables, and recurring departures."
            ),

            // Module 3 Exercises
            DailyGrammarExerciseEntity(
                moduleId = "module_day_3",
                orderIndex = 1,
                questionPromptEnglish = "I am currently ______ the bus traveling to Madurai.",
                questionTamilContext = "பேருந்தில் பயணிக்கும் போது:",
                optionA = "in",
                optionB = "on",
                optionC = "at",
                optionD = "inside of",
                correctOptionIndex = 1,
                explanationTamil = "நாம் எழுந்து நிற்க அல்லது நடக்கக்கூடிய பெரிய பொதுப் போக்குவரத்துக்கு (Bus, Train, Flight) 'on' பயன்படுத்த வேண்டும்.",
                explanationEnglish = "Use 'on' for large public transit vehicles (on the bus, on the train, on a plane)."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_3",
                orderIndex = 2,
                questionPromptEnglish = "We arrived ______ Chennai International Airport at noon.",
                questionTamilContext = "விமான நிலையத்தை அடைந்தோம்:",
                optionA = "at",
                optionB = "to",
                optionC = "in",
                optionD = "into",
                correctOptionIndex = 0,
                explanationTamil = "குறிப்பிட்ட இடங்களுக்கு 'arrived at' சொல்ல வேண்டும். பெரிய நகரங்கள்/நாடுகளுக்கு மட்டுமே 'arrived in' வரும்.",
                explanationEnglish = "Use 'arrived at' for specific points or locations (airport, station), and 'arrived in' for cities/countries."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_3",
                orderIndex = 3,
                questionPromptEnglish = "The weekly team sync is scheduled ______ Monday morning.",
                questionTamilContext = "திங்கட்கிழமை காலையில்:",
                optionA = "in",
                optionB = "at",
                optionC = "on",
                optionD = "by",
                correctOptionIndex = 2,
                explanationTamil = "வாரத்தின் நாட்களுக்கு (Monday, Tuesday...) எப்போதும் 'on' மட்டுமே முன்னிடைச்சொல்லாக வரும்.",
                explanationEnglish = "Always use preposition 'on' before days of the week (on Monday morning)."
            ),

            // Module 4 Exercises
            DailyGrammarExerciseEntity(
                moduleId = "module_day_4",
                orderIndex = 1,
                questionPromptEnglish = "Could you please ______ me the updated invoice?",
                questionTamilContext = "பணிவாக ரசீதை அனுப்பக் கேட்கும்போது:",
                optionA = "send",
                optionB = "sending",
                optionC = "sent",
                optionD = "to send",
                correctOptionIndex = 0,
                explanationTamil = "Modal verb (could, would, can) க்குப் பிறகு எப்போதும் base verb 'send' மட்டுமே வரும்.",
                explanationEnglish = "After modal auxiliaries (could, would, can), always use the bare infinitive / base verb form."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_4",
                orderIndex = 2,
                questionPromptEnglish = "I look forward to ______ your response soon.",
                questionTamilContext = "மின்னஞ்சலின் முடிவில் 'உங்களின் பதிலை ஆவலுடன் எதிர்நோக்குகிறேன்':",
                optionA = "receive",
                optionB = "receiving",
                optionC = "received",
                optionD = "be received",
                correctOptionIndex = 1,
                explanationTamil = "மிக முக்கியமான அலுவலக விதி: 'look forward to' என்ற தொடரில் 'to' என்பது preposition ஆகும். எனவே தொடர்ந்து Gerund (-ing) வர வேண்டும்: 'receiving'.",
                explanationEnglish = "In the idiom 'look forward to', 'to' is a preposition, which requires a gerund (-ing form: 'receiving')."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_4",
                orderIndex = 3,
                questionPromptEnglish = "Would you mind ______ the window?",
                questionTamilContext = "ஜன்னலை மூட முடியுமா என பணிவாகக் கேட்கும்போது:",
                optionA = "close",
                optionB = "closing",
                optionC = "to close",
                optionD = "closed",
                correctOptionIndex = 1,
                explanationTamil = "'Would you mind...' வந்த பிறகு எப்போதும் -ing வடிவ வினைச்சொல் (Gerund: closing) மட்டுமே வர வேண்டும்.",
                explanationEnglish = "The phrase 'Would you mind' is always followed by a verb in the -ing form (gerund)."
            ),

            // Module 5 Exercises
            DailyGrammarExerciseEntity(
                moduleId = "module_day_5",
                orderIndex = 1,
                questionPromptEnglish = "Why ______ to the manager yesterday?",
                questionTamilContext = "நேற்று மேலாளரிடம் ஏன் பேசவில்லை என்று கேட்க:",
                optionA = "you didn't talk",
                optionB = "didn't you talk",
                optionC = "didn't you talked",
                optionD = "why you not talked",
                correctOptionIndex = 1,
                explanationTamil = "ஆங்கிலத்தில் கேள்வி அமைக்கும்போது உதவி வினைச்சொல் (didn't) எழுவாய்க்கு (you) முன் வர வேண்டும்: 'didn't you talk'. மேலும் did வந்தால் base verb (talk) மட்டுமே வரும்.",
                explanationEnglish = "Questions require subject-auxiliary inversion ('didn't you'). After 'did', the main verb must be base form ('talk')."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_5",
                orderIndex = 2,
                questionPromptEnglish = "Where ______ your manager work currently?",
                questionTamilContext = "உங்கள் மேலாளர் எங்கு பணிபுரிகிறார்? (Third person singular):",
                optionA = "do",
                optionB = "does",
                optionC = "is",
                optionD = "are",
                correctOptionIndex = 1,
                explanationTamil = "'Your manager' என்பது Third Person Singular (he/she). எனவே 'do' அல்லாமல் 'does' பயன்படுத்த வேண்டும்.",
                explanationEnglish = "Use auxiliary 'does' for third-person singular subjects (your manager = he/she) in present tense questions."
            ),
            DailyGrammarExerciseEntity(
                moduleId = "module_day_5",
                orderIndex = 3,
                questionPromptEnglish = "How often ______ you practice spoken English?",
                questionTamilContext = "எவ்வளவு அடிக்கடி ஆங்கிலம் பேசப் பழகுகிறீர்கள்?",
                optionA = "do",
                optionB = "are",
                optionC = "have",
                optionD = "is",
                correctOptionIndex = 0,
                explanationTamil = "Second person 'you' மற்றும் வழக்கமான செயலுக்கு 'do' உதவி வினைச்சொல் பயன்படுத்த வேண்டும்.",
                explanationEnglish = "Use 'do' with subject 'you' for routine/habitual present actions."
            )
        )

        grammarDao.insertModules(modules)
        grammarDao.insertExercises(exercises)
    }

    suspend fun recordModuleExerciseAnswer(
        moduleId: String,
        exerciseId: Int,
        selectedOption: Int,
        isCorrect: Boolean,
        explanationTamil: String
    ) {
        val now = System.currentTimeMillis()
        grammarDao.recordExerciseAnswer(exerciseId, selectedOption, isCorrect, now)

        // Retrieve current exercises for this module to calculate progress
        val exercises = grammarDao.getExercisesForModule(moduleId).firstOrNull() ?: emptyList()
        val total = exercises.size
        val completedCount = exercises.count { it.isAttempted || it.exerciseId == exerciseId }
        val correctCount = exercises.count {
            if (it.exerciseId == exerciseId) isCorrect else (it.isAnsweredCorrectly == true)
        }
        val scorePercent = if (total > 0) ((correctCount * 100) / total) else 0
        val isCompleted = completedCount >= total

        grammarDao.updateModuleProgress(
            moduleId = moduleId,
            completedCount = completedCount,
            correctCount = correctCount,
            scorePercent = scorePercent,
            isCompleted = isCompleted,
            timestamp = now
        )

        // Update overall user progress
        progressDao.recordGrammarResult(if (isCorrect) 1 else 0)

        if (!isCorrect) {
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "GRAMMAR",
                    title = "Module: $moduleId Drill",
                    mistakePattern = "Option $selectedOption selected",
                    correction = "Review rule in module",
                    adviceTamil = explanationTamil,
                    frequency = 1
                )
            )
        }

        if (isCompleted) {
            sessionDao.insertSession(
                PracticeSessionEntity(
                    sessionType = "GRAMMAR",
                    topicTitle = "Completed Module: $moduleId",
                    score = scorePercent,
                    summaryTamil = "இலக்கண தொகுதி முடிந்தது. மதிப்பெண்: $scorePercent%",
                    summaryEnglish = "Daily module completed with $scorePercent% score."
                )
            )
        }
    }

    suspend fun resetModule(moduleId: String) {
        grammarDao.resetModuleExercises(moduleId)
        grammarDao.resetModuleEntity(moduleId)
    }

    suspend fun recordGrammarResult(
        isCorrect: Boolean,
        category: String,
        mistakeTitle: String,
        mistakeSentence: String,
        correction: String,
        adviceTamil: String
    ) {
        progressDao.recordGrammarResult(if (isCorrect) 1 else 0)

        if (!isCorrect) {
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "GRAMMAR",
                    title = mistakeTitle,
                    mistakePattern = mistakeSentence,
                    correction = correction,
                    adviceTamil = adviceTamil,
                    frequency = 1
                )
            )
        }

        sessionDao.insertSession(
            PracticeSessionEntity(
                sessionType = "GRAMMAR",
                topicTitle = category,
                score = if (isCorrect) 100 else 40,
                summaryTamil = if (isCorrect) "சரியான பதில்! இலக்கண விதி பின்பற்றப்பட்டது." else "தவறை திருத்தி புதிய விதி கற்றறியப்பட்டது.",
                summaryEnglish = if (isCorrect) "Correct answer registered." else "Reviewed correction and explanation."
            )
        )
    }

    suspend fun recordPronunciationResult(
        phrase: String,
        score: Int,
        feedbackTamil: String,
        adviceTamil: String
    ) {
        progressDao.recordPronunciationResult(score.toFloat())

        if (score < 75) {
            weakSpotDao.insertOrUpdateWeakSpot(
                WeakSpotEntity(
                    category = "PRONUNCIATION",
                    title = "Pronunciation: $phrase",
                    mistakePattern = "Score: $score%",
                    correction = phrase,
                    adviceTamil = adviceTamil,
                    frequency = 1
                )
            )
        }

        sessionDao.insertSession(
            PracticeSessionEntity(
                sessionType = "PRONUNCIATION",
                topicTitle = phrase,
                score = score,
                summaryTamil = feedbackTamil,
                summaryEnglish = "Pronunciation accuracy score: $score%"
            )
        )
    }

    suspend fun recordVoiceSession(
        topic: String,
        score: Int,
        summaryTamil: String,
        summaryEnglish: String
    ) {
        sessionDao.insertSession(
            PracticeSessionEntity(
                sessionType = "VOICE_CLASS",
                topicTitle = topic,
                score = score,
                summaryTamil = summaryTamil,
                summaryEnglish = summaryEnglish
            )
        )
    }

    suspend fun markWeakSpotResolved(id: Int) {
        weakSpotDao.markResolved(id)
    }
}
