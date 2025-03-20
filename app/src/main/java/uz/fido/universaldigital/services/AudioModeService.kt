package uz.fido.universaldigital.services

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class AudioModeService : Service() {

    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private var isInCallMode = false
    private var isActivityOpened = false

    companion object {
        const val ACTION_OPEN_ACTIVITY = "ACTION_OPEN_ACTIVITY"
        const val ACTION_CLOSE_ACTIVITY = "ACTION_CLOSE_ACTIVITY"
    }

    private val checkAudioModeRunnable = object : Runnable {
        override fun run() {
            checkAudioMode()
            handler.postDelayed(this, 2000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        handler.post(checkAudioModeRunnable)
    }

    private fun checkAudioMode() {
        val mode = audioManager.mode
        if ((mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_RINGTONE || mode == AudioManager.MODE_IN_COMMUNICATION) && !isInCallMode) {
            isInCallMode = true
            if (!isActivityOpened) {
                isActivityOpened = true
                sendBroadcast(Intent(ACTION_OPEN_ACTIVITY))
            }
        } else if (mode == AudioManager.MODE_NORMAL && isInCallMode) {
            isInCallMode = false
            if (isActivityOpened) {
                isActivityOpened = false
                sendBroadcast(Intent(ACTION_CLOSE_ACTIVITY))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkAudioModeRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

}
