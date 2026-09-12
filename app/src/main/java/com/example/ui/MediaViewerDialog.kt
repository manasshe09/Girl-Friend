package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.ChatMessage
import com.example.data.CryptoManager
import com.example.ui.theme.CouplePink
import com.example.ui.theme.TelegramCyan

@Composable
fun MediaViewerDialog(
  message: ChatMessage,
  onDismiss: () -> Unit
) {
  val caption = CryptoManager.decrypt(message.contentEncrypted)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .testTag("media_viewer_dialog")
    ) {
      // Media Content (Image or Video preview)
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        if (!message.mediaUri.isNullOrEmpty()) {
          AsyncImage(
            model = message.mediaUri,
            contentDescription = "Full Media",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          // Preset / Placeholder Media representation
          Box(
            modifier = Modifier
              .fillMaxWidth(0.9f)
              .height(300.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (message.messageType == "VIDEO") "🎬" else "📸",
                fontSize = 54.sp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = message.mediaFileName ?: "Couple Photo",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        if (message.messageType == "VIDEO") {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play Video",
              tint = Color.White,
              modifier = Modifier.size(44.dp)
            )
          }
        }
      }

      // Top bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
            )
          )
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
          Column {
            Text(
              text = message.senderName,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = TelegramCyan,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = " E2EE Encrypted Media",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }
        }

        IconButton(onClick = { /* Saved to gallery */ }) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
            tint = Color.White
          )
        }
      }

      // Bottom Caption
      if (caption.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
              )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
          Text(
            text = caption,
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 20.sp
          )
        }
      }
    }
  }
}
