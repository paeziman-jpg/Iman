package com.example.bodskad.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodskad.ui.screens.cultural.CulturalGuideScreen
import com.example.bodskad.ui.screens.dictionary.DictionaryScreen
import com.example.bodskad.ui.screens.dictionary.DictionaryViewModel
import com.example.bodskad.ui.screens.flashcards.FlashcardsScreen
import com.example.bodskad.ui.screens.flashcards.FlashcardsViewModel
import com.example.bodskad.ui.screens.progress.ProgressScreen
import com.example.bodskad.ui.screens.progress.ProgressViewModel
import com.example.bodskad.ui.screens.translate.TranslateScreen
import com.example.bodskad.ui.screens.translate.TranslateViewModel

enum class BodskadTab(
  val label: String,
  val tibetanLabel: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  TRANSLATE("Translate", "ལོ་ཙཱ", Icons.Filled.Translate, Icons.Outlined.Translate),
  DICTIONARY("Dictionary", "ཚིག་མཛོད", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
  FLASHCARDS("Flashcards", "སྦྱོང་བྱང", Icons.Filled.School, Icons.Outlined.School),
  CULTURE("Culture", "རིག་གཞུང", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
  PROGRESS("Progress", "གྲུབ་འབྲས", Icons.Filled.BarChart, Icons.Outlined.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  isDarkTheme: Boolean,
  onToggleDarkTheme: () -> Unit,
  translateViewModel: TranslateViewModel = viewModel(),
  dictionaryViewModel: DictionaryViewModel = viewModel(),
  flashcardsViewModel: FlashcardsViewModel = viewModel(),
  progressViewModel: ProgressViewModel = viewModel()
) {
  var selectedTab by remember { mutableStateOf(BodskadTab.TRANSLATE) }
  val streakDays by translateViewModel.streakDays.collectAsState()
  val isStreakActiveToday by translateViewModel.isStreakActiveToday.collectAsState()
  var isAdvancedMode by remember { mutableStateOf(false) }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isWideScreen = maxWidth >= 600.dp

    Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
          title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
              Text(
                text = "བོད་སྐད།",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
              )
              Text(
                text = " Bodskad",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          },
          actions = {
            // Streak Flame Quick Indicator
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
              else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { selectedTab = BodskadTab.FLASHCARDS }
                .padding(end = 6.dp)
                .testTag("topbar_streak_badge")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
              ) {
                Text(text = "🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "$streakDays",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Beginner / Advanced Mode Chip
            FilterChip(
              selected = isAdvancedMode,
              onClick = { isAdvancedMode = !isAdvancedMode },
              label = {
                Text(
                  text = if (isAdvancedMode) "Advanced" else "Beginner",
                  style = MaterialTheme.typography.labelSmall
                )
              },
              modifier = Modifier
                .padding(end = 4.dp)
                .testTag("mode_toggle_chip")
            )

            // Dark Mode Toggle Button
            IconButton(
              onClick = onToggleDarkTheme,
              modifier = Modifier.testTag("dark_mode_toggle_button")
            ) {
              Icon(
                imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                contentDescription = "Toggle Dark Mode",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          },
          colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
          )
        )
      },
      bottomBar = {
        if (!isWideScreen) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
          ) {
            BodskadTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              NavigationBarItem(
                selected = isSelected,
                onClick = { selectedTab = tab },
                icon = {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label
                  )
                },
                label = {
                  Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                  )
                },
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
              )
            }
          }
        }
      }
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        if (isWideScreen) {
          // Tablet / Foldable side navigation rail
          NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight()
          ) {
            BodskadTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              NavigationRailItem(
                selected = isSelected,
                onClick = { selectedTab = tab },
                icon = {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label
                  )
                },
                label = { Text(tab.label) },
                modifier = Modifier.testTag("rail_item_${tab.name.lowercase()}")
              )
            }
          }
        }

        // Screen Content
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f),
          color = MaterialTheme.colorScheme.background
        ) {
          Crossfade(targetState = selectedTab, label = "tab_crossfade") { tab ->
            when (tab) {
              BodskadTab.TRANSLATE -> TranslateScreen(
                viewModel = translateViewModel,
                onNavigateToFlashcards = { selectedTab = BodskadTab.FLASHCARDS }
              )
              BodskadTab.DICTIONARY -> DictionaryScreen(viewModel = dictionaryViewModel)
              BodskadTab.FLASHCARDS -> FlashcardsScreen(viewModel = flashcardsViewModel)
              BodskadTab.CULTURE -> CulturalGuideScreen(
                onPhraseClick = { phrase ->
                  translateViewModel.onSourceTextChanged(phrase)
                  selectedTab = BodskadTab.TRANSLATE
                }
              )
              BodskadTab.PROGRESS -> ProgressScreen(viewModel = progressViewModel)
            }
          }
        }
      }
    }
  }
}
