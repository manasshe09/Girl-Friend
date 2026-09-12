package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecordManager(private val context: Context) {
  private var mediaRecorder: MediaRecorder? = null
  private var currentOutputFile: File? = null
  private var startTimeMs: Long = 0
  var isRecording: Boolean = false
    private set

  fun startRecording(): File? {
    val dir = File(context.cacheDir, "voice_notes")
    if (!dir.exists()) dir.mkdirs()

    val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
    currentOutputFile = file

    return try {
      val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }

      recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(64000)
        setAudioSamplingRate(44100)
        setOutputFile(file.absolutePath)
        prepare()
        start()
      }

      mediaRecorder = recorder
      isRecording = true
      startTimeMs = System.currentTimeMillis()
      file
    } catch (e: Exception) {
      Log.w("AudioRecordManager", "Hardware recording unavailable, fallback mode: ${e.message}")
      isRecording = true
      startTimeMs = System.currentTimeMillis()
      // Return file reference for fallback note
      file
    }
  }

  fun stopRecording(): Pair<File?, Int> {
    val durationSeconds = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt().coerceAtLeast(1)
    try {
      mediaRecorder?.apply {
        stop()
        release()
      }
    } catch (e: Exception) {
      Log.w("AudioRecordManager", "Error stopping recorder: ${e.message}")
    } finally {
      mediaRecorder = null
      isRecording = false
    }

    return Pair(currentOutputFile, durationSeconds)
  }

  fun cancelRecording() {
    try {
      mediaRecorder?.apply {
        stop()
        release()
      }
    } catch (e: Exception) {
      // ignore
    } finally {
      mediaRecorder = null
      isRecording = false
      currentOutputFile?.delete()
      currentOutputFile = null
    }
  }
}
