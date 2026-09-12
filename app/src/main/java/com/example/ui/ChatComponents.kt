package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.PlaybackState
import com.example.data.ChatMessage
import com.example.data.CryptoManager
import com.example.ui.theme.CouplePink
import com.example.ui.theme.CoupleRose
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface
import com.example.ui.theme.TelegramReceivedBubble
import com.example.ui.theme.TelegramSentBubble
import com.example.ui.theme.TickBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubbleItem(
  message: ChatMessage,
  isSentByMe: Boolean,
  playbackState: PlaybackState,
  onPlayVoice: () -> Unit,
  onCycleSpeed: () -> Unit,
  onMediaClick: () -> Unit,
  onBubbleClick: () -> Unit,
  onBubbleLongClick: () -> Unit
) {
  val plainText = CryptoManager.decrypt(message.contentEncrypted)
  val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  val bubbleShape = if (isSentByMe) {
    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
  } else {
    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
  }

  val bubbleColor = if (isSentByMe) TelegramSentBubble else TelegramReceivedBubble

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 3.dp),
    horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start
  ) {
    Box {
      Surface(
        shape = bubbleShape,
        color = bubbleColor,
        tonalElevation = 2.dp,
        modifier = Modifier
          .widthIn(min = 90.dp, max = 310.dp)
          .combinedClickable(
            onClick = onBubbleClick,
            onLongClick = onBubbleLongClick
          )
          .testTag("message_bubble_${message.id}")
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
          // Sender name in received messages
          if (!isSentByMe) {
            Text(
              text = message.senderName,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CouplePink,
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }

          // Content based on messageType
          when (message.messageType) {
            "IMAGE" -> {
              ImageBubbleContent(
                message = message,
                caption = plainText,
                onClick = onMediaClick
              )
            }
            "VIDEO" -> {
              VideoBubbleContent(
                message = message,
                caption = plainText,
                onClick = onMediaClick
              )
            }
            "DOCUMENT" -> {
              DocumentBubbleContent(
                message = message,
                caption = plainText,
                onClick = onMediaClick
              )
            }
            "VOICE" -> {
              VoiceNoteBubbleContent(
                message = message,
                playbackState = playbackState,
                onPlayToggle = onPlayVoice,
                onCycleSpeed = onCycleSpeed
              )
            }
            else -> {
              // Standard Text Message
              Text(
                text = plainText,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 2.dp)
              )
            }
          }

          // Timestamp, Edited Badge, Status Ticks
          Row(
            modifier = Modifier
              .align(Alignment.End)
              .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (message.isEdited) {
              Text(
                text = "edited ",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(end = 2.dp)
              )
            }

            if (message.selfDestructSeconds > 0) {
              Text(
                text = "⏱️ ${message.selfDestructSeconds}s ",
                fontSize = 10.sp,
                color = CouplePink,
                modifier = Modifier.padding(end = 2.dp)
              )
            }

            Text(
              text = formattedTime,
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )

            if (isSentByMe) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = TickBlue,
                modifier = Modifier.size(15.dp)
              )
            }
          }
        }
      }

      // Reaction pill attached to bubble
      if (!message.reaction.isNullOrEmpty()) {
        Box(
          modifier = Modifier
            .align(if (isSentByMe) Alignment.BottomStart else Alignment.BottomEnd)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(CircleShape)
            .background(TelegramDarkInput)
            .border(1.dp, Color(0xFF334155), CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(text = message.reaction, fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
fun ImageBubbleContent(
  message: ChatMessage,
  caption: String,
  onClick: () -> Unit
) {
  Column(modifier = Modifier.clickable(onClick = onClick)) {
    if (!message.mediaUri.isNullOrEmpty()) {
      AsyncImage(
        model = message.mediaUri,
        contentDescription = "Photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(10.dp))
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1E293B)),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📸", fontSize = 36.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = message.mediaFileName ?: "Couple Photo",
            fontSize = 12.sp,
            color = Color.White
          )
        }
      }
    }

    if (caption.isNotBlank()) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = caption,
        color = Color.White,
        fontSize = 14.sp
      )
    }
  }
}

@Composable
fun VideoBubbleContent(
  message: ChatMessage,
  caption: String,
  onClick: () -> Unit
) {
  Column(modifier = Modifier.clickable(onClick = onClick)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF0F172A)),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "🎥", fontSize = 34.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = message.mediaFileName ?: "Video Clip",
          fontSize = 12.sp,
          color = Color.White
        )
      }

      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Play",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }
    }

    if (caption.isNotBlank()) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = caption,
        color = Color.White,
        fontSize = 14.sp
      )
    }
  }
}

@Composable
fun DocumentBubbleContent(
  message: ChatMessage,
  caption: String,
  onClick: () -> Unit
) {
  Column(modifier = Modifier.clickable(onClick = onClick)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF0F172A).copy(alpha = 0.4f))
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(TelegramCyan),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Description,
          contentDescription = "Doc",
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = message.mediaFileName ?: "Document.pdf",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = message.mediaFileSize ?: "1.8 MB",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )
      }
    }

    if (caption.isNotBlank()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = caption,
        color = Color.White,
        fontSize = 14.sp
      )
    }
  }
}

@Composable
fun VoiceNoteBubbleContent(
  message: ChatMessage,
  playbackState: PlaybackState,
  onPlayToggle: () -> Unit,
  onCycleSpeed: () -> Unit
) {
  val isThisPlaying = playbackState.playingMessageId == message.id && playbackState.isPlaying
  val progress = if (playbackState.playingMessageId == message.id) playbackState.progress else 0f
  val duration = message.durationSeconds.coerceAtLeast(1)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Play / Pause Circle Button
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(TelegramCyan)
        .clickable(onClick = onPlayToggle),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = if (isThisPlaying) "Pause" else "Play",
        tint = Color.White,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      // Waveform Visualization Bars
      WaveformBars(
        progress = progress,
        isPlaying = isThisPlaying
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val currentSec = if (playbackState.playingMessageId == message.id) {
          playbackState.currentSeconds
        } else {
          0
        }

        Text(
          text = "${formatDuration(currentSec)} / ${formatDuration(duration)}",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )

        // Speed Chip
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .clickable(onClick = onCycleSpeed)
            .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
          Text(
            text = "${playbackState.speed}x",
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun WaveformBars(
  progress: Float,
  isPlaying: Boolean
) {
  val barHeights = listOf(
    10, 16, 8, 22, 14, 28, 12, 18, 24, 16,
    20, 14, 26, 12, 22, 18, 14, 24, 10, 16
  )

  val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(30.dp),
    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    barHeights.forEachIndexed { index, baseHeight ->
      val barFraction = index.toFloat() / barHeights.size.toFloat()
      val isPassed = barFraction <= progress
      val finalHeight = if (isPlaying && isPassed) {
        (baseHeight * pulse).coerceIn(6f, 30f)
      } else {
        baseHeight.toFloat()
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .height(finalHeight.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(
            if (isPassed) TelegramCyan else Color(0xFF64748B)
          )
      )
    }
  }
}

fun formatDuration(seconds: Int): String {
  val m = seconds / 60
  val s = seconds % 60
  return "%d:%02d".format(m, s)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActionBottomSheet(
  message: ChatMessage,
  isSentByMe: Boolean,
  onReaction: (String) -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onPin: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = TelegramDarkSurface,
    modifier = Modifier.testTag("message_action_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Quick reaction emojis row (Telegram style)
      Text(
        text = "Quick Reactions",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(bottom = 8.dp)
      )

      val reactions = listOf("❤️", "🔥", "🥰", "🥺", "😂", "👍", "💍")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(TelegramDarkInput)
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        reactions.forEach { emoji ->
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .clickable {
                onReaction(emoji)
                onDismiss()
              },
            contentAlignment = Alignment.Center
          ) {
            Text(text = emoji, fontSize = 24.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action Rows
      if (isSentByMe && message.messageType == "TEXT") {
        ActionMenuRow(
          icon = Icons.Default.Edit,
          title = "Edit Message",
          tint = TelegramCyan,
          onClick = {
            onDismiss()
            onEdit()
          }
        )
      }

      ActionMenuRow(
        icon = Icons.Default.PushPin,
        title = if (message.isPinned) "Unpin from Top" else "Pin to Top",
        tint = Color(0xFFFBBF24),
        onClick = {
          onDismiss()
          onPin()
        }
      )

      ActionMenuRow(
        icon = Icons.Default.Delete,
        title = "Delete Message",
        tint = Color(0xFFEF4444),
        onClick = {
          onDismiss()
          onDelete()
        }
      )

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun ActionMenuRow(
  icon: ImageVector,
  title: String,
  tint: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp))
    Spacer(modifier = Modifier.width(14.dp))
    Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
  }
}

@Composable
fun DeleteConfirmationDialog(
  isSentByMe: Boolean,
  onDeleteForMe: () -> Unit,
  onDeleteForEveryone: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = TelegramDarkSurface,
    title = {
      Text(
        text = "Delete message?",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Text(
        text = "Do you want to delete this message just for yourself or for both of you?",
        color = Color(0xFFCBD5E1),
        fontSize = 14.sp
      )
    },
    confirmButton = {
      Button(
        onClick = onDeleteForEveryone,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Delete for Everyone", color = Color.White)
      }
    },
    dismissButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onDeleteForMe) {
          Text("Delete for Me", color = TelegramCyan)
        }
        TextButton(onClick = onDismiss) {
          Text("Cancel", color = Color(0xFF94A3B8))
        }
      }
    },
    modifier = Modifier.testTag("delete_confirmation_dialog")
  )
}

@Composable
fun EditMessageDialog(
  originalMessage: ChatMessage,
  onConfirmEdit: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var text by remember {
    mutableStateOf(CryptoManager.decrypt(originalMessage.contentEncrypted))
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = TelegramDarkSurface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Edit, contentDescription = null, tint = TelegramCyan)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Edit Message", color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = TelegramCyan,
          unfocusedBorderColor = Color(0xFF334155),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("edit_message_input")
      )
    },
    confirmButton = {
      Button(
        onClick = { onConfirmEdit(text) },
        colors = ButtonDefaults.buttonColors(containerColor = TelegramCyan),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Save", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = Color(0xFF94A3B8))
      }
    }
  )
}
