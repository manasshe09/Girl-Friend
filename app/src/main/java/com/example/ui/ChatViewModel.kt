package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayManager
import com.example.audio.AudioRecordManager
import com.example.audio.PlaybackState
import com.example.data.ChatMessage
import com.example.data.ChatDatabase
import com.example.data.ChatRepository
import com.example.data.CryptoManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class ChatUiState(
  val isLocked: Boolean = true,
  val activeUserId: String = "partner1", // "partner1" (Me) or "partner2" (GF)
  val partner1Name: String = "Kiran",
  val partner2Name: String = "Sweetheart ❤️",
  val partnerStatus: String = "online",
  val searchQuery: String = "",
  val isSearching: Boolean = false,
  val editingMessage: ChatMessage? = null,
  val deletingMessage: ChatMessage? = null,
  val actionSheetMessage: ChatMessage? = null,
  val showAttachSheet: Boolean = false,
  val showE2eeDialog: Boolean = false,
  val showSettingsDialog: Boolean = false,
  val fullScreenMediaMessage: ChatMessage? = null,
  val isRecordingVoice: Boolean = false,
  val recordingDurationSec: Int = 0,
  val selfDestructTimerSec: Int = 0,
  val anniversaryDays: Int = 365,
  val partner1Pin: String = "1111",
  val partner2Pin: String = "2222"
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: ChatRepository
  private val audioRecordManager: AudioRecordManager
  val audioPlayManager: AudioPlayManager

  private val _uiState = MutableStateFlow(ChatUiState())
  val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

  private var recordingTimerJob: Job? = null

  private val prefs = application.getSharedPreferences("duo_chat_prefs", Context.MODE_PRIVATE)

  init {
    val db = ChatDatabase.getDatabase(application)
    repository = ChatRepository(db.chatDao())
    audioRecordManager = AudioRecordManager(application)
    audioPlayManager = AudioPlayManager(application, viewModelScope)

    val p1Pin = prefs.getString("partner1_pin", "1111") ?: "1111"
    val p2Pin = prefs.getString("partner2_pin", "2222") ?: "2222"
    val p1Name = prefs.getString("partner1_name", "Kiran") ?: "Kiran"
    val p2Name = prefs.getString("partner2_name", "Sweetheart ❤️") ?: "Sweetheart ❤️"
    val days = prefs.getInt("anniversary_days", 365)

    _uiState.value = _uiState.value.copy(
      partner1Pin = p1Pin,
      partner2Pin = p2Pin,
      partner1Name = p1Name,
      partner2Name = p2Name,
      anniversaryDays = days
    )

    viewModelScope.launch {
      repository.seedInitialCoupleConversationIfEmpty(p1Name, p2Name)
    }
  }

  val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
    .combine(_uiState) { messages, state ->
      val activeUser = state.activeUserId
      val filtered = messages.filter { msg ->
        val notDeletedForMe = if (msg.senderId == activeUser) {
          !msg.isDeletedForSender
        } else {
          !msg.isDeletedForReceiver
        }
        notDeletedForMe && !msg.isDeletedForEveryone
      }

      if (state.searchQuery.isBlank()) {
        filtered
      } else {
        val q = state.searchQuery.trim().lowercase()
        filtered.filter { msg ->
          val decrypted = CryptoManager.decrypt(msg.contentEncrypted).lowercase()
          val fileName = (msg.mediaFileName ?: "").lowercase()
          decrypted.contains(q) || fileName.contains(q)
        }
      }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val latestPinnedMessage: StateFlow<ChatMessage?> = repository.latestPinnedMessage
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val playbackState: StateFlow<PlaybackState> = audioPlayManager.playbackState

  // Security / Lock with Auto User Detection based on Password entered
  fun unlockWithPin(pin: String): Pair<Boolean, String?> {
    val clean = pin.trim()
    return when (clean) {
      _uiState.value.partner1Pin -> {
        _uiState.value = _uiState.value.copy(
          isLocked = false,
          activeUserId = "partner1"
        )
        Pair(true, _uiState.value.partner1Name)
      }
      _uiState.value.partner2Pin -> {
        _uiState.value = _uiState.value.copy(
          isLocked = false,
          activeUserId = "partner2"
        )
        Pair(true, _uiState.value.partner2Name)
      }
      else -> Pair(false, null)
    }
  }

  fun loginWithUsernameAndPassword(username: String, pass: String): Boolean {
    val u = username.trim().lowercase()
    val p = pass.trim()
    val p1Name = _uiState.value.partner1Name.lowercase()
    val p2Name = _uiState.value.partner2Name.lowercase()

    if ((u == p1Name || u == "kiran" || u == "partner 1" || u == "p1" || u == "him") && p == _uiState.value.partner1Pin) {
      _uiState.value = _uiState.value.copy(isLocked = false, activeUserId = "partner1")
      return true
    }
    if ((u == p2Name || u.contains("sweetheart") || u.contains("gf") || u == "partner 2" || u == "p2" || u == "her") && p == _uiState.value.partner2Pin) {
      _uiState.value = _uiState.value.copy(isLocked = false, activeUserId = "partner2")
      return true
    }
    return false
  }

  fun unlockAsUser(userId: String) {
    _uiState.value = _uiState.value.copy(
      isLocked = false,
      activeUserId = userId
    )
  }

  fun lockApp() {
    audioPlayManager.stopCurrent()
    _uiState.value = _uiState.value.copy(
      isLocked = true,
      actionSheetMessage = null,
      editingMessage = null,
      deletingMessage = null,
      showAttachSheet = false
    )
  }

  fun updateCredentials(
    p1Name: String,
    p1Pin: String,
    p2Name: String,
    p2Pin: String,
    anniversary: Int
  ) {
    prefs.edit()
      .putString("partner1_name", p1Name)
      .putString("partner1_pin", p1Pin)
      .putString("partner2_name", p2Name)
      .putString("partner2_pin", p2Pin)
      .putInt("anniversary_days", anniversary)
      .apply()
    _uiState.value = _uiState.value.copy(
      partner1Name = p1Name,
      partner1Pin = p1Pin,
      partner2Name = p2Name,
      partner2Pin = p2Pin,
      anniversaryDays = anniversary,
      showSettingsDialog = false
    )
  }

  // Profile switch between couple
  fun switchActiveUser() {
    val next = if (_uiState.value.activeUserId == "partner1") "partner2" else "partner1"
    _uiState.value = _uiState.value.copy(
      activeUserId = next,
      partnerStatus = if (next == "partner1") "online" else "typing..."
    )
  }

  // Messaging Actions
  fun sendTextMessage(text: String) {
    if (text.isBlank()) return
    viewModelScope.launch {
      val state = _uiState.value
      val senderName = if (state.activeUserId == "partner1") state.partner1Name else state.partner2Name
      repository.sendMessage(
        senderId = state.activeUserId,
        senderName = senderName,
        plainText = text.trim(),
        messageType = "TEXT",
        selfDestructSeconds = state.selfDestructTimerSec
      )
    }
  }

  fun sendMediaMessage(
    type: String, // "IMAGE", "VIDEO", "DOCUMENT"
    uri: String,
    fileName: String?,
    fileSize: String?,
    caption: String
  ) {
    viewModelScope.launch {
      val state = _uiState.value
      val senderName = if (state.activeUserId == "partner1") state.partner1Name else state.partner2Name
      repository.sendMessage(
        senderId = state.activeUserId,
        senderName = senderName,
        plainText = caption.trim(),
        messageType = type,
        mediaUri = uri,
        mediaFileName = fileName,
        mediaFileSize = fileSize,
        selfDestructSeconds = state.selfDestructTimerSec
      )
      _uiState.value = _uiState.value.copy(showAttachSheet = false)
    }
  }

  // Voice Notes
  fun startVoiceRecording() {
    audioPlayManager.stopCurrent()
    val file = audioRecordManager.startRecording()
    _uiState.value = _uiState.value.copy(
      isRecordingVoice = true,
      recordingDurationSec = 0
    )

    recordingTimerJob?.cancel()
    recordingTimerJob = viewModelScope.launch {
      while (_uiState.value.isRecordingVoice) {
        delay(1000)
        _uiState.value = _uiState.value.copy(
          recordingDurationSec = _uiState.value.recordingDurationSec + 1
        )
      }
    }
  }

  fun stopAndSendVoiceRecording() {
    recordingTimerJob?.cancel()
    val (file, duration) = audioRecordManager.stopRecording()
    _uiState.value = _uiState.value.copy(isRecordingVoice = false, recordingDurationSec = 0)

    val validDuration = duration.coerceAtLeast(1)
    viewModelScope.launch {
      val state = _uiState.value
      val senderName = if (state.activeUserId == "partner1") state.partner1Name else state.partner2Name
      repository.sendMessage(
        senderId = state.activeUserId,
        senderName = senderName,
        plainText = "Voice message ($validDuration s)",
        messageType = "VOICE",
        mediaUri = file?.absolutePath,
        mediaFileName = file?.name ?: "voice_note.m4a",
        mediaFileSize = "${validDuration * 12} KB",
        durationSeconds = validDuration,
        selfDestructSeconds = state.selfDestructTimerSec
      )
    }
  }

  fun cancelVoiceRecording() {
    recordingTimerJob?.cancel()
    audioRecordManager.cancelRecording()
    _uiState.value = _uiState.value.copy(isRecordingVoice = false, recordingDurationSec = 0)
  }

  // Edit Message
  fun startEditingMessage(message: ChatMessage) {
    _uiState.value = _uiState.value.copy(
      editingMessage = message,
      actionSheetMessage = null
    )
  }

  fun cancelEditing() {
    _uiState.value = _uiState.value.copy(editingMessage = null)
  }

  fun submitEditedMessage(newText: String) {
    val target = _uiState.value.editingMessage ?: return
    if (newText.isBlank()) return
    viewModelScope.launch {
      repository.editMessage(target.id, newText.trim())
      _uiState.value = _uiState.value.copy(editingMessage = null)
    }
  }

  // Delete Message
  fun promptDeleteMessage(message: ChatMessage) {
    _uiState.value = _uiState.value.copy(
      deletingMessage = message,
      actionSheetMessage = null
    )
  }

  fun dismissDeleteDialog() {
    _uiState.value = _uiState.value.copy(deletingMessage = null)
  }

  fun deleteForMe() {
    val target = _uiState.value.deletingMessage ?: return
    val isSender = target.senderId == _uiState.value.activeUserId
    viewModelScope.launch {
      repository.deleteMessageForMe(target.id, isSender)
      _uiState.value = _uiState.value.copy(deletingMessage = null)
    }
  }

  fun deleteForEveryone() {
    val target = _uiState.value.deletingMessage ?: return
    viewModelScope.launch {
      repository.deleteMessageForEveryone(target.id)
      _uiState.value = _uiState.value.copy(deletingMessage = null)
    }
  }

  // Pin / Reaction
  fun togglePin(id: Long) {
    viewModelScope.launch {
      repository.togglePin(id)
      _uiState.value = _uiState.value.copy(actionSheetMessage = null)
    }
  }

  fun setReaction(id: Long, emoji: String) {
    viewModelScope.launch {
      repository.setReaction(id, emoji)
      _uiState.value = _uiState.value.copy(actionSheetMessage = null)
    }
  }

  // Search
  fun toggleSearch() {
    val nextSearching = !_uiState.value.isSearching
    _uiState.value = _uiState.value.copy(
      isSearching = nextSearching,
      searchQuery = if (!nextSearching) "" else _uiState.value.searchQuery
    )
  }

  fun updateSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  // UI Modals
  fun openActionSheet(message: ChatMessage) {
    _uiState.value = _uiState.value.copy(actionSheetMessage = message)
  }

  fun closeActionSheet() {
    _uiState.value = _uiState.value.copy(actionSheetMessage = null)
  }

  fun toggleAttachSheet(show: Boolean) {
    _uiState.value = _uiState.value.copy(showAttachSheet = show)
  }

  fun toggleE2eeDialog(show: Boolean) {
    _uiState.value = _uiState.value.copy(showE2eeDialog = show)
  }

  fun toggleSettingsDialog(show: Boolean) {
    _uiState.value = _uiState.value.copy(showSettingsDialog = show)
  }

  fun openFullScreenMedia(message: ChatMessage) {
    _uiState.value = _uiState.value.copy(fullScreenMediaMessage = message)
  }

  fun closeFullScreenMedia() {
    _uiState.value = _uiState.value.copy(fullScreenMediaMessage = null)
  }

  fun setSelfDestructTimer(seconds: Int) {
    _uiState.value = _uiState.value.copy(selfDestructTimerSec = seconds)
  }

  fun clearChat() {
    viewModelScope.launch {
      repository.clearChat()
      _uiState.value = _uiState.value.copy(showSettingsDialog = false)
    }
  }

  fun updateProfiles(p1: String, p2: String, anniversary: Int) {
    prefs.edit()
      .putString("partner1_name", p1)
      .putString("partner2_name", p2)
      .putInt("anniversary_days", anniversary)
      .apply()
    _uiState.value = _uiState.value.copy(
      partner1Name = p1,
      partner2Name = p2,
      anniversaryDays = anniversary,
      showSettingsDialog = false
    )
  }

  override fun onCleared() {
    super.onCleared()
    audioPlayManager.stopCurrent()
  }
}
