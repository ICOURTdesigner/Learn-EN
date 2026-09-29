package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaffronSecondary

@Composable
fun AudioWaveform(
    isActive: Boolean,
    rmsLevel: Float = 0f,
    barColor: Color = EmeraldPrimary,
    maxHeight: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    // Multi-phase animations for rhythmic bars
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w4"
    )

    val factors = listOf(
        if (isActive) (wave1 * 0.7f + rmsLevel * 0.3f).coerceIn(0.15f, 1f) else 0.15f,
        if (isActive) (wave2 * 0.6f + rmsLevel * 0.4f).coerceIn(0.2f, 1f) else 0.2f,
        if (isActive) (wave3 * 0.5f + rmsLevel * 0.5f).coerceIn(0.25f, 1f) else 0.25f,
        if (isActive) (wave4 * 0.7f + rmsLevel * 0.3f).coerceIn(0.2f, 1f) else 0.2f,
        if (isActive) (wave1 * 0.8f + rmsLevel * 0.2f).coerceIn(0.15f, 1f) else 0.15f
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(maxHeight)
    ) {
        factors.forEach { factor ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(fraction = factor)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}
