package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
  @Query("SELECT * FROM chat_messages WHERE isDeletedForEveryone = 0 ORDER BY timestamp ASC")
  fun getAllMessages(): Flow<List<ChatMessage>>

  @Query("SELECT * FROM chat_messages WHERE isPinned = 1 AND isDeletedForEveryone = 0 ORDER BY timestamp DESC LIMIT 1")
  fun getLatestPinnedMessage(): Flow<ChatMessage?>

  @Query("SELECT * FROM chat_messages WHERE id = :id LIMIT 1")
  suspend fun getMessageById(id: Long): ChatMessage?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessage): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(messages: List<ChatMessage>)

  @Update
  suspend fun updateMessage(message: ChatMessage)

  @Query("DELETE FROM chat_messages WHERE id = :id")
  suspend fun deleteMessageById(id: Long)

  @Query("DELETE FROM chat_messages")
  suspend fun clearAll()

  @Query("UPDATE chat_messages SET isPinned = 0")
  suspend fun unpinAll()
}
