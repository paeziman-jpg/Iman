package com.example.bodskad.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsHelper(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
  private var isInitialized = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale.US)
      isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    } else {
      Log.w("TtsHelper", "TextToSpeech init failed with status: $status")
    }
  }

  fun speak(text: String, isTibetan: Boolean = false) {
    if (!isInitialized || text.isBlank()) return
    tts?.let { engine ->
      engine.stop()
      if (isTibetan) {
        // Pronounce phonetics with calm, articulate cadence
        engine.setSpeechRate(0.85f)
        engine.setPitch(1.05f)
      } else {
        engine.setSpeechRate(0.95f)
        engine.setPitch(1.0f)
      }
      engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BodskadUtteranceId")
    }
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
