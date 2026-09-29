package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaffronSecondary

@Composable
fun TeacherAvatarView(
    isSpeaking: Boolean,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking || isListening) 1.15f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isSpeaking || isListening) 0.15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Outer glowing ring
            if (isSpeaking || isListening) {
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isSpeaking) EmeraldPrimary.copy(alpha = pulseAlpha)
                            else SaffronSecondary.copy(alpha = pulseAlpha)
                        )
                )
            }

            // Inner avatar image container
            Surface(
                shape = CircleShape,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(105.dp)
                    .border(
                        width = 3.dp,
                        color = when {
                            isSpeaking -> EmeraldPrimary
                            isListening -> SaffronSecondary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.teacher_avatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = "பவி டீச்சர் (Pavi Teacher - AI English Coach)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(105.dp)
                        .clip(CircleShape)
                )
            }
        }

        // Status badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = when {
                isSpeaking -> EmeraldPrimary
                isListening -> SaffronSecondary
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = when {
                        isSpeaking -> Icons.Filled.RecordVoiceOver
                        isListening -> Icons.Filled.Mic
                        else -> Icons.Filled.School
                    },
                    contentDescription = null,
                    tint = if (isSpeaking || isListening) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isSpeaking -> "பவி டீச்சர் பேசுகிறார்..."
                        isListening -> "உங்களைக் கேட்கிறார்..."
                        else -> "பவி டீச்சர் • ஆன்லைன்"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = if (isSpeaking || isListening) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
