package uz.fido.universaldigital.ui.activities.security

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivitySecurityViolationBinding
import kotlin.system.exitProcess

@AndroidEntryPoint
class SecurityViolationActivity : BaseActivity() {

    private lateinit var binding: ActivitySecurityViolationBinding
    private lateinit var violationType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        violationType = intent.getStringExtra(VIOLATION_TYPE) ?: EMULATOR_DETECTED
        binding = ActivitySecurityViolationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDetails()
        initSetOnClickListeners()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                terminateApplication()
            }
        })
    }

    private fun initDetails() {
        when (violationType) {
            EMULATOR_DETECTED -> {
                binding.tvDescription.text = getString(R.string.emulator_is_not_allowed)
            }

            ROOT_DETECTED -> {
                binding.tvDescription.text = getString(R.string.rooted_device_not_allowed)
            }

            else -> {
                binding.tvDescription.text = /*getString(R.string.other_violation_found)*/violationType
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.buttonOpenSettings.setOnClickListener { terminateApplication() }
    }

    private fun terminateApplication() {
        finishAffinity()
        exitProcess(0)
    }

    companion object {
        const val VIOLATION_TYPE = "violation_type"
        const val EMULATOR_DETECTED = "emulator_detected"
        const val ROOT_DETECTED = "root_detected"
        const val DEBUGGER_DETECTED = "debugger_detected"
        const val UNTRUSTED_INSTALLATION = "untrusted_installation"
        const val MALWARE_DETECTED = "malware_detected"
        const val VPN_DETECTED = "vpn_detected"
        const val TAMPER_DETECTED = "tamper_detected"
        const val HOOK_DETECTED = "hook_detected"

    }

}