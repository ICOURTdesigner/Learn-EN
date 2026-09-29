package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DailyGrammarExerciseEntity
import com.example.data.model.DailyGrammarModuleEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SkyTertiary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.GrammarViewModel
import com.example.ui.viewmodel.MainViewModel

@Composable
fun GrammarPracticeScreen(
    mainViewModel: MainViewModel,
    grammarViewModel: GrammarViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedModuleId by grammarViewModel.selectedModuleId.collectAsState()
    val feedbackLanguage by mainViewModel.feedbackLanguage.collectAsState()

    androidx.compose.runtime.LaunchedEffect(feedbackLanguage) {
        grammarViewModel.setFeedbackLanguage(feedbackLanguage)
    }

    Crossfade(
        targetState = selectedModuleId != null,
        label = "module_crossfade",
        modifier = modifier.fillMaxSize()
    ) { hasActiveModule ->
        if (hasActiveModule) {
            ModuleQuizRunnerScreen(
                grammarViewModel = grammarViewModel,
                onBack = { grammarViewModel.backToModules() }
            )
        } else {
            ModulesOverviewScreen(
                grammarViewModel = grammarViewModel,
                onSelectModule = { grammarViewModel.openModule(it) }
            )
        }
    }
}

@Composable
fun ModulesOverviewScreen(
    grammarViewModel: GrammarViewModel,
    onSelectModule: (String) -> Unit
) {
    val modules by grammarViewModel.modules.collectAsState()
    val stats by grammarViewModel.overallStats.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Syllabus Progress Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.School,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "தினசரி இலக்கணத் தொகுதிகள்",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Daily Grammar Modules & Progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = SaffronSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${stats.completedModules}/${stats.totalModules} தொகுதிகள்",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SaffronSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val progressFraction = if (stats.totalExercises > 0) {
                        (stats.completedExercises.toFloat() / stats.totalExercises.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${stats.completedExercises} / ${stats.totalExercises} பயிற்சிகள் முடிந்தது",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "தேர்ச்சி விகிதம்: ${stats.overallPercentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "பாடத் தொகுப்புகள் (Module Curriculum)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Module Cards
        items(modules, key = { it.moduleId }) { module ->
            ModuleCard(
                module = module,
                onClick = { onSelectModule(module.moduleId) }
            )
        }
    }
}

@Composable
fun ModuleCard(
    module: DailyGrammarModuleEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("grammar_module_${module.moduleId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (module.isCompleted) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.2.dp,
            if (module.isCompleted) SuccessGreen.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day badge, Category & Level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "நாள் ${module.dayNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = module.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (module.isCompleted) {
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "முடிந்தது • ${module.scorePercentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SuccessGreen
                            )
                        }
                    }
                } else if (module.completedExercises > 0) {
                    Surface(
                        color = SaffronSecondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${module.completedExercises}/${module.totalExercises} தொடர்கிறது",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SaffronSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Module Title in Tamil & English
            Text(
                text = module.titleTamil,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = module.titleEnglish,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = module.descriptionTamil,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val progressFrac = if (module.totalExercises > 0) {
                    module.completedExercises.toFloat() / module.totalExercises.toFloat()
                } else 0f

                LinearProgressIndicator(
                    progress = { progressFrac },
                    color = if (module.isCompleted) SuccessGreen else EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = if (module.isCompleted) "மீண்டும் பார்க்க →" else if (module.completedExercises > 0) "தொடரவும் →" else "தொடங்கவும் →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
            }
        }
    }
}

@Composable
fun ModuleQuizRunnerScreen(
    grammarViewModel: GrammarViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val currentMod by grammarViewModel.currentModule.collectAsState()
    val exercises by grammarViewModel.currentModuleExercises.collectAsState()
    val currentIndex by grammarViewModel.currentExerciseIndex.collectAsState()
    val selectedOption by grammarViewModel.selectedOption.collectAsState()
    val hasChecked by grammarViewModel.hasCheckedAnswer.collectAsState()
    val isCelebration by grammarViewModel.isModuleCelebration.collectAsState()

    val currentEx = exercises.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Runner Top Navigation Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_to_modules_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "பின்செல்",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentMod?.titleTamil ?: "இலக்கணப் பயிற்சி",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${currentMod?.category ?: ""} • நாள் ${currentMod?.dayNumber ?: 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Progress Bar
        if (exercises.isNotEmpty()) {
            val progressVal = ((currentIndex + 1).toFloat() / exercises.size.toFloat()).coerceIn(0f, 1f)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "பயிற்சி ${currentIndex + 1} / ${exercises.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "${(progressVal * 100).toInt()}% முடிந்தது",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LinearProgressIndicator(
                    progress = { progressVal },
                    color = EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        if (isCelebration) {
            // Module Completed Celebration View
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SaffronSecondary.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = SaffronSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "வாழ்த்துகள்! தொகுதி நிறைவடைந்தது! 🎉",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "இந்த தொகுதியில் உள்ள அனைத்து விதிகளையும் வெற்றிகரமாக பயின்றுள்ளீர்கள். உங்கள் தொடர் பயிற்சி ஆங்கில சரளத்தை அதிகரிக்கும்!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("தொகுதிகளுக்குத் திரும்பு (Back to Modules)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { grammarViewModel.retakeModule() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("மீண்டும் பயிற்சி செய் (Retake)", color = EmeraldPrimary)
                    }
                }
            }
        } else if (currentEx != null) {
            // Active Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Tamil Context
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = SaffronSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentEx.questionTamilContext,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // English Sentence
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = currentEx.questionPromptEnglish,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { grammarViewModel.speakEnglish(currentEx.questionPromptEnglish) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "ஆங்கிலத்தில் கேள்",
                                    tint = EmeraldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options List
                    val options = currentEx.getOptionsList()
                    options.forEachIndexed { optIndex, optionText ->
                        val isSelected = selectedOption == optIndex
                        val isCorrectOption = optIndex == currentEx.correctOptionIndex

                        val (containerColor, borderColor, textColor) = when {
                            hasChecked && isCorrectOption -> Triple(
                                SuccessGreen.copy(alpha = 0.12f),
                                SuccessGreen,
                                SuccessGreen
                            )
                            hasChecked && isSelected && !isCorrectOption -> Triple(
                                ErrorRed.copy(alpha = 0.1f),
                                ErrorRed,
                                ErrorRed
                            )
                            isSelected -> Triple(
                                EmeraldPrimary.copy(alpha = 0.1f),
                                EmeraldPrimary,
                                EmeraldPrimary
                            )
                            else -> Triple(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = containerColor,
                            border = BorderStroke(1.5.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !hasChecked) {
                                    grammarViewModel.selectOption(optIndex)
                                }
                                .testTag("exercise_opt_$optIndex")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = borderColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected || (hasChecked && isCorrectOption)) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = textColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons (Check Answer vs Next Exercise)
                    if (!hasChecked) {
                        Button(
                            onClick = { grammarViewModel.checkAnswer() },
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("விடையை சரிபார் (Check Answer)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { grammarViewModel.nextExercise() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(
                                text = if (currentIndex + 1 >= exercises.size) "தொகுதியை நிறைவு செய் (Finish Module)" else "அடுத்த பயிற்சி (Next Exercise)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Detailed Teacher Explanation Card
            AnimatedVisibility(visible = hasChecked) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOption == currentEx.correctOptionIndex) SuccessGreen.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedOption == currentEx.correctOptionIndex) SuccessGreen.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = SaffronSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ஆசிரியை விளக்கம் (Teacher's Note):",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentEx.explanationTamil,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "English Rule: ${currentEx.explanationEnglish}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
