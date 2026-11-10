package com.example.bodskad.ui.screens.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressQuizDialog(
  viewModel: ProgressViewModel,
  uiState: ProgressUiState
) {
  if (!uiState.isQuizActive) return

  if (uiState.quizFinished) {
    // Finished Quiz Summary
    AlertDialog(
      onDismissRequest = { viewModel.closeQuiz() },
      shape = RoundedCornerShape(24.dp),
      icon = {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(48.dp)
        )
      },
      title = {
        Text(
          text = "Quiz Completed! བཀྲ་ཤིས་བདེ་ལེགས།",
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Score: ${uiState.quizScore} / ${viewModel.quizQuestions.size} correct",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "+${uiState.quizScore * 30} XP Earned • Overall Accuracy: ${uiState.overallAccuracyPercent}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Your translation accuracy and lesson completion statistics have been updated in your dashboard charts.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { viewModel.closeQuiz() },
          modifier = Modifier.testTag("quiz_finish_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("View Updated Analytics")
        }
      }
    )
    return
  }

  val currentQ = viewModel.quizQuestions[uiState.currentQuizQuestionIndex]

  AlertDialog(
    onDismissRequest = { viewModel.closeQuiz() },
    shape = RoundedCornerShape(24.dp),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Accuracy Practice",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Question ${uiState.currentQuizQuestionIndex + 1} of ${viewModel.quizQuestions.size}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = { viewModel.closeQuiz() }) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close Quiz")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        LinearProgressIndicator(
          progress = { (uiState.currentQuizQuestionIndex + 1).toFloat() / viewModel.quizQuestions.size },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = MaterialTheme.colorScheme.primary
        )

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
          Text(
            text = currentQ.category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Text(
          text = currentQ.questionText,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        if (currentQ.phoneticOrWylie.isNotBlank()) {
          Text(
            text = currentQ.phoneticOrWylie,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }

        // Multiple Choice Options
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          currentQ.options.forEachIndexed { idx, opt ->
            val isSelected = uiState.selectedQuizAnswerIndex == idx
            val isCorrect = idx == currentQ.correctOptionIndex

            val containerColor = when {
              !uiState.isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
              uiState.isAnswerSubmitted && isCorrect -> Color(0xFFE8F5E9)
              uiState.isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFFFEBEE)
              else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
              !uiState.isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primary
              uiState.isAnswerSubmitted && isCorrect -> Color(0xFF2E7D32)
              uiState.isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFC62828)
              else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_option_$idx"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = containerColor),
              border = BorderStroke(1.5.dp, borderColor),
              onClick = { viewModel.selectQuizAnswer(idx) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${('A' + idx)}. ",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = opt,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = if (isSelected || (uiState.isAnswerSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // Cultural Hint after submission
        AnimatedVisibility(visible = uiState.isAnswerSubmitted) {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = currentQ.culturalHint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
          }
        }
      }
    },
    confirmButton = {
      if (!uiState.isAnswerSubmitted) {
        Button(
          onClick = { viewModel.submitQuizAnswer() },
          enabled = uiState.selectedQuizAnswerIndex != null,
          modifier = Modifier.testTag("quiz_submit_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Check Answer")
        }
      } else {
        Button(
          onClick = { viewModel.nextQuizQuestion() },
          modifier = Modifier.testTag("quiz_next_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Continue")
        }
      }
    }
  )
}
