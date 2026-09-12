package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CryptoManager
import com.example.ui.theme.CouplePink
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface

@Composable
fun E2eeInfoDialog(
  currentTimerSec: Int,
  onSetTimer: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  val emojis = CryptoManager.getSafetyEmojis()
  val fingerprint = CryptoManager.getKeyFingerprint()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = TelegramDarkSurface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("e2ee_info_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
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
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = TelegramCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "End-to-End Encryption",
              fontSize = 17.sp,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Telegram 4-Emoji Verification Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TelegramDarkInput)
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            emojis.forEach { emoji ->
              Text(
                text = emoji,
                fontSize = 32.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "If these 4 emojis match on both of your devices, your private chat is 100% secure and end-to-end encrypted with 256-bit AES-GCM.",
          fontSize = 13.sp,
          color = Color(0xFFCBD5E1),
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Fingerprint snippet
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color.Black.copy(alpha = 0.3f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = fingerprint.take(36) + "...",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF38BDF8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Self-Destruct Timer Option (Telegram Secret Chat)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = CouplePink,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Self-Destruct Timer:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val timerOptions = listOf(
          0 to "Off",
          10 to "10s",
          30 to "30s",
          60 to "1m",
          3600 to "1h"
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          timerOptions.forEach { (sec, label) ->
            val isSelected = currentTimerSec == sec
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) TelegramCyan else TelegramDarkInput)
                .clickable { onSetTimer(sec) }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF94A3B8)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = TelegramCyan),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "Got It, Encryption Active",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
