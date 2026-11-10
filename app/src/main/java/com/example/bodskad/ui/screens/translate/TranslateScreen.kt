package com.example.bodskad.ui.screens.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodskad.ui.components.CultureNoteCard
import com.example.bodskad.ui.components.DialectSelector
import com.example.bodskad.ui.components.VoiceInputDialog

@Composable
fun TranslateScreen(
  viewModel: TranslateViewModel,
  modifier: Modifier = Modifier,
  onNavigateToFlashcards: () -> Unit = {}
) {
  val uiState by viewModel.uiState.collectAsState()
  val recentHistory by viewModel.recentTranslations.collectAsState()
  val streakDays by viewModel.streakDays.collectAsState()
  val isStreakActiveToday by viewModel.isStreakActiveToday.collectAsState()
  val context = LocalContext.current
  var showVoiceDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Prominent Home Daily Streak Counter Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, bottom = 4.dp)
        .clickable { onNavigateToFlashcards() }
        .testTag("home_streak_banner"),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isStreakActiveToday) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      ),
      border = BorderStroke(
        1.5.dp,
        if (isStreakActiveToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                if (isStreakActiveToday) MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🔥", fontSize = 24.sp)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "$streakDays-Day Stupa Streak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
              ) {
                Text(
                  text = if (isStreakActiveToday) "Active Today" else "Needs Study",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = if (isStreakActiveToday) "Great work! Flashcard session completed today."
              else "Complete a vocabulary flashcard session to extend your streak!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (!isStreakActiveToday) {
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = onNavigateToFlashcards,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("home_streak_study_button")
          ) {
            Text("Study", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
    // Header & Language Switcher Row
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Source Language
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (uiState.sourceLang == "en") "English" else "བོད་སྐད (Tibetan)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        // Swap Button
        IconButton(
          onClick = { viewModel.swapLanguages() },
          modifier = Modifier.testTag("swap_languages_button")
        ) {
          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap Source and Target Language",
            tint = MaterialTheme.colorScheme.primary
          )
        }

        // Target Language
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (uiState.sourceLang == "en") "བོད་སྐད (Tibetan)" else "English",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // Dialect Nuance Selector
    DialectSelector(
      selectedDialect = uiState.selectedDialect,
      onDialectSelected = { viewModel.onDialectSelected(it) },
      modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Input Text Field
      item {
        OutlinedTextField(
          value = uiState.sourceText,
          onValueChange = { viewModel.onSourceTextChanged(it) },
          label = {
            Text(if (uiState.sourceLang == "en") "Enter English phrase or word" else "Enter Tibetan script or Wylie")
          },
          placeholder = {
            Text(if (uiState.sourceLang == "en") "e.g., Hello, Thank you, Compassion..." else "e.g., བཀྲ་ཤིས་བདེ་ལེགས།, tashi delek")
          },
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (uiState.sourceText.isNotBlank()) {
                IconButton(
                  onClick = { viewModel.onSourceTextChanged("") },
                  modifier = Modifier.testTag("clear_text_button")
                ) {
                  Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear input")
                }
              }
              FilledIconButton(
                onClick = { showVoiceDialog = true },
                colors = IconButtonDefaults.filledIconButtonColors(
                  containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                  .padding(end = 4.dp)
                  .size(36.dp)
                  .testTag("voice_input_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Voice Input",
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("translation_input_field")
        )
      }

      // Translation Result Card
      val res = uiState.translationResult
      if (res != null && res.translatedText.isNotBlank()) {
        item {
          ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("translation_result_card")
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              // Top Action Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                  Text(
                    text = "${res.dialect.displayName} Nuance",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  // Speak TTS button
                  IconButton(
                    onClick = { viewModel.playAudio() },
                    modifier = Modifier.testTag("audio_tts_button")
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                      contentDescription = "Listen Pronunciation",
                      tint = MaterialTheme.colorScheme.primary
                    )
                  }

                  // Copy text button
                  IconButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("Tibetan Translation", res.translatedText)
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "Copied translation to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_translation_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentCopy,
                      contentDescription = "Copy Translation",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  // Favorite toggle if matched entry exists
                  res.matchedEntry?.let { entry ->
                    IconButton(
                      onClick = { viewModel.toggleFavorite(entry) },
                      modifier = Modifier.testTag("favorite_toggle_button")
                    ) {
                      Icon(
                        imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }

                    IconButton(
                      onClick = {
                        viewModel.toggleFlashcard(entry)
                        Toast.makeText(
                          context,
                          if (!entry.isFlashcard) "Added to study flashcards" else "Removed from flashcards",
                          Toast.LENGTH_SHORT
                        ).show()
                      },
                      modifier = Modifier.testTag("flashcard_add_button")
                    ) {
                      Icon(
                        imageVector = if (entry.isFlashcard) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Flashcards",
                        tint = if (entry.isFlashcard) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Translated Main Text
              Text(
                text = res.translatedText,
                style = if (uiState.sourceLang == "en") {
                  MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold
                  )
                } else {
                  MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                },
                color = MaterialTheme.colorScheme.onSurface
              )

              // Phonetics & Transliteration
              if (res.phonetic.isNotBlank() || res.wylie.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  if (res.phonetic.isNotBlank()) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                      Text(
                        text = "🔊 ${res.phonetic}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  if (res.wylie.isNotBlank()) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                      Text(
                        text = "Wylie: ${res.wylie}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }
                }
              }

              // Dialect nuance note
              if (res.dialectNuanceNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = res.dialectNuanceNote,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // Cultural Context Note Card (MANDATORY REQUIREMENT)
        item {
          CultureNoteCard(
            culturalNote = res.culturalContext,
            honorificText = res.honorificEquivalent,
            initiallyExpanded = true
          )
        }
      }

      // Recent Translations History Section
      if (recentHistory.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Recent Interactions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            TextButton(
              onClick = { viewModel.clearHistory() },
              modifier = Modifier.testTag("clear_history_button")
            ) {
              Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        items(recentHistory.take(5)) { item ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.onSourceTextChanged(item.sourceText) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.sourceText,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = item.translatedText,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = item.dialect.uppercase(),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Voice Interaction Dialog
  if (showVoiceDialog) {
    VoiceInputDialog(
      onDismiss = { showVoiceDialog = false },
      onSpeechResult = { phrase ->
        viewModel.onSourceTextChanged(phrase)
      },
      sourceLang = uiState.sourceLang
    )
  }
}
