package uz.fido.universaldigital.ui.fragments.profile.security

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSecurityBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.login.restore_profile.ChangePasswordFragment
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.util.concurrent.Executors

@AndroidEntryPoint
class SecurityFragment : BaseFragment<FragmentSecurityBinding, MenuProfileViewModel>(
    FragmentSecurityBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initDefaultSwitchStates()
        initFingerPrintAuthSwitcher()
        initPaymentConfirmSwitcher()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.trustedDevices.setOnClickListener { gotoWithSlide(R.id.myDevicesFragment) }
        binding.changePassword.setOnClickListener {
            gotoWithSlide(
                R.id.changePasswordFragment,
                bundleOf(ChangePasswordFragment.CHANGE_PASSWORD_OPERATION to ChangePasswordFragment.CHANGE_PASSWORD)
            )
        }
        binding.changePin.setOnClickListener {
            gotoWithSlide(
                R.id.pinCodeFragment2,
                bundleOf(PinCodeFragment.PIN_OPERATION to PinCodeFragment.PIN_OPERATION_CHANGE_PIN)
            )
        }
    }

    private fun initDefaultSwitchStates() {
        binding.switchFingerprint.isChecked = Paper.book().read(Const.FINGER_STATE, false)
        binding.confirmPaymentSwitch.isChecked =
            Paper.book().read(Const.PAPER_PAYMENT_PIN_CONFIRMATION, false)
        binding.confirmPaymentByPin.isVisible = hasBiometrics()
        binding.fingerprint.isVisible = hasBiometrics()
    }

    private fun initFingerPrintAuthSwitcher() {
        binding.switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasBiometrics()) {
                    fingerPrintAuth()
                }
            } else {
                binding.switchFingerprint.isChecked = false
                Paper.book().write(Const.FINGER_STATE, false)
            }
        }
    }

    private fun initPaymentConfirmSwitcher() {
        binding.confirmPaymentSwitch.setOnCheckedChangeListener { _, isChecked ->
            Paper.book().write(Const.PAPER_PAYMENT_PIN_CONFIRMATION, isChecked)
        }
    }

    private fun fingerPrintAuth() {
        fingerPrintDialogBuilder(getBiometricPrompt())
    }

    private fun getBiometricPrompt(): BiometricPrompt {
        return BiometricPrompt(
            requireActivity(),
            Executors.newSingleThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        (activity as MainActivity).runOnUiThread {
                            setFingerPrintState(false)
                        }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    (activity as MainActivity).runOnUiThread {
                        setFingerPrintState(true)
                    }
                }
            })
    }

    private fun fingerPrintDialogBuilder(biometricPrompt: BiometricPrompt) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.enter_app_with_touch_id))
            .setNegativeButtonText(getString(R.string.cancel)).build()
        biometricPrompt.authenticate(promptInfo)
    }

    internal fun setFingerPrintState(state: Boolean) {
        Paper.book().write(Const.FINGER_STATE, state)
        binding.switchFingerprint.isChecked = state
    }

    private fun hasBiometrics(): Boolean {
        return when (BiometricManager.from(requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                false
            }

            else -> {
                true
            }
        }
    }

}