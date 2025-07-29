package uz.fido.universaldigital.ui.activities.security

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivitySecurityViolationBinding
import kotlin.math.log
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
        logViolationEvent()
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

            DEBUGGER_DETECTED -> {
                binding.tvDescription.text = getString(R.string.debugger_detected)
            }

            UNTRUSTED_INSTALLATION -> {
                binding.tvDescription.text = getString(R.string.untrusted_installation)
            }

            MALWARE_DETECTED -> {
                binding.tvDescription.text = getString(R.string.malware_detected)
            }

            VPN_DETECTED -> {
                binding.tvDescription.text = getString(R.string.please_turn_off_vpn)
            }

            TAMPER_DETECTED -> {
                binding.tvDescription.text = getString(R.string.tamper_detected)
            }

            HOOK_DETECTED -> {
                binding.tvDescription.text = getString(R.string.hook_detected)
            }

            else -> {
                binding.tvDescription.text = getString(R.string.other_violation_found)
            }
        }
    }

    private fun logViolationEvent() {
        val deviceInfo = mapOf(
            "model" to Build.MODEL,
            "manufacturer" to Build.MANUFACTURER,
            "brand" to Build.BRAND,
            "device" to Build.DEVICE,
            "product" to Build.PRODUCT,
            "hardware" to Build.HARDWARE,
            "fingerprint" to Build.FINGERPRINT,
            "sdk" to Build.VERSION.SDK_INT,
            "os_version" to Build.VERSION.RELEASE
        )
        FirebaseCrashlytics.getInstance().recordException(
            IllegalStateException("Detected malware: $violationType===${deviceInfo} ")
        )
    }

    private fun initSetOnClickListeners() {
        binding.buttonExit.setOnClickListener { terminateApplication() }
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
        const val OTHER_ISSUE = "other_issue"
    }

}