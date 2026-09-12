package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CouplePink
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface

@Composable
fun CoupleSettingsDialog(
  currentP1: String,
  currentP1Pin: String,
  currentP2: String,
  currentP2Pin: String,
  currentDays: Int,
  onSaveCredentials: (p1Name: String, p1Pin: String, p2Name: String, p2Pin: String, days: Int) -> Unit,
  onClearChat: () -> Unit,
  onDismiss: () -> Unit
) {
  var p1Name by remember { mutableStateOf(currentP1) }
  var p1Pin by remember { mutableStateOf(currentP1Pin) }
  var p2Name by remember { mutableStateOf(currentP2) }
  var p2Pin by remember { mutableStateOf(currentP2Pin) }
  var daysText by remember { mutableStateOf(currentDays.toString()) }
  var showClearConfirm by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = TelegramDarkSurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("couple_settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = CouplePink,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Couple Sanctuary",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF94A3B8)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Days Together banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TelegramDarkInput)
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "❤️", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Together for $daysText days",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Personal passcode determines login profile",
                color = CouplePink,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Partner 1 (Him) Section
        Text(
          text = "Partner 1 (You / Him)",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TelegramCyan,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
        )

        OutlinedTextField(
          value = p1Name,
          onValueChange = { p1Name = it },
          label = { Text("Partner 1 Name") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = TelegramCyan)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TelegramCyan,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = p1Pin,
          onValueChange = { p1Pin = it },
          label = { Text("Partner 1 Passcode / Password") },
          leadingIcon = {
            Icon(Icons.Default.Key, contentDescription = null, tint = TelegramCyan)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TelegramCyan,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Partner 2 (Her / GF) Section
        Text(
          text = "Partner 2 (GF / Her)",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = CouplePink,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
        )

        OutlinedTextField(
          value = p2Name,
          onValueChange = { p2Name = it },
          label = { Text("Partner 2 Name") },
          leadingIcon = {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = CouplePink)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CouplePink,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = p2Pin,
          onValueChange = { p2Pin = it },
          label = { Text("Partner 2 Passcode / Password") },
          leadingIcon = {
            Icon(Icons.Default.Key, contentDescription = null, tint = CouplePink)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CouplePink,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Relationship Days Field
        OutlinedTextField(
          value = daysText,
          onValueChange = { daysText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Anniversary Days Count") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TelegramCyan,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            val days = daysText.toIntOrNull() ?: currentDays
            onSaveCredentials(p1Name, p1Pin, p2Name, p2Pin, days)
          },
          colors = ButtonDefaults.buttonColors(containerColor = TelegramCyan),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_settings_button")
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Save Credentials", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!showClearConfirm) {
          Button(
            onClick = { showClearConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear Chat History", color = Color(0xFFEF4444))
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFEF4444).copy(alpha = 0.15f))
              .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Are you sure you want to clear all messages?",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { showClearConfirm = false },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Cancel", color = Color.White)
              }
              Button(
                onClick = {
                  onClearChat()
                  showClearConfirm = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Clear All", color = Color.White)
              }
            }
          }
        }
      }
    }
  }
}
