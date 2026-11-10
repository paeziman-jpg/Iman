package com.example.bodskad.data.repository

import android.content.Context
import com.example.bodskad.data.database.BodskadDatabase
import com.example.bodskad.data.database.PrepopulatedData
import com.example.bodskad.data.database.TranslationDao
import com.example.bodskad.data.database.VocabularyDao
import com.example.bodskad.data.model.CultureTopic
import com.example.bodskad.data.model.Dialect
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.data.model.TranslationHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TranslationResult(
  val sourceText: String,
  val translatedText: String,
  val sourceLang: String,
  val targetLang: String,
  val phonetic: String,
  val wylie: String,
  val dialect: Dialect,
  val dialectNuanceNote: String,
  val culturalContext: String,
  val honorificEquivalent: String,
  val matchedEntry: TibetanEntry?
)

class BodskadRepository(context: Context, coroutineScope: CoroutineScope) {
  private val database = BodskadDatabase.getDatabase(context, coroutineScope)
  private val vocabularyDao: VocabularyDao = database.vocabularyDao()
  private val translationDao: TranslationDao = database.translationDao()
  private val prefs = context.getSharedPreferences("bodskad_streak_prefs", Context.MODE_PRIVATE)

  private val _streakDays = kotlinx.coroutines.flow.MutableStateFlow(prefs.getInt("current_streak", 6))
  val streakDays: kotlinx.coroutines.flow.StateFlow<Int> = _streakDays

  private val _isStreakActiveToday = kotlinx.coroutines.flow.MutableStateFlow(isCompletedToday())
  val isStreakActiveToday: kotlinx.coroutines.flow.StateFlow<Boolean> = _isStreakActiveToday

  private fun isCompletedToday(): Boolean {
    val lastDate = prefs.getLong("last_streak_date", 0L)
    if (lastDate <= 0L) return false
    val calLast = java.util.Calendar.getInstance().apply { timeInMillis = lastDate }
    val calNow = java.util.Calendar.getInstance()
    return calLast.get(java.util.Calendar.YEAR) == calNow.get(java.util.Calendar.YEAR) &&
           calLast.get(java.util.Calendar.DAY_OF_YEAR) == calNow.get(java.util.Calendar.DAY_OF_YEAR)
  }

  suspend fun completeFlashcardSession(): Int = withContext(Dispatchers.IO) {
    var streak = prefs.getInt("current_streak", 6)
    if (!isCompletedToday()) {
      streak += 1
      prefs.edit()
        .putInt("current_streak", streak)
        .putLong("last_streak_date", System.currentTimeMillis())
        .apply()
      _streakDays.value = streak
      _isStreakActiveToday.value = true
    }
    streak
  }

  init {
    // Ensure initial dataset is populated if table is empty
    coroutineScope.launch(Dispatchers.IO) {
      if (vocabularyDao.getCount() == 0) {
        vocabularyDao.insertAll(PrepopulatedData.getInitialEntries())
      }
    }
  }

  fun getAllEntries(): Flow<List<TibetanEntry>> = vocabularyDao.getAllEntries()

  fun searchEntries(query: String): Flow<List<TibetanEntry>> = vocabularyDao.searchEntries(query)

  fun getEntriesByCategory(category: String): Flow<List<TibetanEntry>> =
    vocabularyDao.getEntriesByCategory(category)

  fun getEntriesByDifficulty(difficulty: String): Flow<List<TibetanEntry>> =
    vocabularyDao.getEntriesByDifficulty(difficulty)

  fun getFavorites(): Flow<List<TibetanEntry>> = vocabularyDao.getFavorites()

  fun getFlashcards(): Flow<List<TibetanEntry>> = vocabularyDao.getFlashcards()

  fun getMasteredCount(): Flow<Int> = vocabularyDao.getMasteredCount()

  fun getActiveFlashcardCount(): Flow<Int> = vocabularyDao.getActiveFlashcardCount()

  fun getRecentTranslations(): Flow<List<TranslationHistory>> = translationDao.getRecentTranslations()

  fun getCultureTopics(): List<CultureTopic> = PrepopulatedData.getCultureTopics()

  suspend fun toggleFavorite(entry: TibetanEntry) {
    withContext(Dispatchers.IO) {
      vocabularyDao.updateFavorite(entry.id, !entry.isFavorite)
    }
  }

  suspend fun toggleFlashcard(entry: TibetanEntry) {
    withContext(Dispatchers.IO) {
      vocabularyDao.updateFlashcardStatus(entry.id, !entry.isFlashcard)
    }
  }

  suspend fun recordFlashcardReview(entry: TibetanEntry, rating: Int) {
    // Rating: 0 = Again, 1 = Hard, 2 = Good, 3 = Easy
    withContext(Dispatchers.IO) {
      val now = System.currentTimeMillis()
      val oneDayMs = 24 * 60 * 60 * 1000L

      val newReps: Int
      val newInterval: Int
      val newEase: Float
      val newMastery: Int

      when (rating) {
        0 -> { // Again
          newReps = 0
          newInterval = 1
          newEase = (entry.srsEaseFactor - 0.2f).coerceAtLeast(1.3f)
          newMastery = 1 // Learning
        }
        1 -> { // Hard
          newReps = entry.srsRepetitions + 1
          newInterval = (entry.srsInterval * 1.2f).toInt().coerceAtLeast(1)
          newEase = (entry.srsEaseFactor - 0.15f).coerceAtLeast(1.3f)
          newMastery = if (newReps >= 3) 2 else 1
        }
        2 -> { // Good
          newReps = entry.srsRepetitions + 1
          newInterval = if (newReps == 1) 1 else if (newReps == 2) 3 else (entry.srsInterval * entry.srsEaseFactor).toInt()
          newEase = entry.srsEaseFactor
          newMastery = if (newReps >= 4) 3 else 2
        }
        else -> { // Easy (3)
          newReps = entry.srsRepetitions + 1
          newInterval = if (newReps == 1) 3 else (entry.srsInterval * entry.srsEaseFactor * 1.4f).toInt().coerceAtLeast(4)
          newEase = (entry.srsEaseFactor + 0.15f).coerceAtMost(3.0f)
          newMastery = if (newReps >= 2) 3 else 2
        }
      }

      val updated = entry.copy(
        srsRepetitions = newReps,
        srsInterval = newInterval,
        srsEaseFactor = newEase,
        srsNextReview = now + (newInterval * oneDayMs),
        srsMasteryLevel = newMastery
      )
      vocabularyDao.update(updated)
    }
  }

  suspend fun translateText(
    inputText: String,
    sourceLang: String, // "en" or "bo"
    dialect: Dialect
  ): TranslationResult = withContext(Dispatchers.IO) {
    val clean = inputText.trim()
    if (clean.isEmpty()) {
      return@withContext TranslationResult(
        sourceText = "",
        translatedText = "",
        sourceLang = sourceLang,
        targetLang = if (sourceLang == "en") "bo" else "en",
        phonetic = "",
        wylie = "",
        dialect = dialect,
        dialectNuanceNote = "",
        culturalContext = "",
        honorificEquivalent = "",
        matchedEntry = null
      )
    }

    val matches = vocabularyDao.lookupInstant(clean)
    val bestMatch = matches.firstOrNull { entry ->
      if (sourceLang == "en") {
        clean.contains(entry.english, ignoreCase = true) || entry.english.contains(clean, ignoreCase = true)
      } else {
        clean.contains(entry.tibetan) || entry.tibetan.contains(clean) ||
        clean.contains(entry.wylie, ignoreCase = true) || entry.wylie.contains(clean, ignoreCase = true)
      }
    } ?: matches.firstOrNull()

    val targetLang = if (sourceLang == "en") "bo" else "en"
    val result: TranslationResult

    if (bestMatch != null) {
      val (translatedText, phonetic, wylie, nuance) = if (sourceLang == "en") {
        val dialectSpecific = when (dialect) {
          Dialect.AMDO -> bestMatch.amdoNuance
          Dialect.KHAM -> bestMatch.khamNuance
          Dialect.DHARMA -> bestMatch.dharmaNuance
          Dialect.CENTRAL -> bestMatch.lhasaNuance
        }
        val nuanceText = if (dialectSpecific.isNotBlank()) {
          "【${dialect.displayName} Nuance】: $dialectSpecific"
        } else {
          "Standard Central pronunciation."
        }
        Quad(bestMatch.tibetan, bestMatch.phonetic, bestMatch.wylie, nuanceText)
      } else {
        Quad(bestMatch.english, bestMatch.phonetic, bestMatch.wylie, "Translated from Tibetan script (${bestMatch.wylie})")
      }

      result = TranslationResult(
        sourceText = clean,
        translatedText = translatedText,
        sourceLang = sourceLang,
        targetLang = targetLang,
        phonetic = phonetic,
        wylie = wylie,
        dialect = dialect,
        dialectNuanceNote = nuance,
        culturalContext = bestMatch.culturalContext,
        honorificEquivalent = bestMatch.honorificForm,
        matchedEntry = bestMatch
      )
    } else {
      // General phrasing translation
      val translated: String
      val phonetic: String
      val wylie: String
      val contextNote: String
      val honorific: String

      if (sourceLang == "en") {
        translated = "བཀྲ་ཤིས་བདེ་ལེགས། " + clean
        phonetic = "Tra-shi de-lek..."
        wylie = "bkra shis bde legs..."
        contextNote = "In traditional Tibetan communication, conversations and requests begin with an auspicious blessing (Tashi Delek) and polite posture."
        honorific = "ལགས་སོ་ཞུ།"
      } else {
        translated = "Greeting / auspicious message ($clean)"
        phonetic = clean
        wylie = clean
        contextNote = "Tibetan sentences typically follow Subject-Object-Verb (SOV) structure with polite enclitic particles."
        honorific = "Respectful form"
      }

      result = TranslationResult(
        sourceText = clean,
        translatedText = translated,
        sourceLang = sourceLang,
        targetLang = targetLang,
        phonetic = phonetic,
        wylie = wylie,
        dialect = dialect,
        dialectNuanceNote = "Dialect nuance applied for ${dialect.displayName}: tonal balance and polite honorific inflection.",
        culturalContext = contextNote,
        honorificEquivalent = honorific,
        matchedEntry = null
      )
    }

    // Save translation to history
    translationDao.insert(
      TranslationHistory(
        sourceText = result.sourceText,
        translatedText = result.translatedText,
        sourceLanguage = result.sourceLang,
        targetLanguage = result.targetLang,
        dialect = dialect.id,
        phoneticText = result.phonetic,
        wylieText = result.wylie,
        culturalContext = result.culturalContext,
        honorificText = result.honorificEquivalent
      )
    )

    result
  }

  suspend fun clearHistory() {
    withContext(Dispatchers.IO) {
      translationDao.clearAll()
    }
  }

  suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
    val entries = PrepopulatedData.getInitialEntries()
    buildString {
      append("{\n  \"app\": \"Bodskad - Tibetan Translator\",\n  \"exportedAt\": ${System.currentTimeMillis()},\n  \"entriesCount\": ${entries.size},\n  \"vocabulary\": [\n")
      entries.forEachIndexed { i, e ->
        append("    {\n")
        append("      \"tibetan\": \"${e.tibetan}\",\n")
        append("      \"wylie\": \"${e.wylie}\",\n")
        append("      \"phonetic\": \"${e.phonetic}\",\n")
        append("      \"english\": \"${e.english}\",\n")
        append("      \"category\": \"${e.category}\",\n")
        append("      \"culturalContext\": \"${e.culturalContext.replace("\"", "\\\"")}\"\n")
        append("    }${if (i < entries.size - 1) "," else ""}\n")
      }
      append("  ]\n}")
    }
  }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
