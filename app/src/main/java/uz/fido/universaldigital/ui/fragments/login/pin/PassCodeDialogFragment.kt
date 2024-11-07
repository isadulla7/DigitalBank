package uz.fido.universaldigital.ui.fragments.login.pin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricPrompt
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogPassCodeBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.fragments.login.pin.PinDotsAnimation.zoomInAndOutAnim
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.security.getDecodedString
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.util.concurrent.Executors

@AndroidEntryPoint
class PassCodeDialogFragment(var onSuccessBack: () -> Unit) : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogPassCodeBinding
    private var incorrectPinCount = 0
    private var secondPin: String = ""
    private var pin: String = ""
    private var dot1X = 0f
    private var dot2X = 0f
    private var dot3X = 0f
    private var dot4X = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogPassCodeBinding.inflate(inflater, container, false)
        initFingerprint()
        onBackPressCallback()
        initSetOnClickListeners()
        return binding.root
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.clear -> {
                onDeleteClicked()
            }

            R.id.fingerprint -> {
                if (Paper.book().read<Boolean>(Const.FINGER_STATE) == true) {
                    fingerAuth()
                }
            }

            else -> {
                vibrateTick(requireContext())
                val textView = view as TextViewMedium
                onNumberClicked(textView.text.toString())
            }
        }
    }

    private fun initPinCodeOperation() {
        if (pin == getDecodedString(getFromPaper(Const.PAPER_CLIENT_PIN))) {
            dismiss()
            onSuccessBack.invoke()
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                errorPin()
            }, 50)
        }
    }

    private fun onNumberClicked(n: String) {
        if (pin.length < 4) {
            pin += n
            when (pin.length) {
                1 -> {
                    binding.clear.alpha = 1.0f
                    binding.dot1.setImageResource(R.drawable.pin_dot_enabled)
                    zoomInAndOutAnim(binding.dot1)
                }

                2 -> {
                    binding.dot2.setImageResource(R.drawable.pin_dot_enabled)
                    zoomInAndOutAnim(binding.dot2)
                }

                3 -> {
                    binding.dot3.setImageResource(R.drawable.pin_dot_enabled)
                    zoomInAndOutAnim(binding.dot3)
                }

                4 -> {
                    binding.dot4.setImageResource(R.drawable.pin_dot_enabled)
                    initDefaultDotCoordinates()
                    zoomInAndOutAnim(binding.dot4)
                    initPinCodeOperation()
                }
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.num0.setOnClickListener(this)
        binding.num1.setOnClickListener(this)
        binding.num2.setOnClickListener(this)
        binding.num3.setOnClickListener(this)
        binding.num4.setOnClickListener(this)
        binding.num5.setOnClickListener(this)
        binding.num6.setOnClickListener(this)
        binding.num7.setOnClickListener(this)
        binding.num8.setOnClickListener(this)
        binding.num9.setOnClickListener(this)
        binding.clear.setOnClickListener(this)
        binding.fingerprint.setOnClickListener(this)
    }

    private fun fingerAuth() {
        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    requireActivity().runOnUiThread {
                        fillDots()
                        dismiss()
                        onSuccessBack.invoke()
                    }
                }

            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.enter_app_with_touch_id))
            .setNegativeButtonText(getString(R.string.input_pin_code)).build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun onDeleteClicked() {
        PinDotsAnimation.stopPinDotsAnimation()
        if (pin.isEmpty()) return
        pin = pin.substring(0, pin.length - 1)
        when (pin.length) {
            0 -> {
                binding.dot1.setImageResource(R.drawable.pin_dot_disable)
                binding.clear.alpha = 0.15f
            }

            1 -> {
                binding.dot2.setImageResource(R.drawable.pin_dot_disable)
            }

            2 -> {
                binding.dot3.setImageResource(R.drawable.pin_dot_disable)
            }

            3 -> {
                binding.dot4.setImageResource(R.drawable.pin_dot_disable)
            }
        }
    }

    private fun clearDots() {
        binding.apply {
            progressBar.visibility = View.INVISIBLE
            dot1.visibility = View.VISIBLE
            dot2.visibility = View.VISIBLE
            dot3.visibility = View.VISIBLE
            dot4.visibility = View.VISIBLE
            clear.alpha = 0.15f
            dot1.setImageResource(R.drawable.pin_dot_disable)
            dot2.setImageResource(R.drawable.pin_dot_disable)
            dot3.setImageResource(R.drawable.pin_dot_disable)
            dot4.setImageResource(R.drawable.pin_dot_disable)
            reverseDots()
            pin = ""
        }
    }

    private fun fillDots() {
        binding.apply {
            dot1.setImageResource(R.drawable.pin_dot_success)
            dot2.setImageResource(R.drawable.pin_dot_success)
            dot3.setImageResource(R.drawable.pin_dot_success)
            dot4.setImageResource(R.drawable.pin_dot_success)
            vibrateTick(requireContext())
            gatherAnimation()
            clear.isClickable = false
            clear.alpha = 1f
        }
    }

    private fun gatherAnimation() {
        binding.apply {
            PinDotsAnimation.gatherAnimation(dot1, progressBar)
            PinDotsAnimation.gatherAnimation(dot2, progressBar)
            PinDotsAnimation.gatherAnimation(dot3, progressBar)
            PinDotsAnimation.gatherAnimation(dot4, progressBar)
        }
    }

    private fun initFingerprint() {
        if (Paper.book().read<Boolean>(Const.FINGER_STATE) != null && Paper.book()
                .read<Boolean>(Const.FINGER_STATE) == true
        ) {
            binding.fingerprint.visibility = View.VISIBLE
            fingerAuth()
        } else {
            binding.fingerprint.visibility = View.GONE
        }
    }

    private fun onBackPressCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun errorPin() {
        PinDotsAnimation.stopPinDotsAnimation()
        PinDotsAnimation.errorAnimation(binding.dotView, requireActivity())
        secondPin = ""
        binding.errorText.text = getString(R.string.wrong_pin)
        Handler(Looper.myLooper()!!).postDelayed({
            if (context != null) {
                clearDots()
                binding.errorText.text = ""
            }
        }, 1000)
        setWrongPinCounter()
    }

    private fun setWrongPinCounter() {
        incorrectPinCount++
        if (incorrectPinCount == 2) {
            showWrongPinWarning()
        }
        if (incorrectPinCount == 3) {
            binding.errorText.text = getString(R.string.too_many_attempts)
            Handler(Looper.getMainLooper()).postDelayed({
                Paper.book().write(USER_LOGGED, false)
                requireActivity().startActivityWithClearTask(LoginActivity::class.java)
            }, 1000)
        }
    }

    private fun showWrongPinWarning() {
        showSnackbar(getString(R.string.this_is_last_attempt), getString(R.string.warning))
    }

    private fun initDefaultDotCoordinates() {
        binding.apply {
            dot1X = dot1.x
            dot2X = dot2.x
            dot3X = dot3.x
            dot4X = dot4.x
        }
    }

    private fun reverseDots() {
        binding.apply {
            PinDotsAnimation.reverseDot(dot1, dot1X)
            PinDotsAnimation.reverseDot(dot2, dot2X)
            PinDotsAnimation.reverseDot(dot3, dot3X)
            PinDotsAnimation.reverseDot(dot4, dot4X)
        }
    }

}