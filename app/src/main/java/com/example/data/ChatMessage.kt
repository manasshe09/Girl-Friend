package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val senderId: String, // "partner1" (Me) or "partner2" (GF)
  val senderName: String,
  val contentEncrypted: String,
  val messageType: String = "TEXT", // "TEXT", "IMAGE", "VIDEO", "DOCUMENT", "VOICE"
  val mediaUri: String? = null,
  val mediaFileName: String? = null,
  val mediaFileSize: String? = null,
  val durationSeconds: Int = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val isEdited: Boolean = false,
  val editedTimestamp: Long? = null,
  val isDeletedForSender: Boolean = false,
  val isDeletedForReceiver: Boolean = false,
  val isDeletedForEveryone: Boolean = false,
  val reaction: String? = null,
  val isPinned: Boolean = false,
  val selfDestructSeconds: Int = 0,
  val status: String = "READ" // "SENT", "DELIVERED", "READ"
)
