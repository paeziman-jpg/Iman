package com.example.bodskad.ui.screens.flashcards

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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FlashcardsUiState(
  val currentCardIndex: Int = 0,
  val isCardFlipped: Boolean = false,
  val isTibetanFront: Boolean = true,
  val cardsReviewedThisSession: Int = 0,
  val selectedCategory: String = "All",
  val showStreakCelebration: Boolean = false,
  val celebrationStreakCount: Int = 0
)

class FlashcardsViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = BodskadRepository(application, viewModelScope)
  val ttsHelper = TtsHelper(application)

  private val _uiState = MutableStateFlow(FlashcardsUiState())
  val uiState: StateFlow<FlashcardsUiState> = _uiState.asStateFlow()

  val flashcards: StateFlow<List<TibetanEntry>> = repository
    .getFlashcards()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val streakDays: StateFlow<Int> = repository.streakDays
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

  val isStreakActiveToday: StateFlow<Boolean> = repository.isStreakActiveToday
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  fun flipCard() {
    _uiState.value = _uiState.value.copy(isCardFlipped = !_uiState.value.isCardFlipped)
  }

  fun toggleFrontLanguage() {
    _uiState.value = _uiState.value.copy(
      isTibetanFront = !_uiState.value.isTibetanFront,
      isCardFlipped = false
    )
  }

  fun onCategorySelected(category: String) {
    _uiState.value = _uiState.value.copy(
      selectedCategory = category,
      currentCardIndex = 0,
      isCardFlipped = false
    )
  }

  fun playAudio(entry: TibetanEntry) {
    ttsHelper.speak(entry.phonetic, isTibetan = true)
  }

  fun answerCard(entry: TibetanEntry, rating: Int) {
    viewModelScope.launch {
      repository.recordFlashcardReview(entry, rating)
      val currentCards = getFilteredCards()
      val nextIndex = if (currentCards.isNotEmpty()) {
        (_uiState.value.currentCardIndex + 1) % currentCards.size
      } else 0

      val newReviewedCount = _uiState.value.cardsReviewedThisSession + 1

      // If user reaches 3 cards in this session and streak was not yet active today, complete streak session!
      if (newReviewedCount >= 3 && !isStreakActiveToday.value) {
        val newStreak = repository.completeFlashcardSession()
        _uiState.value = _uiState.value.copy(
          currentCardIndex = nextIndex,
          isCardFlipped = false,
          cardsReviewedThisSession = newReviewedCount,
          showStreakCelebration = true,
          celebrationStreakCount = newStreak
        )
      } else {
        _uiState.value = _uiState.value.copy(
          currentCardIndex = nextIndex,
          isCardFlipped = false,
          cardsReviewedThisSession = newReviewedCount
        )
      }
    }
  }

  fun completeSessionManually() {
    viewModelScope.launch {
      val newStreak = repository.completeFlashcardSession()
      _uiState.value = _uiState.value.copy(
        showStreakCelebration = true,
        celebrationStreakCount = newStreak
      )
    }
  }

  fun dismissStreakCelebration() {
    _uiState.value = _uiState.value.copy(showStreakCelebration = false)
  }

  fun getFilteredCards(): List<TibetanEntry> {
    val allCards = flashcards.value
    val cat = _uiState.value.selectedCategory
    return if (cat == "All") allCards else allCards.filter { it.category == cat }
  }

  fun resetSession() {
    _uiState.value = _uiState.value.copy(
      currentCardIndex = 0,
      isCardFlipped = false,
      cardsReviewedThisSession = 0
    )
  }

  override fun onCleared() {
    super.onCleared()
    ttsHelper.shutdown()
  }
}
