package com.example.bodskad.ui.screens.dictionary

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.ui.components.CultureNoteCard

@Composable
fun DictionaryScreen(
  viewModel: DictionaryViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val entries by viewModel.entries.collectAsState()
  val context = LocalContext.current

  val categories = listOf(
    "All",
    "Greetings & Etiquette",
    "Dharma & Philosophy",
    "Daily Life & Travel",
    "Hospitality & Food",
    "Family & Honorifics",
    "Numbers & Calendar",
    "Wisdom & Idioms"
  )

  val difficulties = listOf("All", "Beginner", "Intermediate", "Advanced")

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Search Bar
    OutlinedTextField(
      value = uiState.searchQuery,
      onValueChange = { viewModel.onSearchQueryChanged(it) },
      placeholder = { Text("Search English, Tibetan, Wylie, or Phonetics...") },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = MaterialTheme.colorScheme.primary
        )
      },
      trailingIcon = {
        if (uiState.searchQuery.isNotBlank()) {
          IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
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
        .padding(top = 8.dp)
        .testTag("dictionary_search_field")
    )

    // Category Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.forEach { cat ->
        val isSelected = cat == uiState.selectedCategory
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.onCategorySelected(cat) },
          label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("category_chip_${cat.replace(" ", "_")}")
        )
      }
    }

    // Difficulty Filter Chips & Count
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        difficulties.forEach { diff ->
          val isSelected = diff == uiState.selectedDifficulty
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.onDifficultySelected(diff) },
            label = { Text(diff, style = MaterialTheme.typography.labelSmall) },
            shape = RoundedCornerShape(8.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
          )
        }
      }

      Text(
        text = "${entries.size} entries",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Dictionary Items List
    if (entries.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "🔍 No entries found",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "Try searching with a different keyword or select 'All' categories.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(entries, key = { it.id }) { entry ->
          DictionaryEntryCard(
            entry = entry,
            isExpanded = uiState.expandedEntryId == entry.id,
            onToggleExpand = { viewModel.toggleEntryExpanded(entry.id) },
            onPlayAudio = { viewModel.playAudio(entry) },
            onToggleFavorite = { viewModel.toggleFavorite(entry) },
            onToggleFlashcard = {
              viewModel.toggleFlashcard(entry)
              Toast.makeText(
                context,
                if (!entry.isFlashcard) "Added to flashcards" else "Removed from flashcards",
                Toast.LENGTH_SHORT
              ).show()
            }
          )
        }
        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
fun DictionaryEntryCard(
  entry: TibetanEntry,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onPlayAudio: () -> Unit,
  onToggleFavorite: () -> Unit,
  onToggleFlashcard: () -> Unit
) {
  val arrowRotate by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow")

  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dictionary_card_${entry.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onToggleExpand() }
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Tibetan Uchen Script
          Text(
            text = entry.tibetan,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Phonetics & Wylie
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = "🔊 ${entry.phonetic}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Text(
              text = entry.wylie,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // English Translation
          Text(
            text = entry.english,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Action Buttons Row
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onPlayAudio, modifier = Modifier.size(36.dp)) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Speak",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
            Icon(
              imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Favorite",
              tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(onClick = onToggleFlashcard, modifier = Modifier.size(36.dp)) {
            Icon(
              imageVector = if (entry.isFlashcard) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Flashcard",
              tint = if (entry.isFlashcard) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "Expand",
            modifier = Modifier
              .size(24.dp)
              .rotate(arrowRotate),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Tags Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = entry.category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = entry.partOfSpeech,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        if (entry.honorificForm.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Text(
              text = "ཞེ་ས Zhe-sa",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Expanded Detail Section
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HorizontalDivider()

          // Etymology / Syllables
          if (entry.etymology.isNotBlank()) {
            Column {
              Text(
                text = "Syllable Etymology:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = entry.etymology,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Dialect Nuances
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Dialect Variations:",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            if (entry.lhasaNuance.isNotBlank()) {
              Text(
                text = "• Lhasa (Central): ${entry.lhasaNuance}",
                style = MaterialTheme.typography.bodySmall
              )
            }
            if (entry.amdoNuance.isNotBlank()) {
              Text(
                text = "• Amdo: ${entry.amdoNuance}",
                style = MaterialTheme.typography.bodySmall
              )
            }
            if (entry.khamNuance.isNotBlank()) {
              Text(
                text = "• Kham: ${entry.khamNuance}",
                style = MaterialTheme.typography.bodySmall
              )
            }
            if (entry.dharmaNuance.isNotBlank()) {
              Text(
                text = "• Dharma / Classical: ${entry.dharmaNuance}",
                style = MaterialTheme.typography.bodySmall
              )
            }
          }

          // Cultural Context & Etiquette Note
          if (entry.culturalContext.isNotBlank()) {
            CultureNoteCard(
              culturalNote = entry.culturalContext,
              honorificText = entry.honorificForm,
              initiallyExpanded = true
            )
          }

          // Example Sentence
          if (entry.exampleTibetan.isNotBlank()) {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Example Usage:",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = entry.exampleTibetan,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = entry.exampleEnglish,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}
