package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CouplePink
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkBg
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LockScreen(
  partner1Name: String,
  partner1Pin: String,
  partner2Name: String,
  partner2Pin: String,
  onUnlockWithPin: (String) -> Pair<Boolean, String?>,
  onLoginWithUsernameAndPassword: (String, String) -> Boolean,
  onQuickLoginAsUser: (String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Passcode, 1: Username/Password
  var enteredPin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }
  var welcomeMessage by remember { mutableStateOf<String?>(null) }
  val shakeOffset = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()

  // Username/Password tab fields
  var inputUsername by remember { mutableStateOf(partner1Name) }
  var inputPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var upError by remember { mutableStateOf<String?>(null) }

  fun triggerShake() {
    coroutineScope.launch {
      isError = true
      shakeOffset.animateTo(20f, tween(50))
      shakeOffset.animateTo(-20f, tween(50))
      shakeOffset.animateTo(15f, tween(50))
      shakeOffset.animateTo(-15f, tween(50))
      shakeOffset.animateTo(0f, tween(50))
      enteredPin = ""
      isError = false
    }
  }

  fun handleKey(digit: String) {
    if (enteredPin.length < 4) {
      val newPin = enteredPin + digit
      enteredPin = newPin
      if (newPin.length == 4) {
        val (success, user) = onUnlockWithPin(newPin)
        if (success && user != null) {
          welcomeMessage = "Welcome, $user! ❤️"
        } else {
          triggerShake()
        }
      }
    }
  }

  fun handleBackspace() {
    if (enteredPin.isNotEmpty()) {
      enteredPin = enteredPin.dropLast(1)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            TelegramDarkBg,
            TelegramDarkSurface,
            Color(0xFF0A0F1D)
          )
        )
      )
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .padding(horizontal = 20.dp)
      .testTag("lock_screen_container")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top couple header
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(TelegramCyan.copy(alpha = 0.2f), CouplePink.copy(alpha = 0.2f)))
            ),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(TelegramDarkInput),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Lock",
              tint = TelegramCyan,
              modifier = Modifier.size(26.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "DuoChat Private Space",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Enter passcode to decide user profile",
          fontSize = 13.sp,
          color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector: [🔢 Passcode PIN] [👤 Username & Pass]
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = TelegramDarkInput,
          modifier = Modifier.padding(horizontal = 12.dp)
        ) {
          Row(modifier = Modifier.padding(4.dp)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTab == 0) TelegramCyan else Color.Transparent)
                .clickable { selectedTab = 0 }
                .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
              Text(
                text = "🔢 Passcode PIN",
                fontSize = 13.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8)
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTab == 1) TelegramCyan else Color.Transparent)
                .clickable { selectedTab = 1 }
                .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
              Text(
                text = "👤 User & Pass",
                fontSize = 13.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 1) Color.White else Color(0xFF94A3B8)
              )
            }
          }
        }
      }

      // Dynamic User Welcome banner if successfully recognized
      if (welcomeMessage != null) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF22C55E).copy(alpha = 0.2f),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
          Text(
            text = welcomeMessage!!,
            color = Color(0xFF4ADE80),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // TAB 0: PASSCODE (Passcode decides user automatically!)
      if (selectedTab == 0) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
        ) {
          // Quick couple user hints / instant login cards
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Partner 1 Card
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = TelegramDarkInput,
              modifier = Modifier
                .weight(1f)
                .border(1.dp, TelegramCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .clickable {
                  enteredPin = partner1Pin
                  onUnlockWithPin(partner1Pin)
                }
                .testTag("p1_quick_login_card")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "👦", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = partner1Name,
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Pass: $partner1Pin",
                  color = TelegramCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            // Partner 2 Card
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = TelegramDarkInput,
              modifier = Modifier
                .weight(1f)
                .border(1.dp, CouplePink.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .clickable {
                  enteredPin = partner2Pin
                  onUnlockWithPin(partner2Pin)
                }
                .testTag("p2_quick_login_card")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "👧", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = partner2Name,
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Pass: $partner2Pin",
                  color = CouplePink,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // PIN Dots Display
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
          ) {
            Text(
              text = if (isError) "Incorrect Passcode! Try again" else "Enter 4-Digit Passcode",
              fontSize = 14.sp,
              color = if (isError) Color(0xFFEF4444) else Color(0xFFCBD5E1),
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              for (i in 0 until 4) {
                val filled = i < enteredPin.length
                Box(
                  modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(
                      when {
                        isError -> Color(0xFFEF4444)
                        filled -> TelegramCyan
                        else -> Color(0xFF334155)
                      }
                    )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Keypad (1 to 9, Biometric/Him, 0, Backspace)
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            val rows = listOf(
              listOf("1", "2", "3"),
              listOf("4", "5", "6"),
              listOf("7", "8", "9")
            )

            for (row in rows) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                for (digit in row) {
                  KeypadButton(text = digit, onClick = { handleKey(digit) })
                }
              }
            }

            // Bottom row: Biometric / Quick, 0, Backspace
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Biometric / Key icon
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(TelegramDarkInput)
                  .clickable {
                    // Quick unlock as Partner 1
                    onQuickLoginAsUser("partner1")
                  }
                  .testTag("biometric_unlock_button"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = "Quick Key",
                  tint = TelegramCyan,
                  modifier = Modifier.size(28.dp)
                )
              }

              // Zero
              KeypadButton(text = "0", onClick = { handleKey("0") })

              // Backspace
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(TelegramDarkInput)
                  .clickable { handleBackspace() }
                  .testTag("backspace_button"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Backspace,
                  contentDescription = "Backspace",
                  tint = Color(0xFFCBD5E1),
                  modifier = Modifier.size(22.dp)
                )
              }
            }
          }
        }
      } else {
        // TAB 1: USERNAME & PASSWORD
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Select or Enter Credentials",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Quick selection chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (inputUsername == partner1Name) TelegramCyan.copy(alpha = 0.2f) else TelegramDarkInput,
              border = if (inputUsername == partner1Name) androidx.compose.foundation.BorderStroke(1.5.dp, TelegramCyan) else null,
              modifier = Modifier
                .weight(1f)
                .clickable {
                  inputUsername = partner1Name
                  inputPassword = partner1Pin
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(text = "👦 ", fontSize = 16.sp)
                Text(text = partner1Name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (inputUsername == partner2Name) CouplePink.copy(alpha = 0.2f) else TelegramDarkInput,
              border = if (inputUsername == partner2Name) androidx.compose.foundation.BorderStroke(1.5.dp, CouplePink) else null,
              modifier = Modifier
                .weight(1f)
                .clickable {
                  inputUsername = partner2Name
                  inputPassword = partner2Pin
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(text = "👧 ", fontSize = 16.sp)
                Text(text = partner2Name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Username input
          OutlinedTextField(
            value = inputUsername,
            onValueChange = {
              inputUsername = it
              upError = null
            },
            label = { Text("Username") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = TelegramCyan)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TelegramCyan,
              unfocusedBorderColor = Color(0xFF334155),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_username_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Password input
          OutlinedTextField(
            value = inputPassword,
            onValueChange = {
              inputPassword = it
              upError = null
            },
            label = { Text("Password / PIN") },
            leadingIcon = {
              Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFFBBF24))
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = "Toggle password",
                  tint = Color(0xFF94A3B8)
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TelegramCyan,
              unfocusedBorderColor = Color(0xFF334155),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input")
          )

          if (upError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = upError!!,
              color = Color(0xFFEF4444),
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              val success = onLoginWithUsernameAndPassword(inputUsername, inputPassword)
              if (!success) {
                upError = "Invalid username or password! Please check."
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TelegramCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_submit_button")
          ) {
            Text(
              text = "Log In as Partner",
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Security footer
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = Color(0xFF4ADE80),
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
          text = "256-Bit E2EE Duo Couple Space",
          fontSize = 11.sp,
          color = Color(0xFF94A3B8)
        )
      }
    }
  }
}

@Composable
fun KeypadButton(
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(64.dp)
      .clip(CircleShape)
      .background(TelegramDarkInput)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .testTag("keypad_$text"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 24.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color.White,
      textAlign = TextAlign.Center
    )
  }
}
