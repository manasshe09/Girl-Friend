package com.example.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
  val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()
  val latestPinnedMessage: Flow<ChatMessage?> = chatDao.getLatestPinnedMessage()

  suspend fun sendMessage(
    senderId: String,
    senderName: String,
    plainText: String,
    messageType: String = "TEXT",
    mediaUri: String? = null,
    mediaFileName: String? = null,
    mediaFileSize: String? = null,
    durationSeconds: Int = 0,
    selfDestructSeconds: Int = 0
  ): Long {
    val encryptedContent = CryptoManager.encrypt(plainText)
    val message = ChatMessage(
      senderId = senderId,
      senderName = senderName,
      contentEncrypted = encryptedContent,
      messageType = messageType,
      mediaUri = mediaUri,
      mediaFileName = mediaFileName,
      mediaFileSize = mediaFileSize,
      durationSeconds = durationSeconds,
      timestamp = System.currentTimeMillis(),
      selfDestructSeconds = selfDestructSeconds,
      status = "READ"
    )
    return chatDao.insertMessage(message)
  }

  suspend fun editMessage(id: Long, newPlainText: String) {
    val existing = chatDao.getMessageById(id) ?: return
    val updated = existing.copy(
      contentEncrypted = CryptoManager.encrypt(newPlainText),
      isEdited = true,
      editedTimestamp = System.currentTimeMillis()
    )
    chatDao.updateMessage(updated)
  }

  suspend fun deleteMessageForMe(id: Long, isSender: Boolean) {
    val existing = chatDao.getMessageById(id) ?: return
    val updated = if (isSender) {
      existing.copy(isDeletedForSender = true)
    } else {
      existing.copy(isDeletedForReceiver = true)
    }
    chatDao.updateMessage(updated)
  }

  suspend fun deleteMessageForEveryone(id: Long) {
    val existing = chatDao.getMessageById(id) ?: return
    // Telegram marks it or removes it completely
    chatDao.deleteMessageById(id)
  }

  suspend fun togglePin(id: Long) {
    val existing = chatDao.getMessageById(id) ?: return
    val newPinnedState = !existing.isPinned
    if (newPinnedState) {
      chatDao.unpinAll()
    }
    chatDao.updateMessage(existing.copy(isPinned = newPinnedState))
  }

  suspend fun setReaction(id: Long, reactionEmoji: String?) {
    val existing = chatDao.getMessageById(id) ?: return
    val newReaction = if (existing.reaction == reactionEmoji) null else reactionEmoji
    chatDao.updateMessage(existing.copy(reaction = newReaction))
  }

  suspend fun clearChat() {
    chatDao.clearAll()
  }

  suspend fun seedInitialCoupleConversationIfEmpty(partner1Name: String, partner2Name: String) {
    val count = chatDao.getMessageById(1)
    if (count == null) {
      val welcomeTime = System.currentTimeMillis() - 7200000 // 2 hours ago
      val m1 = ChatMessage(
        senderId = "partner1",
        senderName = partner1Name,
        contentEncrypted = CryptoManager.encrypt("Hi darling! Welcome to our private secret space ❤️🔒"),
        messageType = "TEXT",
        timestamp = welcomeTime,
        status = "READ",
        reaction = "❤️",
        isPinned = true
      )
      val m2 = ChatMessage(
        senderId = "partner2",
        senderName = partner2Name,
        contentEncrypted = CryptoManager.encrypt("Hey sweetheart! Wow, end-to-end encrypted like Telegram just for us two! Love this 🥰"),
        messageType = "TEXT",
        timestamp = welcomeTime + 120000,
        status = "READ",
        reaction = "🥰"
      )
      val m3 = ChatMessage(
        senderId = "partner1",
        senderName = partner1Name,
        contentEncrypted = CryptoManager.encrypt("We can share voice notes, photos, documents and delete/edit anytime!"),
        messageType = "TEXT",
        timestamp = welcomeTime + 240000,
        status = "READ"
      )
      chatDao.insertAll(listOf(m1, m2, m3))
    }
  }
}
