package com.example.bodskad.ui.screens.dictionary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.data.repository.BodskadRepository
import com.example.bodskad.util.TtsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DictionaryUiState(
  val searchQuery: String = "",
  val selectedCategory: String = "All",
  val selectedDifficulty: String = "All",
  val expandedEntryId: Long? = null
)

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = BodskadRepository(application, viewModelScope)
  val ttsHelper = TtsHelper(application)

  private val _uiState = MutableStateFlow(DictionaryUiState())
  val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

  val entries: StateFlow<List<TibetanEntry>> = combine(
    repository.getAllEntries(),
    _uiState
  ) { allEntries, state ->
    allEntries.filter { entry ->
      val matchesQuery = state.searchQuery.isBlank() ||
        entry.tibetan.contains(state.searchQuery, ignoreCase = true) ||
        entry.wylie.contains(state.searchQuery, ignoreCase = true) ||
        entry.phonetic.contains(state.searchQuery, ignoreCase = true) ||
        entry.english.contains(state.searchQuery, ignoreCase = true)

      val matchesCategory = state.selectedCategory == "All" || entry.category == state.selectedCategory
      val matchesDifficulty = state.selectedDifficulty == "All" || entry.difficulty == state.selectedDifficulty

      matchesQuery && matchesCategory && matchesDifficulty
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun onSearchQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun onCategorySelected(category: String) {
    _uiState.value = _uiState.value.copy(selectedCategory = category)
  }

  fun onDifficultySelected(difficulty: String) {
    _uiState.value = _uiState.value.copy(selectedDifficulty = difficulty)
  }

  fun toggleEntryExpanded(id: Long) {
    _uiState.value = _uiState.value.copy(
      expandedEntryId = if (_uiState.value.expandedEntryId == id) null else id
    )
  }

  fun playAudio(entry: TibetanEntry) {
    ttsHelper.speak(entry.phonetic, isTibetan = true)
  }

  fun toggleFavorite(entry: TibetanEntry) {
    viewModelScope.launch {
      repository.toggleFavorite(entry)
    }
  }

  fun toggleFlashcard(entry: TibetanEntry) {
    viewModelScope.launch {
      repository.toggleFlashcard(entry)
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsHelper.shutdown()
  }
}
