package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ChatMessage
import com.example.data.CryptoManager
import com.example.ui.theme.CouplePink
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.TelegramCyan
import com.example.ui.theme.TelegramDarkBg
import com.example.ui.theme.TelegramDarkInput
import com.example.ui.theme.TelegramDarkSurface
import com.example.ui.theme.TelegramSentBubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramChatScreen(
  viewModel: ChatViewModel
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val messages by viewModel.allMessages.collectAsStateWithLifecycle()
  val pinnedMessage by viewModel.latestPinnedMessage.collectAsStateWithLifecycle()
  val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

  val context = LocalContext.current
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Auto-scroll to bottom when new messages arrive
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Permission Launcher for Voice recording
  val recordAudioLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    // Always start recording (fallback mode will kick in if permission denied)
    viewModel.startVoiceRecording()
  }

  fun requestRecordVoice() {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
      viewModel.startVoiceRecording()
    } else {
      recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  // Active Partner display
  val isMeActive = uiState.activeUserId == "partner1"
  val activeSpeakerName = if (isMeActive) uiState.partner1Name else uiState.partner2Name
  val otherPartnerName = if (isMeActive) uiState.partner2Name else uiState.partner1Name

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(TelegramDarkBg)
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .testTag("telegram_chat_screen"),
    containerColor = TelegramDarkBg,
    topBar = {
      Column(modifier = Modifier.background(TelegramDarkSurface)) {
        // Main Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Left: Partner Avatar & Status
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .weight(1f)
              .clickable { viewModel.toggleE2eeDialog(true) }
          ) {
            Box(contentAlignment = Alignment.BottomEnd) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(
                      if (isMeActive) listOf(CouplePink, Color(0xFFE11D48))
                      else listOf(TelegramCyan, Color(0xFF0284C7))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (isMeActive) "👧" else "👦",
                  fontSize = 20.sp
                )
              }
              // Online green indicator dot
              Box(
                modifier = Modifier
                  .size(11.dp)
                  .clip(CircleShape)
                  .background(OnlineGreen)
                  .border(1.5.dp, TelegramDarkSurface, CircleShape)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = otherPartnerName,
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Secret Chat",
                  tint = TelegramCyan,
                  modifier = Modifier.size(13.dp)
                )
              }
              Text(
                text = "${uiState.partnerStatus} • E2EE",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }

          // Active Speaker Switcher Chip (Me ⇄ GF)
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = TelegramDarkInput,
            modifier = Modifier
              .clickable { viewModel.switchActiveUser() }
              .testTag("speaker_toggle_chip")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isMeActive) "👦 Me" else "👧 GF",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMeActive) TelegramCyan else CouplePink
              )
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch active partner",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }

          // Actions: Search, Settings, Lock
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.toggleSearch() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (uiState.isSearching) TelegramCyan else Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = { viewModel.toggleSettingsDialog(true) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = { viewModel.lockApp() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        // Search Bar (expandable)
        AnimatedVisibility(visible = uiState.isSearching) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = uiState.searchQuery,
              onValueChange = { viewModel.updateSearchQuery(it) },
              placeholder = { Text("Search messages...", color = Color(0xFF64748B), fontSize = 13.sp) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TelegramCyan,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                  IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(16.dp))
                  }
                }
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("search_text_input")
            )
          }
        }

        // Telegram-style Pinned Message Banner
        if (pinnedMessage != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(TelegramDarkInput)
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(3.dp, 28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TelegramCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.PushPin,
              contentDescription = null,
              tint = TelegramCyan,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Pinned Message",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TelegramCyan
              )
              Text(
                text = CryptoManager.decrypt(pinnedMessage!!.contentEncrypted),
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            IconButton(
              onClick = { viewModel.togglePin(pinnedMessage!!.id) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Unpin",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(TelegramDarkSurface)
          .navigationBarsPadding()
          .imePadding()
      ) {
        // Edit Message Top Banner
        if (uiState.editingMessage != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(TelegramDarkInput)
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = TelegramCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Edit Message",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TelegramCyan
              )
              Text(
                text = CryptoManager.decrypt(uiState.editingMessage!!.contentEncrypted),
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            IconButton(
              onClick = { viewModel.cancelEditing() },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Edit",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        // Chat Input Row OR Voice Recording Row
        if (uiState.isRecordingVoice) {
          // Live Voice Recording State
          VoiceRecordingBar(
            durationSec = uiState.recordingDurationSec,
            onCancel = { viewModel.cancelVoiceRecording() },
            onSend = { viewModel.stopAndSendVoiceRecording() }
          )
        } else {
          // Standard Message Input Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Attach Button (+)
            IconButton(
              onClick = { viewModel.toggleAttachSheet(true) },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(TelegramDarkInput)
                .testTag("attach_button")
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Attach",
                tint = TelegramCyan,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Text Input Field
            Box(modifier = Modifier.weight(1f)) {
              TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                  Text(
                    text = if (uiState.editingMessage != null) "Edit text..." else "Message as $activeSpeakerName...",
                    color = Color(0xFF64748B),
                    fontSize = 15.sp
                  )
                },
                colors = TextFieldDefaults.colors(
                  focusedContainerColor = TelegramDarkInput,
                  unfocusedContainerColor = TelegramDarkInput,
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent,
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(22.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                  onSend = {
                    if (inputText.isNotBlank()) {
                      if (uiState.editingMessage != null) {
                        viewModel.submitEditedMessage(inputText)
                      } else {
                        viewModel.sendTextMessage(inputText)
                      }
                      inputText = ""
                    }
                  }
                ),
                maxLines = 4,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("chat_message_input")
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action button: Send or Voice Mic
            if (inputText.isNotBlank()) {
              IconButton(
                onClick = {
                  if (uiState.editingMessage != null) {
                    viewModel.submitEditedMessage(inputText)
                  } else {
                    viewModel.sendTextMessage(inputText)
                  }
                  inputText = ""
                },
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(TelegramCyan)
                  .testTag("send_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Send",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            } else {
              // Voice Note Mic Button
              IconButton(
                onClick = { requestRecordVoice() },
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(TelegramDarkInput)
                  .testTag("voice_note_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Record Voice Note",
                  tint = TelegramCyan,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    // Messages List
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (messages.isEmpty()) {
        // Friendly empty state
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(text = "🔒", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Your Private Couple Chat",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "End-to-End Encrypted. Send your first message, photo, or voice note to begin! ❤️",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      } else {
        LazyColumn(
          state = listState,
          contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp),
          modifier = Modifier
            .fillMaxSize()
            .testTag("chat_messages_list")
        ) {
          items(messages, key = { it.id }) { message ->
            val isSentByMe = message.senderId == uiState.activeUserId

            MessageBubbleItem(
              message = message,
              isSentByMe = isSentByMe,
              playbackState = playbackState,
              onPlayVoice = {
                viewModel.audioPlayManager.togglePlay(
                  messageId = message.id,
                  audioPath = message.mediaUri,
                  defaultDuration = message.durationSeconds
                )
              },
              onCycleSpeed = {
                viewModel.audioPlayManager.cycleSpeed()
              },
              onMediaClick = {
                viewModel.openFullScreenMedia(message)
              },
              onBubbleClick = {
                // If it's a media message, open viewer; else show actions
                if (message.messageType in listOf("IMAGE", "VIDEO")) {
                  viewModel.openFullScreenMedia(message)
                } else {
                  viewModel.openActionSheet(message)
                }
              },
              onBubbleLongClick = {
                viewModel.openActionSheet(message)
              }
            )
          }
        }
      }
    }
  }

  // Modals & Dialogs
  if (uiState.actionSheetMessage != null) {
    val target = uiState.actionSheetMessage!!
    MessageActionBottomSheet(
      message = target,
      isSentByMe = target.senderId == uiState.activeUserId,
      onReaction = { emoji -> viewModel.setReaction(target.id, emoji) },
      onEdit = {
        if (target.messageType == "TEXT") {
          inputText = CryptoManager.decrypt(target.contentEncrypted)
          viewModel.startEditingMessage(target)
        }
      },
      onDelete = { viewModel.promptDeleteMessage(target) },
      onPin = { viewModel.togglePin(target.id) },
      onDismiss = { viewModel.closeActionSheet() }
    )
  }

  if (uiState.deletingMessage != null) {
    DeleteConfirmationDialog(
      isSentByMe = uiState.deletingMessage!!.senderId == uiState.activeUserId,
      onDeleteForMe = { viewModel.deleteForMe() },
      onDeleteForEveryone = { viewModel.deleteForEveryone() },
      onDismiss = { viewModel.dismissDeleteDialog() }
    )
  }

  if (uiState.showAttachSheet) {
    AttachmentBottomSheet(
      onSendMedia = { type, uri, fileName, fileSize, caption ->
        viewModel.sendMediaMessage(type, uri, fileName, fileSize, caption)
      },
      onDismiss = { viewModel.toggleAttachSheet(false) }
    )
  }

  if (uiState.showE2eeDialog) {
    E2eeInfoDialog(
      currentTimerSec = uiState.selfDestructTimerSec,
      onSetTimer = { viewModel.setSelfDestructTimer(it) },
      onDismiss = { viewModel.toggleE2eeDialog(false) }
    )
  }

  if (uiState.showSettingsDialog) {
    CoupleSettingsDialog(
      currentP1 = uiState.partner1Name,
      currentP1Pin = uiState.partner1Pin,
      currentP2 = uiState.partner2Name,
      currentP2Pin = uiState.partner2Pin,
      currentDays = uiState.anniversaryDays,
      onSaveCredentials = { p1Name, p1Pin, p2Name, p2Pin, days ->
        viewModel.updateCredentials(p1Name, p1Pin, p2Name, p2Pin, days)
      },
      onClearChat = {
        viewModel.clearChat()
      },
      onDismiss = { viewModel.toggleSettingsDialog(false) }
    )
  }

  if (uiState.fullScreenMediaMessage != null) {
    MediaViewerDialog(
      message = uiState.fullScreenMediaMessage!!,
      onDismiss = { viewModel.closeFullScreenMedia() }
    )
  }
}

@Composable
fun VoiceRecordingBar(
  durationSec: Int,
  onCancel: () -> Unit,
  onSend: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "rec_alpha"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Red blinking dot + Duration
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(14.dp)
          .clip(CircleShape)
          .background(Color(0xFFEF4444).copy(alpha = alpha))
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = formatDuration(durationSec),
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = "Recording audio note...",
        color = Color(0xFF94A3B8),
        fontSize = 12.sp
      )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Cancel / Trash
      IconButton(
        onClick = onCancel,
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color(0xFF334155))
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Cancel Recording",
          tint = Color(0xFFEF4444),
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Send Voice Note
      IconButton(
        onClick = onSend,
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(TelegramCyan)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send Recording",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
