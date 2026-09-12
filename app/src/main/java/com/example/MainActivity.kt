package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChatViewModel
import com.example.ui.LockScreen
import com.example.ui.TelegramChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TelegramDarkBg

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        val chatViewModel: ChatViewModel = viewModel()
        val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = TelegramDarkBg
        ) {
          if (uiState.isLocked) {
            LockScreen(
              partner1Name = uiState.partner1Name,
              partner1Pin = uiState.partner1Pin,
              partner2Name = uiState.partner2Name,
              partner2Pin = uiState.partner2Pin,
              onUnlockWithPin = { pin -> chatViewModel.unlockWithPin(pin) },
              onLoginWithUsernameAndPassword = { user, pass ->
                chatViewModel.loginWithUsernameAndPassword(user, pass)
              },
              onQuickLoginAsUser = { userId -> chatViewModel.unlockAsUser(userId) }
            )
          } else {
            TelegramChatScreen(viewModel = chatViewModel)
          }
        }
      }
    }
  }
}

