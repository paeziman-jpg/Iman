package com.example.bodskad.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodskad.data.model.Dialect

@Composable
fun DialectSelector(
  selectedDialect: Dialect,
  onDialectSelected: (Dialect) -> Unit,
  modifier: Modifier = Modifier
) {
  var showInfoDialog by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Dialect Nuance",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IconButton(
          onClick = { showInfoDialog = true },
          modifier = Modifier
            .size(36.dp)
            .testTag("dialect_info_button")
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "About Dialect Nuances",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Text(
        text = selectedDialect.tibetanName,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Dialect.values().forEach { dialect ->
        val isSelected = dialect == selectedDialect
        FilterChip(
          selected = isSelected,
          onClick = { onDialectSelected(dialect) },
          label = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
              Text(
                text = dialect.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
              Text(
                text = dialect.phoneticName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("dialect_chip_${dialect.id}")
        )
      }
    }
  }

  if (showInfoDialog) {
    AlertDialog(
      onDismissRequest = { showInfoDialog = false },
      title = {
        Text(
          text = "Tibetan Dialects & Registers",
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Dialect.values().forEach { d ->
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = d.tibetanName,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• ${d.displayName}",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold
                )
              }
              Text(
                text = d.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = d.characteristics,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showInfoDialog = false }) {
          Text("Understood")
        }
      }
    )
  }
}
