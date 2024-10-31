package uz.fido.universaldigital.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.os.IBinder

class AudioModeService : Service() {

    private lateinit var audioManager: AudioManager
    private var audioModeObserver: ContentObserver? = null

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Observe audio mode changes
        audioModeObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                checkAudioMode()
            }
        }

        // Register the observer
        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            audioModeObserver!!
        )
    }

    private fun checkAudioMode() {
        when (audioManager.mode) {
            AudioManager.MODE_IN_CALL, AudioManager.MODE_RINGTONE -> sendBroadcast(Intent("ACTION_OPEN_ACTIVITY"))
            AudioManager.MODE_NORMAL -> sendBroadcast(Intent("ACTION_CLOSE_ACTIVITY"))
            AudioManager.MODE_CALL_REDIRECT -> {

            }

            AudioManager.MODE_CALL_SCREENING -> {

            }

            AudioManager.MODE_COMMUNICATION_REDIRECT -> {

            }

            AudioManager.MODE_IN_COMMUNICATION -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioModeObserver?.let { contentResolver.unregisterContentObserver(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
