package com.example.bodskad.ui.screens.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeeklyActivityBarChart(
  weeklyData: List<DayActivity>,
  dailyGoal: Int,
  modifier: Modifier = Modifier
) {
  var selectedDay by remember { mutableStateOf<DayActivity?>(null) }
  val primaryColor = MaterialTheme.colorScheme.primary
  val secondaryColor = MaterialTheme.colorScheme.secondary
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
  val maxWords = (weeklyData.maxOfOrNull { it.wordsLearned } ?: 15).coerceAtLeast(dailyGoal + 4)

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("weekly_activity_chart"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Weekly Activity & Words Learned",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Daily volume vs ${dailyGoal} words/day target",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        selectedDay?.let { day ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = "${day.dayLabel}: ${day.wordsLearned} words • ${day.accuracyPercent}%",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Custom Canvas Bar Chart
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      ) {
        Canvas(modifier = Modifier.matchParentSize()) {
          val canvasWidth = size.width
          val canvasHeight = size.height - 24.dp.toPx()
          val barWidth = 22.dp.toPx()
          val slotWidth = canvasWidth / weeklyData.size

          // Target line
          val targetY = canvasHeight * (1f - (dailyGoal.toFloat() / maxWords))
          drawLine(
            color = secondaryColor.copy(alpha = 0.4f),
            start = Offset(0f, targetY),
            end = Offset(canvasWidth, targetY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
          )

          weeklyData.forEachIndexed { index, day ->
            val centerX = slotWidth * index + (slotWidth / 2)
            val barHeight = (day.wordsLearned.toFloat() / maxWords) * canvasHeight
            val barTop = canvasHeight - barHeight

            // Background slot bar
            drawRoundRect(
              color = surfaceVariant.copy(alpha = 0.5f),
              topLeft = Offset(centerX - (barWidth / 2), 0f),
              size = Size(barWidth, canvasHeight),
              cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Value bar
            val barColor = if (day.isToday) {
              primaryColor
            } else if (day.wordsLearned >= dailyGoal) {
              secondaryColor
            } else {
              primaryColor.copy(alpha = 0.75f)
            }

            drawRoundRect(
              color = barColor,
              topLeft = Offset(centerX - (barWidth / 2), barTop),
              size = Size(barWidth, barHeight),
              cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
          }
        }

        // Tap targets & Day labels row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          weeklyData.forEach { day ->
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { selectedDay = day }
                .padding(vertical = 2.dp)
            ) {
              Text(
                text = day.dayLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isToday) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Chart Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        LegendItem(color = primaryColor, label = "Today's Study")
        LegendItem(color = secondaryColor, label = "Goal Met")
        LegendItem(color = secondaryColor.copy(alpha = 0.4f), label = "Daily Goal Line")
      }
    }
  }
}

@Composable
fun AccuracyGaugeChart(
  accuracyPercent: Int,
  totalAttempts: Int,
  correctAttempts: Int,
  modifier: Modifier = Modifier
) {
  val animatedAccuracy by animateFloatAsState(
    targetValue = accuracyPercent.toFloat() / 100f,
    animationSpec = tween(durationMillis = 800),
    label = "accuracy_anim"
  )
  val primaryColor = MaterialTheme.colorScheme.primary
  val secondaryColor = MaterialTheme.colorScheme.secondary
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("accuracy_gauge_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "Translation & Quiz Accuracy",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Based on $totalAttempts translation exercises and nuance checks",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Radial Meter Box
        Box(
          modifier = Modifier.size(110.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(96.dp)) {
            val strokeWidth = 10.dp.toPx()
            // Track arc
            drawArc(
              color = surfaceVariant,
              startAngle = 140f,
              sweepAngle = 260f,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
              brush = Brush.sweepGradient(
                listOf(secondaryColor, primaryColor, secondaryColor)
              ),
              startAngle = 140f,
              sweepAngle = 260f * animatedAccuracy,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$accuracyPercent%",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = primaryColor
            )
            Text(
              text = "Accuracy",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.width(18.dp))

        // Breakdown Pillars
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AccuracyPillar(label = "Greetings & Etiquette", percent = 96, color = secondaryColor)
          AccuracyPillar(label = "Butter Tea & Hospitality", percent = 92, color = primaryColor)
          AccuracyPillar(label = "Zhe-sa (ཞེ་ས) Honorifics", percent = 88, color = MaterialTheme.colorScheme.tertiary)
          AccuracyPillar(label = "Dharma Philosophy", percent = 90, color = secondaryColor)
        }
      }
    }
  }
}

@Composable
fun MasteryDistributionBar(
  masteredCount: Int,
  activeCount: Int,
  totalVocabulary: Int = 27,
  modifier: Modifier = Modifier
) {
  val reviewCount = (activeCount - masteredCount).coerceAtLeast(0)
  val learningCount = (totalVocabulary - activeCount).coerceAtLeast(0)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Vocabulary Mastery Pipeline",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "$totalVocabulary Total Words",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Multi-segment horizontal bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
      ) {
        if (masteredCount > 0) {
          Box(
            modifier = Modifier
              .weight(masteredCount.toFloat())
              .height(12.dp)
              .background(MaterialTheme.colorScheme.secondary)
          )
        }
        if (reviewCount > 0) {
          Box(
            modifier = Modifier
              .weight(reviewCount.toFloat())
              .height(12.dp)
              .background(MaterialTheme.colorScheme.tertiary)
          )
        }
        if (learningCount > 0) {
          Box(
            modifier = Modifier
              .weight(learningCount.toFloat())
              .height(12.dp)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        LegendItem(color = MaterialTheme.colorScheme.secondary, label = "Mastered ($masteredCount)")
        LegendItem(color = MaterialTheme.colorScheme.tertiary, label = "Review ($reviewCount)")
        LegendItem(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), label = "Learning ($learningCount)")
      }
    }
  }
}

@Composable
private fun AccuracyPillar(label: String, percent: Int, color: Color) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
      Text(text = "$percent%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(2.dp))
    LinearProgressIndicator(
      progress = { percent.toFloat() / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = color,
      trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
  }
}

@Composable
private fun LegendItem(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
