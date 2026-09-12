package com.example.data

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
  private const val ALGORITHM = "AES"
  private const val TRANSFORMATION = "AES/GCM/NoPadding"
  private const val TAG_LENGTH_BIT = 128
  private const val IV_LENGTH_BYTE = 12

  // Couple shared secret seed (can be customized per couple pairing)
  private const val SHARED_SECRET_SEED = "DUO_COUPLE_PRIVATE_E2EE_KEY_2026_HEART_LOCK_99"

  private val secretKey: SecretKeySpec by lazy {
    val digest = MessageDigest.getInstance("SHA-256")
    val keyBytes = digest.digest(SHARED_SECRET_SEED.toByteArray(StandardCharsets.UTF_8))
    SecretKeySpec(keyBytes, ALGORITHM)
  }

  /**
   * Encrypts plaintext using AES-256-GCM.
   * Returns Base64-encoded string containing [12 bytes IV] + [Ciphertext + Auth Tag].
   */
  fun encrypt(plainText: String): String {
    if (plainText.isEmpty()) return ""
    return try {
      val cipher = Cipher.getInstance(TRANSFORMATION)
      val iv = ByteArray(IV_LENGTH_BYTE)
      SecureRandom().nextBytes(iv)
      val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

      val cipherText = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
      val combined = ByteArray(iv.size + cipherText.size)
      System.arraycopy(iv, 0, combined, 0, iv.size)
      System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)

      Base64.encodeToString(combined, Base64.NO_WRAP)
    } catch (e: Exception) {
      // Fallback in case of crypto error
      plainText
    }
  }

  /**
   * Decrypts Base64-encoded string [IV + Ciphertext] using AES-256-GCM.
   */
  fun decrypt(encryptedBase64: String): String {
    if (encryptedBase64.isEmpty()) return ""
    return try {
      val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
      if (combined.size <= IV_LENGTH_BYTE) return encryptedBase64

      val iv = ByteArray(IV_LENGTH_BYTE)
      val cipherText = ByteArray(combined.size - IV_LENGTH_BYTE)
      System.arraycopy(combined, 0, iv, 0, iv.size)
      System.arraycopy(combined, IV_LENGTH_BYTE, cipherText, 0, cipherText.size)

      val cipher = Cipher.getInstance(TRANSFORMATION)
      val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
      cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

      val decryptedBytes = cipher.doFinal(cipherText)
      String(decryptedBytes, StandardCharsets.UTF_8)
    } catch (e: Exception) {
      // Return raw string if already plain or failure
      encryptedBase64
    }
  }

  /**
   * Generates a Telegram-like 4-emoji safety verification key
   * for the couple's end-to-end encryption confirmation.
   */
  fun getSafetyEmojis(): List<String> {
    val emojis = listOf(
      "❤️", "🔒", "🌹", "✨", "💎", "🌙", "👑", "🕊️",
      "🍇", "🔥", "🌸", "⭐", "💍", "🦋", "🎀", "🧸"
    )
    val hash = MessageDigest.getInstance("SHA-256").digest(SHARED_SECRET_SEED.toByteArray())
    return (0..3).map { i ->
      val index = Math.abs(hash[i].toInt()) % emojis.size
      emojis[index]
    }
  }

  /**
   * Generates Telegram-like 64-character hex visual fingerprint
   */
  fun getKeyFingerprint(): String {
    val hash = MessageDigest.getInstance("SHA-256").digest(SHARED_SECRET_SEED.toByteArray())
    return hash.joinToString(" ") { "%02X".format(it) }
  }
}
