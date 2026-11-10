package com.example.bodskad.ui.screens.cultural

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodskad.data.database.PrepopulatedData

@Composable
fun CulturalGuideScreen(
  modifier: Modifier = Modifier,
  onPhraseClick: (String) -> Unit = {}
) {
  val topics = PrepopulatedData.getCultureTopics()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Tibetan Etiquette & Sacred Culture",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Master the social etiquette, bodily gestures, and cultural context of the Tibetan plateau.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(topics, key = { it.id }) { topic ->
      ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Header with Emoji & Titles
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = topic.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = topic.titleTibetan,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = topic.titleEnglish,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Text(
                  text = topic.category,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Summary
          Text(
            text = topic.summary,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(10.dp))

          // Key Etiquette Rules
          Text(
            text = "Essential Etiquette & Situational Rules:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            topic.etiquetteRules.forEach { rule ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier
                    .size(16.dp)
                    .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = rule,
                  style = MaterialTheme.typography.bodySmall,
                  lineHeight = 18.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          // Related Phrases
          if (topic.relatedPhrases.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Key Phrases:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              topic.relatedPhrases.forEach { phrase ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                  Text(
                    text = phrase,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
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
