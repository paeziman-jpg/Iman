package com.example.bodskad.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationHistory(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val sourceText: String,
  val translatedText: String,
  val sourceLanguage: String, // "en" or "bo"
  val targetLanguage: String, // "bo" or "en"
  val dialect: String,        // "central", "amdo", "kham", "dharma"
  val phoneticText: String,
  val wylieText: String,
  val culturalContext: String,
  val honorificText: String = "",
  val timestamp: Long = System.currentTimeMillis()
)
