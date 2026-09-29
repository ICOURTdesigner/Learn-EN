package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.ui.components.AudioWaveform
import com.example.ui.components.TeacherAvatarView
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SkyTertiary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun VoiceClassroomScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTtsSpeaking by viewModel.isTtsSpeaking.collectAsState()
    val isMicListening by viewModel.isMicListening.collectAsState()
    val audioRms by viewModel.audioRms.collectAsState()
    val isTeacherThinking by viewModel.isTeacherThinking.collectAsState()
    val selectedScenario by viewModel.selectedScenario.collectAsState()
    val manualInputText by viewModel.manualInputText.collectAsState()
    val liveSpokenText by viewModel.liveSpokenText.collectAsState()
    val feedbackLanguage by viewModel.feedbackLanguage.collectAsState()

    var showKeyboardInput by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Permission launcher for audio recording
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.initSpeechHelper(context)
            viewModel.toggleMicListening()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initSpeechHelper(context)
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Teacher Avatar Header & Status
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    TeacherAvatarView(
                        isSpeaking = isTtsSpeaking,
                        isListening = isMicListening
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Language Delivery Mode Toggle Pill
                    val isTamilFeedback = feedbackLanguage == com.example.data.model.FeedbackLanguage.TAMIL
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isTamilFeedback) EmeraldPrimary.copy(alpha = 0.12f) else SaffronSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isTamilFeedback) EmeraldPrimary.copy(alpha = 0.4f) else SaffronSecondary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clickable { viewModel.toggleFeedbackLanguage() }
                            .padding(bottom = 6.dp)
                            .testTag("classroom_lang_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Translate,
                                contentDescription = null,
                                tint = if (isTamilFeedback) EmeraldPrimary else SaffronSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTamilFeedback) "பவி விளக்கம்: தமிழ் (Tamil)" else "Pavi Feedback: English",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isTamilFeedback) EmeraldPrimary else SaffronSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                // Scenario selector horizontal chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.scenarios) { scenario ->
                        val isSelected = scenario == selectedScenario
                        AssistChip(
                            onClick = { viewModel.setScenario(scenario) },
                            label = {
                                Text(
                                    text = scenario,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null
                        )
                    }
                }
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onPlayEnglish = { viewModel.speakTeacherEnglish(msg.englishText) },
                    onPlayTamil = { viewModel.speakTeacherTamil(msg.tamilTranslation) }
                )
            }

            if (isTeacherThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = EmeraldPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "பவி டீச்சர் சிந்திக்கிறார்...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live spoken transcript banner if user is speaking
        AnimatedVisibility(visible = isMicListening || liveSpokenText.isNotBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    AudioWaveform(
                        isActive = isMicListening,
                        rmsLevel = audioRms,
                        barColor = SaffronSecondary,
                        modifier = Modifier.width(36.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (liveSpokenText.isNotBlank()) liveSpokenText else "நீங்கள் பேசுவது கேட்கிறது... ஆங்கிலத்தில் பேசுங்கள்!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Keyboard text input drawer (if toggled)
        AnimatedVisibility(visible = showKeyboardInput) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = manualInputText,
                    onValueChange = { viewModel.updateManualInput(it) },
                    placeholder = { Text("ஆங்கிலத்தில் தட்டச்சு செய்யவும்...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { viewModel.sendManualMessage() },
                    modifier = Modifier.testTag("send_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "அனுப்பு", tint = Color.White)
                }
            }
        }

        // Bottom Voice Control Bar (Supernova AI Style)
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Quick suggested Locality & Daily Tamil starters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    val quickPrompts = listOf(
                        "Could you drop me near the bus stop?",
                        "Can I pay through UPI QR code?",
                        "Take the second left after the signal.",
                        "One strong filter coffee with less sugar.",
                        "Is this the visitor parking area?",
                        "How much will you charge to Central?",
                        "Tell me about yourself."
                    )
                    items(quickPrompts) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.updateManualInput(prompt)
                                viewModel.sendManualMessage()
                            }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Toggle Keyboard
                    IconButton(
                        onClick = { showKeyboardInput = !showKeyboardInput },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Keyboard,
                            contentDescription = "தட்டச்சு பலகை",
                            tint = if (showKeyboardInput) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Main Big Mic Button (Supernova AI style)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = if (isMicListening) listOf(SaffronSecondary, Color(0xFFB45309))
                                    else listOf(EmeraldPrimary, Color(0xFF0D5A47))
                                )
                            )
                            .clickable {
                                val hasAudioPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasAudioPermission) {
                                    viewModel.toggleMicListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                            .testTag("voice_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isMicListening) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (isMicListening) "நிறுத்து" else "குரல் பதிவு",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Audio Mute/Stop button
                    IconButton(
                        onClick = { viewModel.stopAudio() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isTtsSpeaking) Icons.Filled.Stop else Icons.Filled.Campaign,
                            contentDescription = "ஒலியை நிறுத்து",
                            tint = if (isTtsSpeaking) SaffronSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (isMicListening) "பேசுங்கள்... ஆங்கிலத்தில் பேசுங்கள்!" else "பேச மைக் பட்டனை தொடவும் (Tap to Speak)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Floating Action Button to switch Pavi's feedback delivery language
    val isTamilFab = feedbackLanguage == com.example.data.model.FeedbackLanguage.TAMIL
    ExtendedFloatingActionButton(
        onClick = { viewModel.toggleFeedbackLanguage() },
        icon = {
            Icon(
                imageVector = Icons.Filled.Translate,
                contentDescription = "மொழி மாற்றம்",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        },
        text = {
            Text(
                text = if (isTamilFab) "விளக்கம்: தமிழ்" else "Feedback: English",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        },
        containerColor = if (isTamilFab) EmeraldPrimary else SaffronSecondary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 125.dp)
            .testTag("language_switch_fab")
    )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onPlayEnglish: () -> Unit,
    onPlayTamil: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == ChatSender.USER

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            Surface(
                color = EmeraldPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.englishText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.widthIn(max = 340.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // English Phrase Box
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = message.englishText,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onPlayEnglish,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "ஆங்கிலத்தில் கேள்",
                                    tint = EmeraldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tamil Explanation
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = null,
                            tint = SaffronSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = message.tamilTranslation,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Grammar Tip if present
                    message.grammarTipTamil?.let { tip ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = SaffronSecondary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 இலக்கண குறிப்பு: $tip",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Phonetic Tip if present
                    message.pronunciationTipTamil?.let { pronTip ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = SkyTertiary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🗣️ உச்சரிப்பு டிப்ஸ்: $pronTip",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
