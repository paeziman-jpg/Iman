package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = EmberGold,
  onPrimary = Color(0xFF3E2204),
  primaryContainer = CrimsonNightContainer,
  onPrimaryContainer = Color(0xFFFFD9DF),
  secondary = MistSage,
  onSecondary = Color(0xFF0F3532),
  secondaryContainer = Color(0xFF264C48),
  onSecondaryContainer = Color(0xFFCCE8E4),
  tertiary = EmberGoldDim,
  onTertiary = Color.White,
  background = NightBackground,
  onBackground = NightTextPrimary,
  surface = NightSurface,
  onSurface = NightTextPrimary,
  surfaceVariant = NightSurfaceVariant,
  onSurfaceVariant = NightTextSecondary,
  outline = NightBorder
)

private val LightColorScheme = lightColorScheme(
  primary = MonasteryCrimson,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFDADF),
  onPrimaryContainer = Color(0xFF400010),
  secondary = SaffronGold,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFDEAC),
  onSecondaryContainer = Color(0xFF2B1700),
  tertiary = LotusSage,
  onTertiary = Color.White,
  tertiaryContainer = LotusSageLight,
  onTertiaryContainer = Color(0xFF04201E),
  background = WarmParchment,
  onBackground = CharcoalText,
  surface = SoftLinen,
  onSurface = CharcoalText,
  surfaceVariant = WarmSand,
  onSurfaceVariant = MutedEarthText,
  outline = SandBorder
)

@Composable
fun BodskadTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep intentional soothing Himalayan aesthetic
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
