package com.example.data.repository

import com.example.data.model.GrammarQuestion
import com.example.data.model.PronunciationExercise

object CurriculumData {

    val grammarExercises: List<GrammarQuestion> = listOf(
        GrammarQuestion(
            id = 1,
            category = "Tamil Speaker Common Errors",
            questionPromptEnglish = "Choose the correct sentence to describe your family:",
            questionTamilContext = "எனக்கு இரண்டு சகோதரர்கள் இருக்கிறார்கள் - இதை ஆங்கிலத்தில் எப்படி சரியாக சொல்வது?",
            options = listOf(
                "I am having two brothers.",
                "I have two brothers.",
                "I have had two brothers.",
                "Two brothers are with me."
            ),
            correctIndex = 1,
            explanationTamil = "தமிழில் 'என்னிடம் இருக்கிறது' என்பதற்கு 'having' என்று Continuous Tense-ல் பயன்படுத்தக் கூடாது. உடைமை (Possession) குறிக்கும்போது 'have' என்று மட்டுமே பயன்படுத்த வேண்டும்.",
            explanationEnglish = "Use simple present 'I have' for relationships and possession. 'I am having' is only used for eating or experiences (e.g. 'I am having breakfast')."
        ),
        GrammarQuestion(
            id = 2,
            category = "Tenses",
            questionPromptEnglish = "He ______ to the office yesterday.",
            questionTamilContext = "அவர் நேற்று அலுவலகத்திற்கு சென்றார். (Past Tense)",
            options = listOf(
                "go",
                "goes",
                "went",
                "gone"
            ),
            correctIndex = 2,
            explanationTamil = "'yesterday' என்பது முடிந்துபோன காலம் (Past Tense). எனவே 'go' என்ற வினைச்சொல்லின் கடந்த கால வடிவம் 'went' வர வேண்டும்.",
            explanationEnglish = "'Went' is the past simple form of 'go', required here because of the past time marker 'yesterday'."
        ),
        GrammarQuestion(
            id = 3,
            category = "Tamil Speaker Common Errors",
            questionPromptEnglish = "Spot the error: 'Why you didn't came to the meeting?' -> Correct form:",
            questionTamilContext = "'did' வந்த பிறகு வினைச்சொல் எந்த வடிவத்தில் இருக்க வேண்டும்?",
            options = listOf(
                "Why you didn't came?",
                "Why didn't you come?",
                "Why didn't you came?",
                "Why you no come?"
            ),
            correctIndex = 1,
            explanationTamil = "மிக முக்கியமான விதி: வாக்கியத்தில் 'did' அல்லது 'didn't' பயன்படுத்தினால், அதன் பின் வரும் வினைச்சொல் Base form (V1: come) ஆக மட்டுமே இருக்க வேண்டும். 'didn't came' என்பது தவறு!",
            explanationEnglish = "After auxiliary 'did' or 'didn't', the main verb must always be in base form (come, not came). Also invert subject and auxiliary for questions: 'Why didn't you come?'"
        ),
        GrammarQuestion(
            id = 4,
            category = "Prepositions",
            questionPromptEnglish = "I am currently ______ the bus on my way home.",
            questionTamilContext = "பேருந்தில் இருக்கும்போது 'in' சொல்ல வேண்டுமா அல்லது 'on' சொல்ல வேண்டுமா?",
            options = listOf(
                "in",
                "on",
                "at",
                "by"
            ),
            correctIndex = 1,
            explanationTamil = "நாம் எழுந்து நடக்கக்கூடிய பெரிய வாகனங்களுக்கு (Bus, Train, Plane) 'on' என்று சொல்ல வேண்டும் ('on the bus'). சிறிய காருக்கு மட்டுமே 'in the car' என்று சொல்லுவோம்.",
            explanationEnglish = "Use 'on' for public transport where you can stand/walk (on a bus, train, plane), and 'in' for private cars or taxis."
        ),
        GrammarQuestion(
            id = 5,
            category = "Tamil Speaker Common Errors",
            questionPromptEnglish = "Let us ______ the project timeline tomorrow.",
            questionTamilContext = "'discuss' சொல்லும்போது 'discuss about' என்று சொல்லலாமா?",
            options = listOf(
                "discuss about",
                "discuss with",
                "discuss",
                "discussing"
            ),
            correctIndex = 2,
            explanationTamil = "நாம் தமிழில் 'இதைப் பற்றி விவாதிப்போம்' என்று சொல்வதால், பலர் 'discuss about' என்று தவறாக ஆங்கிலத்தில் பயன்படுத்துகிறார்கள். 'discuss' என்ற சொல்லிலேயே 'about' அடங்கியுள்ளது!",
            explanationEnglish = "'Discuss' is a transitive verb that directly takes the object without 'about'. Say 'discuss the project', never 'discuss about the project'."
        ),
        GrammarQuestion(
            id = 6,
            category = "Daily Talk",
            questionPromptEnglish = "How to politely ask someone to repeat what they said?",
            questionTamilContext = "ஒருவர் பேசியது புரியவில்லை என்றால் பணிவாக மீண்டும் கேட்கும் விதம்:",
            options = listOf(
                "What? Say again!",
                "Pardon me, could you please repeat that?",
                "Repeat that once more.",
                "Speak slowly now."
            ),
            correctIndex = 1,
            explanationTamil = "'Pardon me, could you please repeat that?' என்பது சர்வதேச அளவில் மிக கண்ணியமான மற்றும் மரியாதையான வாக்கியமாகும்.",
            explanationEnglish = "'Pardon me, could you please repeat that?' is professional, polite, and universally understood."
        )
    )

    val pronunciationExercises: List<PronunciationExercise> = listOf(
        PronunciationExercise(
            id = "pron_1",
            phrase = "Very well done",
            tamilMeaning = "மிக நன்றாக செய்தீர்கள்",
            phoneticTamil = "வெரி வெல் டன் (V-sound: உதட்டின் மீது பல் பட வேண்டும்)",
            soundFocus = "V vs W ஒலி வேறுபாடு",
            commonTamilMistakeNotice = "தமிழில் 'வ' ஒலி W போல இருக்கும். ஆனால் ஆங்கில 'V' பேசும்போது மேல் பற்கள் கீழ் உதட்டில் மெதுவாக தொட வேண்டும் (Friction)."
        ),
        PronunciationExercise(
            id = "pron_2",
            phrase = "School and Station",
            tamilMeaning = "பள்ளி மற்றும் ரயில் நிலையம்",
            phoneticTamil = "ஸ்கூல் அண்ட் ஸ்டேஷன் ('இ' சேர்க்கக் கூடாது!)",
            soundFocus = "S-Cluster (இஸ்கூல் தவறு -> ஸ்கூல் சரி)",
            commonTamilMistakeNotice = "தமிழ் பேசுபவர்கள் தொடக்கத்தில் 'இ' அல்லது 'எ' சேர்த்து 'இஸ்கூல்' / 'எஸ்டேஷன்' என்று சொல்வது வழக்கம். வெறும் 'ஸ்ஸ்' காற்றை மட்டுமே வெளிவிட வேண்டும்."
        ),
        PronunciationExercise(
            id = "pron_3",
            phrase = "Thank you for the coffee",
            tamilMeaning = "காபிக்கு நன்றி",
            phoneticTamil = "தேங்க் யூ ஃபார் த காஃபி (F & Th ஒலி)",
            soundFocus = "F vs P மற்றும் Th ஒலி",
            commonTamilMistakeNotice = "காபி (Coffee) சொல்லும்போது 'பி' (P) ஒலியாக மாறாமல் மேல் பல் கீழ் உதட்டை தொடும் 'ஃப்' (F) ஒலியாக இருக்க வேண்டும்."
        ),
        PronunciationExercise(
            id = "pron_4",
            phrase = "Wednesday afternoon",
            tamilMeaning = "புதன்கிழமை மதியம்",
            phoneticTamil = "வென்ஸ்-டே ஆஃப்டர்நூன் (D எழுத்து சத்தமில்லை)",
            soundFocus = "Silent Letters (மறைமுக எழுத்துக்கள்)",
            commonTamilMistakeNotice = "Wednesday-ல் 'd' உச்சரிக்கக் கூடாது. 'வெட்னஸ்டே' அல்ல, 'வென்ஸ்-டே' (Wenz-day) என்று மட்டுமே உச்சரிக்க வேண்டும்."
        ),
        PronunciationExercise(
            id = "pron_5",
            phrase = "Comfortable environment",
            tamilMeaning = "வசதியான சூழல்",
            phoneticTamil = "கம்ஃபர்ட்டபிள் என்விரான்மெண்ட்",
            soundFocus = "Syllable Stress & Rhythm",
            commonTamilMistakeNotice = "'கம்-ஃபோர்ட்-ஏபில்' என்று 4 அசைகளாக பிரிக்காமல் 'கம்ஃப்-டபிள்' (comf-ter-ble) என 3 அசைகளாக உச்சரிக்க வேண்டும்."
        ),
        PronunciationExercise(
            id = "pron_loc_1",
            phrase = "Could you drop me near the signal?",
            tamilMeaning = "சிக்னல் அருகே என்னை இறக்கிவிட முடியுமா?",
            phoneticTamil = "குட் யூ ட்ராப் மீ நியர் த சிக்னல்?",
            soundFocus = "Locality: ஆட்டோ & வாடகை வண்டி",
            commonTamilMistakeNotice = "'Drop me' என்று கட்டளையாக கூறுவதை விட 'Could you drop me...' என தொடங்குவது மரியாதையான நடைமுறை ஆங்கிலம்."
        ),
        PronunciationExercise(
            id = "pron_loc_2",
            phrase = "Can I pay through UPI QR code?",
            tamilMeaning = "நான் UPI க்யூஆர் கோட் மூலம் பணம் செலுத்தலாமா?",
            phoneticTamil = "கேன் ஐ பே த்ரூ யூ.பி.ஐ க்யூ-ஆர் கோட்?",
            soundFocus = "Locality: உள்ளூர் கடை & சூப்பர் மார்க்கெட்",
            commonTamilMistakeNotice = "'Through' உச்சரிக்கும் போது 'த்ரூ' என மென்மையான 'Th' ஒலியுடன் சொல்லவும்; 'த்ரூ' vs 'ட்ரூ' வேறுபாடு கவனிக்கவும்."
        ),
        PronunciationExercise(
            id = "pron_loc_3",
            phrase = "Take the second left after the roundtana",
            tamilMeaning = "ரவுண்டானா தாண்டி இரண்டாவது இடதுபுறம் செல்லவும்",
            phoneticTamil = "டேக் த செகண்ட் லெஃப்ட் ஆஃப்டர் த ரவுண்டானா",
            soundFocus = "Locality: திசைகளும் தெரு அடையாளங்களும்",
            commonTamilMistakeNotice = "'Left' உச்சரிப்பில் 'L' மற்றும் 'F' ஒலிகள் தெளிவாக இருக்க வேண்டும். 'லெப்ட்' அல்லாமல் 'லெஃப்ட்' என்று சொல்லவும்."
        ),
        PronunciationExercise(
            id = "pron_loc_4",
            phrase = "One strong filter coffee with less sugar",
            tamilMeaning = "சர்க்கரை குறைவாக ஒரு ஸ்ட்ராங் ஃபில்டர் காபி கொடுங்கள்",
            phoneticTamil = "ஒன் ஸ்ட்ராங் ஃபில்டர் காஃபி வித் லெஸ் சுகர்",
            soundFocus = "Locality: டீக்கடை & உணவகம்",
            commonTamilMistakeNotice = "'Coffee' சொல்லும்போது 'பி' அல்லாமல் உதடு பற்களை தொடும் 'ஃப்' (F) ஒலியாக இருக்க வேண்டும்."
        ),
        PronunciationExercise(
            id = "pron_loc_5",
            phrase = "Is this the designated visitor parking?",
            tamilMeaning = "இது விருந்தினர்களுக்கான வாகன நிறுத்துமிடமா?",
            phoneticTamil = "இஸ் திஸ் த டெசிக்னேடட் விசிட்டர் பார்க்கிங்?",
            soundFocus = "Locality: குடியிருப்பு & அடுக்குமாடி சங்கம்",
            commonTamilMistakeNotice = "'Designated' - டெ-சிக்-னே-டட் (G ஒலி மென்மையாக வர வேண்டும்)."
        )
    )
}
