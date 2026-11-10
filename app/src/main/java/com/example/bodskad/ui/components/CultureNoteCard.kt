package com.example.bodskad.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CultureNoteCard(
  culturalNote: String,
  modifier: Modifier = Modifier,
  honorificText: String = "",
  initiallyExpanded: Boolean = true
) {
  if (culturalNote.isBlank() && honorificText.isBlank()) return

  var isExpanded by remember { mutableStateOf(initiallyExpanded) }
  val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow")

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("culture_note_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = "Cultural Context",
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Cultural Context & Etiquette",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
              text = "Situational usage & Tibetan norms",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Icon(
          imageVector = Icons.Default.KeyboardArrowDown,
          contentDescription = if (isExpanded) "Collapse" else "Expand",
          modifier = Modifier.rotate(arrowRotation),
          tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          if (honorificText.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ཞེ་ས",
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Honorific (Zhe-sa): $honorificText",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }
          }

          Text(
            text = culturalNote,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}
