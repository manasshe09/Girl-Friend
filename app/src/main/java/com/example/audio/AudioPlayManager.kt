package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class PlaybackState(
  val playingMessageId: Long? = null,
  val isPlaying: Boolean = false,
  val progress: Float = 0f,
  val currentSeconds: Int = 0,
  val totalSeconds: Int = 0,
  val speed: Float = 1.0f
)

class AudioPlayManager(private val context: Context, private val scope: CoroutineScope) {
  private var mediaPlayer: MediaPlayer? = null
  private var progressJob: Job? = null

  private val _playbackState = MutableStateFlow(PlaybackState())
  val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  fun togglePlay(messageId: Long, audioPath: String?, defaultDuration: Int) {
    val current = _playbackState.value
    if (current.playingMessageId == messageId && current.isPlaying) {
      pause()
      return
    }

    if (current.playingMessageId == messageId && !current.isPlaying && mediaPlayer != null) {
      resume()
      return
    }

    // Play new track
    startPlaying(messageId, audioPath, defaultDuration)
  }

  private fun startPlaying(messageId: Long, audioPath: String?, defaultDuration: Int) {
    stopCurrent()

    val file = audioPath?.let { File(it) }
    if (file != null && file.exists()) {
      try {
        val player = MediaPlayer().apply {
          setDataSource(context, Uri.fromFile(file))
          prepare()
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playbackParams = playbackParams.setSpeed(_playbackState.value.speed)
          }
          start()
        }
        mediaPlayer = player

        val totalMs = player.duration.coerceAtLeast(defaultDuration * 1000)
        _playbackState.value = _playbackState.value.copy(
          playingMessageId = messageId,
          isPlaying = true,
          totalSeconds = (totalMs / 1000).coerceAtLeast(1)
        )

        player.setOnCompletionListener {
          stopCurrent()
        }

        startProgressTracking(messageId, totalMs)
      } catch (e: Exception) {
        Log.w("AudioPlayManager", "Error playing actual audio, using simulated playback: ${e.message}")
        startSimulatedPlayback(messageId, defaultDuration)
      }
    } else {
      startSimulatedPlayback(messageId, defaultDuration)
    }
  }

  private fun startSimulatedPlayback(messageId: Long, defaultDuration: Int) {
    val totalSec = defaultDuration.coerceAtLeast(5)
    _playbackState.value = _playbackState.value.copy(
      playingMessageId = messageId,
      isPlaying = true,
      currentSeconds = 0,
      totalSeconds = totalSec,
      progress = 0f
    )

    progressJob = scope.launch(Dispatchers.Main) {
      var sec = 0
      while (sec < totalSec) {
        delay((1000 / _playbackState.value.speed).toLong())
        if (!_playbackState.value.isPlaying) break
        sec++
        _playbackState.value = _playbackState.value.copy(
          currentSeconds = sec,
          progress = sec.toFloat() / totalSec.toFloat()
        )
      }
      stopCurrent()
    }
  }

  private fun startProgressTracking(messageId: Long, totalMs: Int) {
    progressJob?.cancel()
    progressJob = scope.launch(Dispatchers.Main) {
      while (mediaPlayer?.isPlaying == true) {
        val currentMs = mediaPlayer?.currentPosition ?: 0
        val currentSec = currentMs / 1000
        val progress = if (totalMs > 0) currentMs.toFloat() / totalMs.toFloat() else 0f
        _playbackState.value = _playbackState.value.copy(
          currentSeconds = currentSec,
          progress = progress.coerceIn(0f, 1f)
        )
        delay(100)
      }
    }
  }

  private fun pause() {
    try {
      mediaPlayer?.pause()
    } catch (e: Exception) {
      // ignore
    }
    progressJob?.cancel()
    _playbackState.value = _playbackState.value.copy(isPlaying = false)
  }

  private fun resume() {
    try {
      mediaPlayer?.start()
    } catch (e: Exception) {
      // ignore
    }
    _playbackState.value = _playbackState.value.copy(isPlaying = true)
    _playbackState.value.playingMessageId?.let { id ->
      val totalMs = (mediaPlayer?.duration ?: (_playbackState.value.totalSeconds * 1000)).coerceAtLeast(1)
      startProgressTracking(id, totalMs)
    }
  }

  fun cycleSpeed() {
    val speeds = listOf(1.0f, 1.5f, 2.0f)
    val current = _playbackState.value.speed
    val nextIndex = (speeds.indexOf(current) + 1) % speeds.size
    val nextSpeed = speeds[nextIndex]

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer?.isPlaying == true) {
      try {
        mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(nextSpeed) ?: return
      } catch (e: Exception) {
        // ignore
      }
    }
    _playbackState.value = _playbackState.value.copy(speed = nextSpeed)
  }

  fun stopCurrent() {
    progressJob?.cancel()
    progressJob = null
    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
    } catch (e: Exception) {
      // ignore
    }
    mediaPlayer = null
    _playbackState.value = _playbackState.value.copy(
      playingMessageId = null,
      isPlaying = false,
      progress = 0f,
      currentSeconds = 0
    )
  }
}
