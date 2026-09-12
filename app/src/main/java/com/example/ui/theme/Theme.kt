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

private val DarkColorScheme =
  darkColorScheme(
    primary = TelegramCyan,
    secondary = CouplePink,
    tertiary = TelegramSentBubble,
    background = TelegramDarkBg,
    surface = TelegramDarkSurface,
    surfaceVariant = TelegramDarkInput,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0284C7),
    secondary = CouplePink,
    tertiary = LightSentBubble,
    background = LightChatBg,
    surface = LightSurface,
    surfaceVariant = Color(0xFFE2E8F0),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to Telegram dark theme for private couples chat
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

