package uz.fido.universaldigital.ui.activities

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
        binding = ActivityVpnErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.update.setOnClickListener {
            if (!SecurityCheck.isFromVpn()) {
                finish()
            }
        }

    }

    override fun onBackPressed() {
        if (onBackPress) {
            super.onBackPressed()
        }
    }
}
