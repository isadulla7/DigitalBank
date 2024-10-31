package uz.fido.universaldigital.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityCallSafeBinding
import uz.fido.universaldigital.services.AudioModeService

/**
 * This activity opens when user in CALL
 */

@AndroidEntryPoint
class CallSafeActivity : BaseActivity() {

    private lateinit var binding: ActivityCallSafeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_UniversalMobileDigital)
        super.onCreate(savedInstanceState)
        isActivityOpen = true
        binding = ActivityCallSafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_CLOSE_ACTIVITY" -> finishIfOpen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            startService(Intent(this, AudioModeService::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    broadcastReceiver, IntentFilter("ACTION_CLOSE_ACTIVITY"), Context.RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(
                    broadcastReceiver, IntentFilter("ACTION_CLOSE_ACTIVITY")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(broadcastReceiver)
            stopService(Intent(this, AudioModeService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setOnClickListener() {

    }

    private fun finishIfOpen() {
        if (isActivityOpen) finish()
    }

    companion object {
        var isActivityOpen = false
    }
}
