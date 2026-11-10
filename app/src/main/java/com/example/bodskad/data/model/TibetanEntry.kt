package com.example.bodskad.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tibetan_entries")
data class TibetanEntry(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val tibetan: String,
  val wylie: String,
  val phonetic: String,
  val english: String,
  val category: String,
  val difficulty: String = "Beginner", // Beginner, Intermediate, Advanced
  val partOfSpeech: String = "Phrase", // Noun, Verb, Adjective, Phrase, Particle
  val honorificForm: String = "",      // Zhe-sa (ཞེ་ས) honorific equivalent
  val lhasaNuance: String = "",
  val amdoNuance: String = "",
  val khamNuance: String = "",
  val dharmaNuance: String = "",
  val culturalContext: String = "",    // Crucial cultural etiquette, situational norms
  val etymology: String = "",          // Syllabic decomposition
  val exampleTibetan: String = "",
  val exampleEnglish: String = "",
  val isFavorite: Boolean = false,
  val isFlashcard: Boolean = false,
  val srsInterval: Int = 1,
  val srsRepetitions: Int = 0,
  val srsEaseFactor: Float = 2.5f,
  val srsNextReview: Long = 0L,
  val srsMasteryLevel: Int = 0          // 0: New, 1: Learning, 2: Review, 3: Mastered
)
