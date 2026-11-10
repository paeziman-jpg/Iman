package com.example.bodskad.data.model

enum class Dialect(
  val id: String,
  val displayName: String,
  val tibetanName: String,
  val phoneticName: String,
  val description: String,
  val characteristics: String
) {
  CENTRAL(
    id = "central",
    displayName = "Central / Lhasa (Standard)",
    tibetanName = "དབུས་སྐད།",
    phoneticName = "Ü-ke / Lhasa",
    description = "Official lingua franca and standard dialect used across Central Tibet (Ü-Tsang) and diaspora communities.",
    characteristics = "Distinguished by tonal registers (high/low), subtle prefixes, and rich honorific (ཞེ་ས Zhe-sa) structures."
  ),
  AMDO(
    id = "amdo",
    displayName = "Amdo Dialect",
    tibetanName = "ཨ་མདོ་སྐད།",
    phoneticName = "Amdo-ke",
    description = "Spoken across Qinghai, southern Gansu, and northern Sichuan.",
    characteristics = "Non-tonal, preserves archaic consonant clusters and prefix sounds that were lost in Central Tibetan."
  ),
  KHAM(
    id = "kham",
    displayName = "Kham Dialect",
    tibetanName = "ཁམས་སྐད།",
    phoneticName = "Kham-ke",
    description = "Spoken throughout eastern Tibet, western Sichuan, and northwestern Yunnan.",
    characteristics = "Lyrical cadence with a 2 to 4 tone system, vibrant colloquial idioms, and strong aspirational glides."
  ),
  DHARMA(
    id = "dharma",
    displayName = "Classical / Dharma (Chöke)",
    tibetanName = "ཆོས་སྐད།",
    phoneticName = "Chö-ke",
    description = "Canonical literary language used in Buddhist scriptures (Kangyur & Tengyur), philosophical debates, and liturgy.",
    characteristics = "Rigid classical syntax, profound spiritual vocabulary, honorific prefixes, and philosophical precision."
  )
}
