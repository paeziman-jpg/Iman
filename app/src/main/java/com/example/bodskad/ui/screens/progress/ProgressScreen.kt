package com.example.bodskad.ui.screens.progress

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressScreen(
  viewModel: ProgressViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val masteredCount by viewModel.masteredCount.collectAsState()
  val activeCards by viewModel.activeCardCount.collectAsState()
  val lessons by viewModel.lessons.collectAsState()
  val streakDays by viewModel.streakDays.collectAsState()
  val isStreakActiveToday by viewModel.isStreakActiveToday.collectAsState()
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Row with XP & Level
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Progress & Activity Tracking",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = uiState.userLevelTitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${uiState.totalXp} XP",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }
    }

    // Top Overview Card: Streak & Key Stats
    item {
      ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Streak Banner
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Whatshot,
                  contentDescription = "Streak",
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(28.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "$streakDays-Day Stupa Streak",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (isStreakActiveToday) "Active Today • Session completed" else "Needs study • Review flashcards to extend",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isStreakActiveToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = "${uiState.overallAccuracyPercent}% Accuracy",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 4 Metric Stat Pillars
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            StatPillar(label = "Lessons Done", value = "${uiState.lessonsCompletedCount}", icon = "📚")
            StatPillar(label = "Words Mastered", value = "$masteredCount", icon = "🏆")
            StatPillar(label = "Translations", value = "${uiState.totalTranslationsAttempted}", icon = "✍️")
            StatPillar(label = "Study Time", value = "${uiState.totalStudyMinutes}m", icon = "⏳")
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Daily Goal Progress
          Text(
            text = "Today's Target: ${uiState.studiedTodayWords} / ${uiState.dailyGoalWords} words",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { (uiState.studiedTodayWords.toFloat() / uiState.dailyGoalWords).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Goal Selector Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Goal:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            listOf(5, 10, 15, 20).forEach { goal ->
              FilterChip(
                selected = uiState.dailyGoalWords == goal,
                onClick = { viewModel.setDailyGoal(goal) },
                label = { Text("$goal words", style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(8.dp)
              )
            }
          }
        }
      }
    }

    // Interactive Practice Quiz Action Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Test Translation Accuracy",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "Practice dialect nuances & etiquette rules to boost accuracy.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = { viewModel.startQuiz() },
            modifier = Modifier.testTag("start_quiz_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Quiz")
          }
        }
      }
    }

    // Chart 1: Weekly Activity Bar Chart
    item {
      WeeklyActivityBarChart(
        weeklyData = uiState.weeklyActivity,
        dailyGoal = uiState.dailyGoalWords
      )
    }

    // Chart 2: Translation & Quiz Accuracy Radial Gauge
    item {
      AccuracyGaugeChart(
        accuracyPercent = uiState.overallAccuracyPercent,
        totalAttempts = uiState.totalTranslationsAttempted,
        correctAttempts = uiState.correctTranslationsCount
      )
    }

    // Chart 3: Vocabulary Mastery Distribution Pipeline
    item {
      MasteryDistributionBar(
        masteredCount = masteredCount,
        activeCount = activeCards
      )
    }

    // Section: Lessons Completed
    item {
      Text(
        text = "Curriculum Lessons & Mastery",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(lessons, key = { it.id }) { lesson ->
      LessonRowCard(lesson = lesson)
    }

    // Milestones & Achievements
    item {
      Text(
        text = "Milestones & Achievements",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(viewModel.milestones) { milestone ->
      MilestoneCard(milestone = milestone)
    }

    // Daily Push Notification Settings Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Daily Goal Reminder",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Notification at ${uiState.reminderTime}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = uiState.isNotificationReminderEnabled,
              onCheckedChange = { viewModel.toggleNotificationReminder(it) },
              modifier = Modifier.testTag("notification_reminder_switch")
            )
          }

          if (uiState.isNotificationReminderEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = {
                viewModel.sendTestNotification(context)
                Toast.makeText(context, "Daily goal notification sent!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("send_test_notification_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Send Test Reminder Notification")
            }
          }
        }
      }
    }

    // Export Data Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FileDownload,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Data Portability & Backup",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "Export study analytics, translation records, and flashcards as JSON.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 6.dp)
          )

          Button(
            onClick = {
              viewModel.exportData { json ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Bodskad Backup JSON", json)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Analytics & Vocabulary JSON copied!", Toast.LENGTH_LONG).show()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("export_data_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Data (JSON)")
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Interactive Quiz Dialog
  ProgressQuizDialog(viewModel = viewModel, uiState = uiState)
}

@Composable
private fun StatPillar(label: String, value: String, icon: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = icon, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 10.sp
    )
  }
}

@Composable
private fun LessonRowCard(lesson: LessonItem) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (lesson.isCompleted) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      }
    ),
    border = if (lesson.isCompleted) CardDefaults.outlinedCardBorder() else null,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              if (lesson.isCompleted) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
              else MaterialTheme.colorScheme.surfaceVariant
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.School,
            contentDescription = null,
            tint = if (lesson.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = lesson.titleTibetan,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "• ${lesson.titleEnglish}",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium
            )
          }

          Text(
            text = "${lesson.category} • ${lesson.accuracyPercent}% accuracy • +${lesson.xpReward} XP",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (lesson.isCompleted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant
      ) {
        Text(
          text = if (lesson.isCompleted) "Completed" else "${lesson.completedQuestions}/${lesson.totalQuestions}",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = if (lesson.isCompleted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }
  }
}

@Composable
private fun MilestoneCard(milestone: Milestone) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (milestone.isUnlocked) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      }
    ),
    border = if (milestone.isUnlocked) CardDefaults.outlinedCardBorder() else null,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(
            if (milestone.isUnlocked) {
              MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(text = milestone.iconEmoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = milestone.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = milestone.tibetanTitle,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          text = "${milestone.description} (+${milestone.xpValue} XP)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(vertical = 2.dp)
        )

        LinearProgressIndicator(
          progress = { milestone.progressPercent },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = if (milestone.isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Icon(
        imageVector = if (milestone.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
        contentDescription = if (milestone.isUnlocked) "Unlocked" else "Locked",
        tint = if (milestone.isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}
