package com.example.bodskad.data.model

data class CultureTopic(
  val id: String,
  val titleTibetan: String,
  val titleEnglish: String,
  val category: String,
  val summary: String,
  val etiquetteRules: List<String>,
  val relatedPhrases: List<String>,
  val iconEmoji: String
)
