package com.example.bodskad.ui.screens.translate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodskad.data.model.Dialect
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.data.model.TranslationHistory
import com.example.bodskad.data.repository.BodskadRepository
import com.example.bodskad.data.repository.TranslationResult
import com.example.bodskad.util.TtsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TranslateUiState(
  val sourceText: String = "Hello blessings",
  val sourceLang: String = "en", // "en" or "bo"
  val selectedDialect: Dialect = Dialect.CENTRAL,
  val translationResult: TranslationResult? = null,
  val isLoading: Boolean = false,
  val isAudioPlaying: Boolean = false,
  val isBeginnerMode: Boolean = true
)

class TranslateViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = BodskadRepository(application, viewModelScope)
  val ttsHelper = TtsHelper(application)

  private val _uiState = MutableStateFlow(TranslateUiState())
  val uiState: StateFlow<TranslateUiState> = _uiState.asStateFlow()

  val recentTranslations: StateFlow<List<TranslationHistory>> = repository
    .getRecentTranslations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val streakDays: StateFlow<Int> = repository.streakDays
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

  val isStreakActiveToday: StateFlow<Boolean> = repository.isStreakActiveToday
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  init {
    performTranslation()
  }

  fun onSourceTextChanged(text: String) {
    _uiState.value = _uiState.value.copy(sourceText = text)
    performTranslation()
  }

  fun onDialectSelected(dialect: Dialect) {
    _uiState.value = _uiState.value.copy(selectedDialect = dialect)
    performTranslation()
  }

  fun swapLanguages() {
    val current = _uiState.value
    val newSourceLang = if (current.sourceLang == "en") "bo" else "en"
    val newSourceText = current.translationResult?.translatedText ?: ""
    _uiState.value = current.copy(
      sourceLang = newSourceLang,
      sourceText = newSourceText
    )
    performTranslation()
  }

  fun toggleBeginnerMode() {
    _uiState.value = _uiState.value.copy(isBeginnerMode = !_uiState.value.isBeginnerMode)
  }

  fun performTranslation() {
    val text = _uiState.value.sourceText
    if (text.isBlank()) {
      _uiState.value = _uiState.value.copy(translationResult = null)
      return
    }

    viewModelScope.launch {
      val result = repository.translateText(
        inputText = text,
        sourceLang = _uiState.value.sourceLang,
        dialect = _uiState.value.selectedDialect
      )
      _uiState.value = _uiState.value.copy(translationResult = result)
    }
  }

  fun playAudio() {
    val res = _uiState.value.translationResult ?: return
    if (_uiState.value.sourceLang == "en") {
      // Speaking Tibetan phonetics clearly
      val textToSpeak = if (res.phonetic.isNotBlank()) res.phonetic else res.translatedText
      ttsHelper.speak(textToSpeak, isTibetan = true)
    } else {
      ttsHelper.speak(res.translatedText, isTibetan = false)
    }
  }

  fun toggleFavorite(entry: TibetanEntry) {
    viewModelScope.launch {
      repository.toggleFavorite(entry)
      performTranslation()
    }
  }

  fun toggleFlashcard(entry: TibetanEntry) {
    viewModelScope.launch {
      repository.toggleFlashcard(entry)
      performTranslation()
    }
  }

  fun clearHistory() {
    viewModelScope.launch {
      repository.clearHistory()
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsHelper.shutdown()
  }
}
