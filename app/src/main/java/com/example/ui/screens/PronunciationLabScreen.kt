package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.AcousticWaveformPitchCanvas
import com.example.ui.components.AudioWaveform
import com.example.ui.components.PronunciationMetricsCanvas
import com.example.ui.components.PronunciationTrendCanvas
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SkyTertiary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PronunciationLabScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exercises by viewModel.pronunciationExercises.collectAsState()
    val currentIndex by viewModel.currentPronIndex.collectAsState()
    val feedback by viewModel.pronFeedback.collectAsState()
    val isEvaluating by viewModel.isEvaluatingPron.collectAsState()
    val isMicListening by viewModel.isMicListening.collectAsState()
    val audioRms by viewModel.audioRms.collectAsState()
    val liveSpokenText by viewModel.liveSpokenText.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("அனைத்தும்") }
    val scrollState = rememberScrollState()

    val currentEx = exercises.getOrNull(currentIndex)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.initSpeechHelper(context)
            viewModel.toggleMicListening(forPronunciation = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "உச்சரிப்புப் பயிலரங்கம் (Pronunciation Lab)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "பவி டீச்சரின் ஒலி அலைவரிசை & சுருதி பகுப்பாய்வு",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Locality and Phonetics Filter Chips
                val filterCategories = listOf("அனைத்தும்", "உள்ளூர் உரையாடல்கள் (Locality)", "ஒலி வேறுபாடு (Phonetics)")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    itemsIndexed(filterCategories) { _, cat ->
                        val isSelected = cat == selectedCategoryFilter
                        AssistChip(
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, EmeraldPrimary) else null
                        )
                    }
                }
            }
        }

        if (currentEx != null) {
            // Main Target Phrase Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Sound focus pill (Locality or Phonetic sound)
                    Surface(
                        color = if (currentEx.soundFocus.contains("Locality")) SaffronSecondary.copy(alpha = 0.15f) else SkyTertiary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (currentEx.soundFocus.contains("Locality")) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = SaffronSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = currentEx.soundFocus,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (currentEx.soundFocus.contains("Locality")) SaffronSecondary else SkyTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // English Phrase
                    Text(
                        text = "\"${currentEx.phrase}\"",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tamil Meaning & Phonetics
                    Text(
                        text = currentEx.tamilMeaning,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentEx.phoneticTamil,
                        style = MaterialTheme.typography.bodySmall,
                        color = SaffronSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Listen Buttons (Normal vs Slow)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.speakTeacherEnglish(currentEx.phrase, slow = false) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("கேள் (1.0x)", color = EmeraldPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.speakTeacherEnglish(currentEx.phrase, slow = true) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SaffronSecondary)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SlowMotionVideo,
                                contentDescription = null,
                                tint = SaffronSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("மெதுவாக (0.75x)", color = SaffronSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Common Tamil Mistake Notice
                    Surface(
                        color = SaffronSecondary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SaffronSecondary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = SaffronSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentEx.commonTamilMistakeNotice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Big Voice Practice Record Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(if (isMicListening) SaffronSecondary else EmeraldPrimary)
                                .clickable {
                                    val hasPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPerm) {
                                        viewModel.toggleMicListening(forPronunciation = true)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .testTag("record_pronunciation_button")
                        ) {
                            Icon(
                                imageVector = if (isMicListening) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = "உச்சரிக்க பேசுங்கள்",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isMicListening) "பேசுங்கள்... கேட்கிறது!" else "மைக் அழுத்தி இந்த வாக்கியத்தை பேசவும்",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isMicListening) SaffronSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Live audio waveform
                        if (isMicListening) {
                            AudioWaveform(
                                isActive = true,
                                rmsLevel = audioRms,
                                barColor = SaffronSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Test with sample speech quick chips (for testing on emulator without physical mic)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "டெஸ்ட் தேர்வு:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.manualPronunciationTest(currentEx.phrase)
                            }
                        ) {
                            Text(
                                text = "சரியான உச்சரிப்பு",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.manualPronunciationTest(currentEx.phrase.split(" ").firstOrNull() ?: "")
                            }
                        ) {
                            Text(
                                text = "அரைகுறை முயற்சி",
                                style = MaterialTheme.typography.labelSmall,
                                color = SaffronSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Custom Canvas Graph 1: Acoustic Waveform & Pitch Contour Graph
            val currentScore = feedback?.overallScore ?: 86
            AcousticWaveformPitchCanvas(
                userScore = currentScore,
                rmsLevel = audioRms,
                isListening = isMicListening
            )

            // AI Evaluating Progress Spinner
            if (isEvaluating) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "பவி டீச்சர் உச்சரிப்பை ஆராய்கிறார்...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Evaluation Result Card with Word-by-Word Analysis & Praise
            AnimatedVisibility(visible = feedback != null) {
                feedback?.let { fb ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Accuracy score header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (fb.overallScore >= 80) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                        contentDescription = null,
                                        tint = if (fb.overallScore >= 80) SuccessGreen else SaffronSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "துல்லியத்தன்மை (Accuracy)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    color = if (fb.overallScore >= 80) SuccessGreen.copy(alpha = 0.15f) else SaffronSecondary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${fb.overallScore}%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        ),
                                        color = if (fb.overallScore >= 80) SuccessGreen else SaffronSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Word-by-word acoustic breakdown
                            Text(
                                text = "வார்த்தை வாரியான மதிப்பீடு (Word Analysis):",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                fb.wordAccuracies.forEach { wordAcc ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (wordAcc.isCorrect) SuccessGreen.copy(alpha = 0.12f) else ErrorRed.copy(alpha = 0.1f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (wordAcc.isCorrect) SuccessGreen else ErrorRed
                                        )
                                    ) {
                                        Text(
                                            text = "${if (wordAcc.isCorrect) "✓" else "✕"} ${wordAcc.word}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (wordAcc.isCorrect) SuccessGreen else ErrorRed,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Praise and coaching advice in Tamil
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = fb.praiseTamil,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = fb.improvementTamil,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Next Exercise button
                            Button(
                                onClick = { viewModel.nextPronunciationExercise() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("next_pronunciation_button")
                            ) {
                                Text(
                                    text = "அடுத்த வாக்கியம் (Next Exercise)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Custom Canvas Graph 2: Pronunciation Dimension Breakdown
            PronunciationMetricsCanvas(
                overallScore = currentScore
            )

            // Custom Canvas Graph 3: Historical Progress Trend Line
            PronunciationTrendCanvas()
        }
    }
}
