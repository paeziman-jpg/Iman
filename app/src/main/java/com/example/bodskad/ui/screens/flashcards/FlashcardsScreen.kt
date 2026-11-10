package com.example.bodskad.ui.screens.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodskad.data.model.TibetanEntry

@Composable
fun FlashcardsScreen(
  viewModel: FlashcardsViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val flashcards = viewModel.getFilteredCards()

  val categories = listOf(
    "All",
    "Greetings & Etiquette",
    "Dharma & Philosophy",
    "Hospitality & Food",
    "Family & Honorifics",
    "Daily Life & Travel"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val streakDays by viewModel.streakDays.collectAsState()
    val isStreakActiveToday by viewModel.isStreakActiveToday.collectAsState()

    // Streak Status Banner in Flashcards
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
      else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 6.dp, bottom = 4.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🔥", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$streakDays-Day Streak",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isStreakActiveToday) "• Extended Today!" else "• Review 3 cards to extend",
            style = MaterialTheme.typography.labelSmall,
            color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        }

        if (!isStreakActiveToday) {
          Button(
            onClick = { viewModel.completeSessionManually() },
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.testTag("complete_session_button")
          ) {
            Text("Complete", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }

    // Header Row: Session Reviews, Front Language Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, bottom = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Reviewed in session: ${uiState.cardsReviewedThisSession}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      OutlinedButton(
        onClick = { viewModel.toggleFrontLanguage() },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("toggle_front_language_button")
      ) {
        Icon(
          imageVector = Icons.Default.FlipCameraAndroid,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (uiState.isTibetanFront) "Front: Tibetan" else "Front: English",
          style = MaterialTheme.typography.labelSmall
        )
      }
    }

    // Category Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      categories.forEach { cat ->
        val isSelected = cat == uiState.selectedCategory
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.onCategorySelected(cat) },
          label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
          shape = RoundedCornerShape(8.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
      }
    }

    if (flashcards.isEmpty()) {
      // Empty State
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Celebration,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "No Flashcards in this Deck",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Add words to your flashcards from the Dictionary or Translation tab to begin Spaced Repetition study.",
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      val currentIndex = uiState.currentCardIndex.coerceIn(0, flashcards.size - 1)
      val currentCard = flashcards[currentIndex]

      // Progress Bar
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { (currentIndex + 1).toFloat() / flashcards.size },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Card ${currentIndex + 1} of ${flashcards.size}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "Mastery: ${when (currentCard.srsMasteryLevel) {
            0 -> "🌱 New"
            1 -> "📖 Learning"
            2 -> "⚡ Review"
            else -> "🏆 Mastered"
          }}",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
      }

      // 3D Flipping Flashcard Box
      val rotation by animateFloatAsState(
        targetValue = if (uiState.isCardFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_rotation"
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(vertical = 8.dp)
          .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density
          }
          .clickable { viewModel.flipCard() }
          .testTag("flashcard_container"),
        contentAlignment = Alignment.Center
      ) {
        if (rotation <= 90f) {
          // Front Side
          FlashcardFront(
            card = currentCard,
            isTibetanFront = uiState.isTibetanFront
          )
        } else {
          // Back Side (flipped)
          FlashcardBack(
            card = currentCard,
            isTibetanFront = uiState.isTibetanFront,
            onPlayAudio = { viewModel.playAudio(currentCard) },
            modifier = Modifier.graphicsLayer { rotationY = 180f }
          )
        }
      }

      // SRS Action Buttons Row (visible when card is flipped)
      if (uiState.isCardFlipped) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "How well did you know this?",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.answerCard(currentCard, 0) },
              modifier = Modifier
                .weight(1f)
                .testTag("srs_again_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC93B3B))
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Again", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("<10m", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
              }
            }

            Button(
              onClick = { viewModel.answerCard(currentCard, 1) },
              modifier = Modifier
                .weight(1f)
                .testTag("srs_hard_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD47C28))
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hard", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("1d", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
              }
            }

            Button(
              onClick = { viewModel.answerCard(currentCard, 2) },
              modifier = Modifier
                .weight(1f)
                .testTag("srs_good_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Good", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("3d", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
              }
            }

            Button(
              onClick = { viewModel.answerCard(currentCard, 3) },
              modifier = Modifier
                .weight(1f)
                .testTag("srs_easy_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C7D64))
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Easy", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text("7d", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
              }
            }
          }
        }
      } else {
        // Tap to Flip Prompt
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp)
            .clickable { viewModel.flipCard() },
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Tap card to reveal answer & cultural note",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // Streak Extension Celebration Dialog
    if (uiState.showStreakCelebration) {
      androidx.compose.material3.AlertDialog(
        onDismissRequest = { viewModel.dismissStreakCelebration() },
        shape = RoundedCornerShape(24.dp),
        icon = {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🔥", fontSize = 32.sp)
          }
        },
        title = {
          Text(
            text = "Daily Streak Extended!",
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
              text = "${uiState.celebrationStreakCount}-Day Stupa Streak བཀྲ་ཤིས་བདེ་ལེགས།",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "You've successfully completed today's vocabulary flashcard session. Your streak counter is updated and displayed prominently on your home screen!",
              style = MaterialTheme.typography.bodySmall,
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { viewModel.dismissStreakCelebration() },
            modifier = Modifier.testTag("dismiss_streak_celebration_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Keep Practicing")
          }
        }
      )
    }
  }
}

@Composable
private fun FlashcardFront(
  card: TibetanEntry,
  isTibetanFront: Boolean
) {
  Card(
    modifier = Modifier.fillMaxSize(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Text(
          text = card.category,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isTibetanFront) {
          Text(
            text = card.tibetan,
            style = MaterialTheme.typography.headlineLarge.copy(
              fontSize = 36.sp,
              lineHeight = 44.sp,
              fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Wylie: ${card.wylie}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          Text(
            text = card.english,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Text(
        text = "Tap to Flip",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
      )
    }
  }
}

@Composable
private fun FlashcardBack(
  card: TibetanEntry,
  isTibetanFront: Boolean,
  onPlayAudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxSize(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ) {
          Text(
            text = "Part of Speech: ${card.partOfSpeech}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        IconButton(onClick = onPlayAudio, modifier = Modifier.size(36.dp)) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Speak",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = card.tibetan,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(
          modifier = Modifier.padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "🔊 ${card.phonetic}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "(${card.wylie})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Text(
          text = card.english,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(top = 4.dp)
        )

        if (card.honorificForm.isNotBlank()) {
          Text(
            text = "Honorific (Zhe-sa): ${card.honorificForm}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }

      // Cultural Note Summary inside Flashcard
      if (card.culturalContext.isNotBlank()) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = card.culturalContext,
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.5.sp,
              lineHeight = 16.sp,
              maxLines = 3,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }
}
