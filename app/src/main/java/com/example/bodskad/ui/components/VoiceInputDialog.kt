package com.example.bodskad.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceInputDialog(
  onDismiss: () -> Unit,
  onSpeechResult: (String) -> Unit,
  sourceLang: String
) {
  val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val quickPhrases = if (sourceLang == "en") {
    listOf(
      "Hello blessings",
      "Thank you",
      "How are you?",
      "Tibetan butter tea",
      "Compassion and wisdom",
      "Where is the monastery?",
      "Excuse me"
    )
  } else {
    listOf(
      "བཀྲ་ཤིས་བདེ་ལེགས།",
      "ཐུགས་རྗེ་ཆེ།",
      "སྐུ་གཟུགས་བདེ་པོ་ཡིན་པས།",
      "བོད་ཇ།",
      "སྙིང་རྗེ།",
      "དགོན་པ།"
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (sourceLang == "en") "Listening in English..." else "བོད་སྐད་གསུང་རོགས། (Listening in Tibetan...)",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Animated Microphone Halo
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(110.dp)
            .padding(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .scale(pulseScale)
              .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = CircleShape
              )
          )
          FilledIconButton(
            onClick = { /* Stop or re-trigger */ },
            modifier = Modifier
              .size(64.dp)
              .testTag("voice_mic_active_button"),
            colors = IconButtonDefaults.filledIconButtonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Microphone Active",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Speak clearly into your microphone.\nOr select a phrase below for instant voice testing:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Suggested Voice Phrases:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          quickPhrases.forEach { phrase ->
            AssistChip(
              onClick = {
                onSpeechResult(phrase)
                onDismiss()
              },
              label = {
                Text(
                  text = phrase,
                  style = MaterialTheme.typography.labelSmall
                )
              },
              colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("voice_dialog_close")
      ) {
        Text("Done")
      }
    }
  )
}
