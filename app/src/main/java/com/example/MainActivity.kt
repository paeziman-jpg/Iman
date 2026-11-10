package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bodskad.ui.MainScreen
import com.example.ui.theme.BodskadTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val systemInDark = isSystemInDarkTheme()
      var userDarkTheme by remember { mutableStateOf<Boolean?>(null) }
      val isDarkTheme = userDarkTheme ?: systemInDark

      BodskadTheme(darkTheme = isDarkTheme) {
        MainScreen(
          isDarkTheme = isDarkTheme,
          onToggleDarkTheme = {
            userDarkTheme = !isDarkTheme
          }
        )
      }
    }
  }
}
