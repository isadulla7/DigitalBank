package uz.fido.universaldigital.ui.fragments.login.pin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPinCodeBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.dialogs.BaseInfoDialog
import uz.fido.universaldigital.ui.fragments.login.pin.PinDotsAnimation.zoomInAndOutAnim
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED
import uz.fido.utils.device.isFingerEnable
import uz.fido.utils.security.getDecodedString
import uz.fido.utils.security.getEncodedString
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.view.custom_text_view.TextViewMedium

@AndroidEntryPoint
class PinCodeFragment : BaseFragment<FragmentPinCodeBinding, PinCodeViewModel>(
    FragmentPinCodeBinding::inflate, PinCodeViewModel::class.java
), View.OnClickListener {

    private var initPinFirstStep = true
    private var incorrectPinCount = 0
    private var secondPin: String = ""
    private var operation: String = ""
    private var pin: String = ""
    private var first = true
    private var oldPassword: String? = null

    companion object {
        const val PIN_OPERATION = "operation_type"
        const val PIN_OPERATION_CHANGE_PIN = "change_pin"
        const val PIN_OPERATION_SIGN_UP = "sign_up"
        const val PIN_OPERATION_SET_PIN = "set_pin_code"
        const val PIN_OPERATION_PAYMENT = "confirm_payment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operation = requireArguments().getString(PIN_OPERATION).toString()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDefaultState()
        initSetOnClickListeners()
    }

    private fun initDefaultState() {
        when (operation) {
            PIN_OPERATION_SET_PIN -> {
                binding.appBar.setTitle(getString(R.string.create_pin_code))
                binding.fingerprint.visibility = View.GONE
            }

            PIN_OPERATION_SIGN_UP -> {
                binding.fingerprint.visibility = View.GONE
                binding.appBar.setTitle(getString(R.string.create_pin_code))
            }

            PIN_OPERATION_CHANGE_PIN -> {
                binding.fingerprint.visibility = View.GONE
                binding.appBar.setTitle(getString(R.string.input_old_pin_code))
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.clear -> {
                onDeleteClicked()
            }

            uz.fido.utils.R.id.back -> {
                pop()
            }

            else -> {
                val textView = view as TextViewMedium
                onNumberClicked(textView.text.toString())
            }
        }
    }

    private fun initPinCodeOperation() {
        if (isInternetConnected(requireContext())) {
            when (operation) {
                PIN_OPERATION_SET_PIN, PIN_OPERATION_SIGN_UP -> {
                    operationSetPin()
                    return
                }

                PIN_OPERATION_CHANGE_PIN -> {
                    operationChangePin()
                    return
                }
            }
        } else {
            clearDots()
        }
    }

    private fun operationSetPin() {
        if (initPinFirstStep) {
            secondPin = pin
            binding.appBar.setTitle(getString(R.string.confirm_pin_code))
            clearDots()
            initPinFirstStep = false
        } else {
            if (secondPin == pin) {
                saveToPaper(Const.PAPER_CLIENT_PIN, getEncodedString(pin))
                if (isFingerEnable(requireContext())) {
                    goto(R.id.fingerPrintFragment)
                } else {
                    openMainActivity()
                }
            } else {
                binding.appBar.setTitle(getString(R.string.create_pin_code))
                initPinFirstStep = true
                errorPin()
            }
        }
    }

    private fun operationChangePin() {
        if (oldPassword == null) {
            oldPassword = pin
            if (oldPassword == getDecodedString(getFromPaper(Const.PAPER_CLIENT_PIN))) {
                binding.appBar.setTitle(getString(R.string.create_pin_code))
                clearDots()
            } else {
                oldPassword = null
                errorPin()
            }
        } else {
            if (first) {
                secondPin = pin
                binding.appBar.setTitle(getString(R.string.repeat_pin))
                clearDots()
                first = false
            } else {
                if (secondPin == pin) {
                    saveToPaper(Const.PAPER_CLIENT_PIN, getEncodedString(pin))
                    showSnackbar(
                        getString(R.string.pin_code_success_changed),
                        getString(R.string.successfully)
                    ) { pop() }
                } else {
                    binding.appBar.setTitle(getString(R.string.create_pin_code))
                    first = true
                    errorPin()
                }
            }
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
        binding.appBar.setOnBackButtonClickListener(this)
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
        if (isVisible) {
            binding.apply {
                dot1.visibility = View.VISIBLE
                dot2.visibility = View.VISIBLE
                dot3.visibility = View.VISIBLE
                dot4.visibility = View.VISIBLE
                clear.alpha = 0.15f
                dot1.setImageResource(R.drawable.pin_dot_disable)
                dot2.setImageResource(R.drawable.pin_dot_disable)
                dot3.setImageResource(R.drawable.pin_dot_disable)
                dot4.setImageResource(R.drawable.pin_dot_disable)
                pin = ""
            }
        }
    }

    private fun openMainActivity() {
        CoroutineScope(Dispatchers.Default).launch { // Main, because UI is changed
            Paper.book().write(Const.FINGER_STATE, false)
            Paper.book().write(USER_LOGGED, true)
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in, android.R.anim.fade_out
            )
            requireActivity().finish()
        }
    }

    private fun errorPin() {
        PinDotsAnimation.stopPinDotsAnimation()
        PinDotsAnimation.errorAnimation(binding.dotView, requireActivity())
        secondPin = ""
        binding.errorText.text = getString(R.string.wrong_pin)
        Handler(Looper.getMainLooper()).postDelayed({
            if (context != null && binding != null) {
                clearDots()
                binding.errorText.text = ""
            }
        }, 2000)
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
        val warningDialog =
            BaseInfoDialog(getString(R.string.warning), getString(R.string.this_is_last_attempt))
        warningDialog.show(childFragmentManager, "")
    }
}