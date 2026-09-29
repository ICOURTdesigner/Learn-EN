package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.WeakSpotEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SaffronSecondary
import com.example.ui.theme.SkyTertiary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val weakSpots by viewModel.weakSpots.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()

    val streak = userProgress?.dayStreak ?: 3
    val totalMinutes = userProgress?.totalPracticeMinutes ?: 45
    val grammarAnswered = userProgress?.totalGrammarAnswered ?: 30
    val grammarCorrect = userProgress?.totalGrammarCorrect ?: 25
    val grammarPercent = if (grammarAnswered > 0) ((grammarCorrect.toFloat() / grammarAnswered.toFloat()) * 100).toInt() else 80
    val pronAvg = userProgress?.avgPronunciationScore ?: 86.5f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI முன்னேற்றப் பகுப்பாய்வு",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "AI-Powered Learning Analytics & Mastery Tracking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4 KPI Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        icon = Icons.Filled.Whatshot,
                        title = "தொடர் நாட்கள்",
                        value = "$streak நாட்கள்",
                        accentColor = SaffronSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        icon = Icons.Filled.Timer,
                        title = "பயிற்சி நேரம்",
                        value = "$totalMinutes நிமிடங்கள்",
                        accentColor = SkyTertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        icon = Icons.Filled.School,
                        title = "இலக்கண வெற்றி",
                        value = "$grammarPercent%",
                        accentColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        icon = Icons.Filled.RecordVoiceOver,
                        title = "சராசரி உச்சரிப்பு",
                        value = "${"%.1f".format(pronAvg)}%",
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // AI Teacher's Personalized Assessment
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "பவி டீச்சரின் தனிப்பயன் அறிக்கை (Pavi Teacher Review)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "உங்களின் உச்சரிப்பு மற்றும் வாக்கிய உருவாக்கம் ஒவ்வொரு நாளும் சீராக மேம்பட்டு வருகிறது. வேலை நேர்முகத் தேர்வு மற்றும் தினசரி உரையாடல்களில் நல்ல தன்னம்பிக்கை தெரிகிறது.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "🎯 அடுத்த இலக்கு (Focal Areas):",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "1. 'did' பயன்படுத்தும் போது இறந்த கால வினைச்சொல் தவிர்ப்பது.\n2. ஆங்கில 'V' மற்றும் 'W' ஒலிகளை தெளிவாக வேறுபடுத்துவது.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Weak Spots / AI Detected Mistakes
        item {
            Text(
                text = "AI கண்டறிந்த பலவீனங்கள் & திருத்தங்கள் (Weak Spots)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(weakSpots, key = { it.id }) { weakSpot ->
            WeakSpotCard(
                weakSpot = weakSpot,
                onResolve = { viewModel.resolveWeakSpot(weakSpot.id) }
            )
        }

        // Recent Practice Sessions History
        item {
            Text(
                text = "சமீபத்திய பயிற்சி வரலாறு (Recent Sessions)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(recentSessions, key = { it.id }) { session ->
            PracticeSessionItem(session = session)
        }
    }
}

@Composable
fun KpiCard(
    icon: ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WeakSpotCard(
    weakSpot: WeakSpotEntity,
    onResolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (weakSpot.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (weakSpot.isResolved) SuccessGreen.copy(alpha = 0.4f)
            else ErrorRed.copy(alpha = 0.25f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = weakSpot.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (weakSpot.isResolved) {
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "தேர்ச்சி பெற்றது ✓",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SuccessGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onResolve,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("resolve_weakspot_${weakSpot.id}")
                    ) {
                        Text("புரிந்தது ✓", fontSize = 11.sp, color = EmeraldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mistake vs Correction
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "தவறு: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ErrorRed
                )
                Text(
                    text = weakSpot.mistakePattern,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "சரியானது: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SuccessGreen
                )
                Text(
                    text = weakSpot.correction,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💡 அறிவுரை: ${weakSpot.adviceTamil}",
                style = MaterialTheme.typography.labelSmall,
                color = SaffronSecondary
            )
        }
    }
}

@Composable
fun PracticeSessionItem(
    session: PracticeSessionEntity,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            val (icon, tint) = when (session.sessionType) {
                "VOICE_CLASS" -> Pair(Icons.Filled.RecordVoiceOver, EmeraldPrimary)
                "GRAMMAR" -> Pair(Icons.Filled.School, SaffronSecondary)
                else -> Pair(Icons.Filled.FitnessCenter, SkyTertiary)
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.topicTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = session.summaryTamil,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Surface(
                color = tint.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${session.score}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = tint,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
