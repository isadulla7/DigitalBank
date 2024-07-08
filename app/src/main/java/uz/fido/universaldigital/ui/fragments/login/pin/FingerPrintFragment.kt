package uz.fido.universaldigital.ui.fragments.login.pin

import android.content.Intent
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import coil.load
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentFingerPrintBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED
import java.util.concurrent.Executors

class FingerPrintFragment : BaseSimpleFragment<FragmentFingerPrintBinding>(FragmentFingerPrintBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        binding.illustration.load(R.drawable.illustration_finger_print)
    }

    private fun initSetOnClickListeners() {
        binding.turnOn.setOnClickListener {
            fingerAuth()
        }
        binding.skipBtn.setOnClickListener {
            Paper.book().write(Const.FINGER_STATE, false)
            openMainActivity()
        }
    }

    private fun fingerAuth() {
        val executor = Executors.newSingleThreadExecutor()
        val activity = activity
        val biometricPrompt = BiometricPrompt(requireActivity(), executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int, errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    (activity as LoginActivity).runOnUiThread {
                        Paper.book().write(Const.FINGER_STATE, false)
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                (activity as LoginActivity).runOnUiThread {
                    Paper.book().write(Const.FINGER_STATE, true)
                    openMainActivity()
                }
            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.enter_app_with_touch_id)).setNegativeButtonText(getString(R.string.cancel)).build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun openMainActivity() {
        try {
            Paper.book().write(USER_LOGGED, true)
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            requireActivity().finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}