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
import uz.fido.universaldigital.databinding.ActivityVpnErrorBinding
import uz.fido.utils.security.SecurityCheck

/**
 * This activity opens when user try to open app with VPN
 */

@AndroidEntryPoint
class VpnErrorActivity : BaseActivity() {

    private lateinit var binding: ActivityVpnErrorBinding
    private var onBackPress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_UniversalMobileDigital)
        super.onCreate(savedInstanceState)
        isActivityOpen = true
        binding = ActivityVpnErrorBinding.inflate(layoutInflater)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, IntentFilter("ACTION_CLOSE_ACTIVITY"), RECEIVER_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, IntentFilter("ACTION_CLOSE_ACTIVITY"))
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }


    private fun setOnClickListener() {
        binding.update.setOnClickListener {
            if (!SecurityCheck.isFromVpn()) {
                finish()
            }
        }

    }

    private fun finishIfOpen() {
        if (isActivityOpen) finish()
    }

    companion object {
        var isActivityOpen = false
    }

    override fun onBackPressed() {
        if (onBackPress) {
            super.onBackPressed()
        }
    }
}
