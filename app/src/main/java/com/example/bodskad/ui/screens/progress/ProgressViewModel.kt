package com.example.bodskad.ui.screens.progress

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.data.repository.BodskadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayActivity(
  val dayLabel: String,       // e.g. "Mon", "Tue"
  val wordsLearned: Int,
  val lessonsCompleted: Int,
  val accuracyPercent: Int,
  val isToday: Boolean = false
)

data class LessonItem(
  val id: String,
  val titleTibetan: String,
  val titleEnglish: String,
  val category: String,
  val totalQuestions: Int,
  val completedQuestions: Int,
  val accuracyPercent: Int,
  val isCompleted: Boolean,
  val xpReward: Int
)

data class QuizQuestion(
  val questionText: String,
  val phoneticOrWylie: String,
  val culturalHint: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val category: String
)

data class Milestone(
  val id: String,
  val title: String,
  val tibetanTitle: String,
  val description: String,
  val iconEmoji: String,
  val isUnlocked: Boolean,
  val progressPercent: Float,
  val xpValue: Int,
  val isClaimed: Boolean = false
)

data class ProgressUiState(
  val currentStreakDays: Int = 6,
  val totalXp: Int = 840,
  val userLevel: Int = 3,
  val userLevelTitle: String = "Monastery Scholar (Level 3)",
  val dailyGoalWords: Int = 10,
  val studiedTodayWords: Int = 8,
  val lessonsCompletedCount: Int = 14,
  val totalTranslationsAttempted: Int = 68,
  val correctTranslationsCount: Int = 62,
  val overallAccuracyPercent: Int = 91,
  val totalStudyMinutes: Int = 74,
  val isNotificationReminderEnabled: Boolean = true,
  val reminderTime: String = "08:30 AM",
  val weeklyActivity: List<DayActivity> = listOf(
    DayActivity("Mon", wordsLearned = 12, lessonsCompleted = 2, accuracyPercent = 95),
    DayActivity("Tue", wordsLearned = 8, lessonsCompleted = 1, accuracyPercent = 88),
    DayActivity("Wed", wordsLearned = 15, lessonsCompleted = 3, accuracyPercent = 92),
    DayActivity("Thu", wordsLearned = 6, lessonsCompleted = 1, accuracyPercent = 85),
    DayActivity("Fri", wordsLearned = 14, lessonsCompleted = 2, accuracyPercent = 94),
    DayActivity("Sat", wordsLearned = 10, lessonsCompleted = 2, accuracyPercent = 90),
    DayActivity("Sun", wordsLearned = 8, lessonsCompleted = 3, accuracyPercent = 93, isToday = true)
  ),
  val isQuizActive: Boolean = false,
  val currentQuizQuestionIndex: Int = 0,
  val quizScore: Int = 0,
  val quizFinished: Boolean = false,
  val selectedQuizAnswerIndex: Int? = null,
  val isAnswerSubmitted: Boolean = false,
  val exportedJson: String = ""
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = BodskadRepository(application, viewModelScope)

  private val _uiState = MutableStateFlow(ProgressUiState())
  val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

  val masteredCount: StateFlow<Int> = repository
    .getMasteredCount()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7)

  val activeCardCount: StateFlow<Int> = repository
    .getActiveFlashcardCount()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 24)

  val streakDays: StateFlow<Int> = repository.streakDays
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

  val isStreakActiveToday: StateFlow<Boolean> = repository.isStreakActiveToday
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  private val _lessons = MutableStateFlow(
    listOf(
      LessonItem(
        id = "l1",
        titleTibetan = "བཀྲ་ཤིས་བདེ་ལེགས།",
        titleEnglish = "Auspicious Greetings & Bows",
        category = "Greetings & Etiquette",
        totalQuestions = 10,
        completedQuestions = 10,
        accuracyPercent = 96,
        isCompleted = true,
        xpReward = 120
      ),
      LessonItem(
        id = "l2",
        titleTibetan = "བོད་ཇའི་གུས་ཞབས།",
        titleEnglish = "Butter Tea Etiquette & Host Hospitality",
        category = "Hospitality & Food",
        totalQuestions = 8,
        completedQuestions = 8,
        accuracyPercent = 92,
        isCompleted = true,
        xpReward = 100
      ),
      LessonItem(
        id = "l3",
        titleTibetan = "ཞེ་སའི་བྱ་ཚིག",
        titleEnglish = "Zhe-sa Honorific Verbs (Pheb, Chö, Zim)",
        category = "Family & Honorifics",
        totalQuestions = 12,
        completedQuestions = 10,
        accuracyPercent = 88,
        isCompleted = false,
        xpReward = 150
      ),
      LessonItem(
        id = "l4",
        titleTibetan = "ཆོས་སྐད་དང་སྙིང་རྗེ།",
        titleEnglish = "Dharma Terms & Mind of Awakening",
        category = "Dharma & Philosophy",
        totalQuestions = 10,
        completedQuestions = 6,
        accuracyPercent = 90,
        isCompleted = false,
        xpReward = 140
      ),
      LessonItem(
        id = "l5",
        titleTibetan = "བར་སྐོར་ཚོང་ཁྲོམ།",
        titleEnglish = "Barkhor Market Bargaining & Numbers",
        category = "Daily Life & Travel",
        totalQuestions = 10,
        completedQuestions = 3,
        accuracyPercent = 80,
        isCompleted = false,
        xpReward = 120
      )
    )
  )
  val lessons: StateFlow<List<LessonItem>> = _lessons.asStateFlow()

  val quizQuestions = listOf(
    QuizQuestion(
      questionText = "What is the polite Tibetan greeting when offering blessings and good fortune?",
      phoneticOrWylie = "Pronounced: Tra-shi De-lek",
      culturalHint = "Often accompanied by an open-palmed nod and respectful head tilt.",
      options = listOf("བཀྲ་ཤིས་བདེ་ལེགས།", "ག་ལེར་ཕེབས།", "ཐུགས་རྗེ་ཆེ།", "དགོངས་དག"),
      correctOptionIndex = 0,
      category = "Greetings & Etiquette"
    ),
    QuizQuestion(
      questionText = "When a host bids farewell to a departing traveler, which phrase must the host say?",
      phoneticOrWylie = "Literally 'Go gently / slowly'",
      culturalHint = "Reversing this with the guest's reply 'Ga-le Zhuk' is considered an etiquette faux pas.",
      options = listOf("ག་ལེར་བཞུགས།", "ག་ལེར་ཕེབས།", "སྐུ་མཁྱེན།", "ལགས་སོ།"),
      correctOptionIndex = 1,
      category = "Greetings & Etiquette"
    ),
    QuizQuestion(
      questionText = "Which term represents the Zhe-sa (ཞེ་ས) honorific verb for 'To Eat / Partake'?",
      phoneticOrWylie = "Replacing the common verb ཟ་ (Za)",
      culturalHint = "Never use this verb for yourself, only when serving or inviting others.",
      options = listOf("འགྲོ (Dro)", "ཉལ (Nyal)", "མཆོད (Chö)", "ལབ (Lab)"),
      correctOptionIndex = 2,
      category = "Family & Honorifics"
    ),
    QuizQuestion(
      questionText = "What does the Tibetan word 'Po Cha' (བོད་ཇ) refer to?",
      phoneticOrWylie = "A traditional staple across the Tibetan plateau",
      culturalHint = "Served hot in wooden bowls, churned with yak butter and Himalayan salt.",
      options = listOf("Tsampa barley", "Savory Tibetan butter tea", "Steamed momo", "Sweet yogurt"),
      correctOptionIndex = 1,
      category = "Hospitality & Food"
    ),
    QuizQuestion(
      questionText = "What is the philosophical meaning of Bodhicitta (བྱང་ཆུབ་ཀྱི་སེམས)?",
      phoneticOrWylie = "Sanskrit: Bodhicitta",
      culturalHint = "Regarded as the crown jewel of Tibetan Mahayana Buddhist ethics.",
      options = listOf("Mind of Awakening for all beings", "Calm solitary hermitage", "Circumambulation circuit", "Ritual silk scarf"),
      correctOptionIndex = 0,
      category = "Dharma & Philosophy"
    )
  )

  val milestones: List<Milestone> = listOf(
    Milestone(
      id = "m1",
      title = "Himalayan Seeker",
      tibetanTitle = "གངས་ཅན་འགྲུལ་པ།",
      description = "Completed your first Tibetan translation and audio pronunciation.",
      iconEmoji = "🏔️",
      isUnlocked = true,
      progressPercent = 1.0f,
      xpValue = 50,
      isClaimed = true
    ),
    Milestone(
      id = "m2",
      title = "Po Cha Master",
      tibetanTitle = "བོད་ཇའི་མཁས་པ།",
      description = "Mastered Tibetan butter tea and dining hospitality etiquette.",
      iconEmoji = "☕",
      isUnlocked = true,
      progressPercent = 1.0f,
      xpValue = 100,
      isClaimed = true
    ),
    Milestone(
      id = "m3",
      title = "Zhe-sa Etiquette Master",
      tibetanTitle = "ཞེ་སའི་གུས་ཞབས།",
      description = "Learned 5 honorific verbal pairs and polite social gestures.",
      iconEmoji = "🙏",
      isUnlocked = true,
      progressPercent = 1.0f,
      xpValue = 150,
      isClaimed = false
    ),
    Milestone(
      id = "m4",
      title = "Three Provinces Polyglot",
      tibetanTitle = "ཆོལ་ཁ་གསུམ་གྱི་སྐད།",
      description = "Practiced dialect nuances across Central, Amdo, and Kham.",
      iconEmoji = "🗣️",
      isUnlocked = true,
      progressPercent = 0.85f,
      xpValue = 200,
      isClaimed = false
    ),
    Milestone(
      id = "m5",
      title = "Dharma Scholar",
      tibetanTitle = "ཆོས་ཀྱི་མཁས་དབང་།",
      description = "Achieve 90%+ accuracy on classical Buddhist philosophical terms.",
      iconEmoji = "☸️",
      isUnlocked = true,
      progressPercent = 0.92f,
      xpValue = 250,
      isClaimed = false
    ),
    Milestone(
      id = "m6",
      title = "7-Day Stupa Streak",
      tibetanTitle = "མཆོད་རྟེན་རྒྱུན་མཐུད།",
      description = "Study consistently for 7 days in a row.",
      iconEmoji = "🔥",
      isUnlocked = false,
      progressPercent = 0.86f,
      xpValue = 300,
      isClaimed = false
    )
  )

  fun startQuiz() {
    _uiState.value = _uiState.value.copy(
      isQuizActive = true,
      currentQuizQuestionIndex = 0,
      quizScore = 0,
      quizFinished = false,
      selectedQuizAnswerIndex = null,
      isAnswerSubmitted = false
    )
  }

  fun selectQuizAnswer(index: Int) {
    if (_uiState.value.isAnswerSubmitted) return
    _uiState.value = _uiState.value.copy(selectedQuizAnswerIndex = index)
  }

  fun submitQuizAnswer() {
    val currentIdx = _uiState.value.currentQuizQuestionIndex
    val selected = _uiState.value.selectedQuizAnswerIndex ?: return
    val question = quizQuestions[currentIdx]
    val isCorrect = selected == question.correctOptionIndex

    val newScore = if (isCorrect) _uiState.value.quizScore + 1 else _uiState.value.quizScore
    val newTotalAttempts = _uiState.value.totalTranslationsAttempted + 1
    val newCorrectAttempts = if (isCorrect) _uiState.value.correctTranslationsCount + 1 else _uiState.value.correctTranslationsCount
    val newAccuracy = ((newCorrectAttempts.toFloat() / newTotalAttempts) * 100).toInt()

    _uiState.value = _uiState.value.copy(
      quizScore = newScore,
      isAnswerSubmitted = true,
      totalTranslationsAttempted = newTotalAttempts,
      correctTranslationsCount = newCorrectAttempts,
      overallAccuracyPercent = newAccuracy
    )
  }

  fun nextQuizQuestion() {
    val nextIdx = _uiState.value.currentQuizQuestionIndex + 1
    if (nextIdx < quizQuestions.size) {
      _uiState.value = _uiState.value.copy(
        currentQuizQuestionIndex = nextIdx,
        selectedQuizAnswerIndex = null,
        isAnswerSubmitted = false
      )
    } else {
      // Finished quiz! Update lessons and XP
      val earnedXp = _uiState.value.quizScore * 30
      val updatedXp = _uiState.value.totalXp + earnedXp
      val newLessonsCount = _uiState.value.lessonsCompletedCount + 1

      // Update today's activity
      val updatedWeekly = _uiState.value.weeklyActivity.map { day ->
        if (day.isToday) {
          day.copy(
            wordsLearned = day.wordsLearned + _uiState.value.quizScore,
            lessonsCompleted = day.lessonsCompleted + 1,
            accuracyPercent = _uiState.value.overallAccuracyPercent
          )
        } else day
      }

      _uiState.value = _uiState.value.copy(
        quizFinished = true,
        totalXp = updatedXp,
        lessonsCompletedCount = newLessonsCount,
        studiedTodayWords = _uiState.value.studiedTodayWords + _uiState.value.quizScore,
        weeklyActivity = updatedWeekly
      )
    }
  }

  fun closeQuiz() {
    _uiState.value = _uiState.value.copy(isQuizActive = false)
  }

  fun setDailyGoal(words: Int) {
    _uiState.value = _uiState.value.copy(dailyGoalWords = words)
  }

  fun toggleNotificationReminder(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(isNotificationReminderEnabled = enabled)
  }

  fun sendTestNotification(context: Context) {
    val channelId = "bodskad_daily_study"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId,
        "Daily Study Reminders",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Reminds you to practice your daily Tibetan vocabulary and mantras."
      }
      manager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("བཀྲ་ཤིས་བདེ་ལེགས། Daily Bodskad Goal")
      .setContentText("You are at ${uiState.value.overallAccuracyPercent}% accuracy! Complete today's lesson to extend your 6-day streak.")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()

    try {
      manager.notify(1001, notification)
    } catch (e: Exception) {
      // Notification catch
    }
  }

  fun exportData(onReady: (String) -> Unit) {
    viewModelScope.launch {
      val json = repository.exportDataJson()
      _uiState.value = _uiState.value.copy(exportedJson = json)
      onReady(json)
    }
  }
}
