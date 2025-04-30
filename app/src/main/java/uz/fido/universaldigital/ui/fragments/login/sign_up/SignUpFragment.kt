package uz.fido.universaldigital.ui.fragments.login.sign_up

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.interceptor.tryMakeToast
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignUpBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment.Companion.SMS_OPERATION_FORGOT_PASSWORD
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>(
    FragmentSignUpBinding::inflate, SignUpViewModel::class.java
) {

    companion object {
        const val OPERATION = "operation"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setTermsOfUseColor()
        initSetOnClickListeners()
        setPhonePrefix()
        setMask()
        initRecoverPasswordDescription()
    }

    private fun setMask() {
        binding.etPhoneNumber.setText("+998")
        pasteText()
        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            private var lastText = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                lastText = s.toString()
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing || text == null) return
                isEditing = true
                var currentText = text.toString().replace(Regex("[^0-9+]"), "")
                if (!currentText.startsWith("+998")) {
                    currentText = "+998"
                }
                val formattedText = formatPhoneNumber(currentText)
                binding.etPhoneNumber.removeTextChangedListener(this)
                binding.etPhoneNumber.setText(formattedText)
                binding.etPhoneNumber.setSelection(formattedText.length)
                binding.etPhoneNumber.addTextChangedListener(this)
                binding.btnContinue.isEnabled(text.toString().length == 17)
                isEditing = false
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun pasteText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.etPhoneNumber.setOnReceiveContentListener(arrayOf("text/plain")){ _, payload ->
                try {
                    val pastedText = payload.clip.getItemAt(0).text
                    var digitsOnly = pastedText.filter { it.isDigit() }
                    if (digitsOnly.startsWith("998")) {
                        digitsOnly = digitsOnly.removePrefix("998")
                    }
                    val formattedNumber = if (digitsOnly.length >= 9) {
                        "+998 ${digitsOnly.substring(0,2)} ${digitsOnly.substring(2,5)} ${digitsOnly.substring(5,7)} ${digitsOnly.substring(7,9)}"
                    } else {
                        "+998 $digitsOnly"
                    }

                    binding.etPhoneNumber.setText(formattedNumber)
                    binding.etPhoneNumber.setSelection(formattedNumber.length)
                }catch (e:Exception){

                }

                null
            }
        }
    }

    private fun formatPhoneNumber(text: String): String {
        val digits = text.replace(Regex("[^0-9]"), "")
        val builder = StringBuilder("+998 ")
        if (digits.length > 3) {
            builder.append(digits.substring(3, minOf(5, digits.length)))
        }
        if (digits.length > 5) {
            builder.append(" ").append(digits.substring(5, minOf(8, digits.length)))
        }
        if (digits.length > 8) {
            builder.append(" ").append(digits.substring(8, minOf(10, digits.length)))
        }
        if (digits.length > 10) {
            builder.append(" ").append(digits.substring(10, minOf(12, digits.length)))
        }

        return builder.toString()
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            if (isValidPhoneNumber()) {
                if (isInternetConnected(requireContext())) {
                    binding.btnContinue.setProgress(true)
                    saveToSecureStore(Const.PAPER_CLIENT_PHONE, phoneNumberFormatted())
                    swapKeysRequest()
                }
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.tvEmployeeCode.setOnClickListener {
            if (binding.expandableLayout.isExpanded) {
                binding.expandableLayout.collapse(true)
                binding.tvEmployeeCode.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down_ios), null)
            } else {
                binding.expandableLayout.expand(true)
                binding.tvEmployeeCode.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(), R.drawable.arrow_up_24dp), null)
            }
        }
    }

    private fun setPhonePrefix() {
        binding.etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etPhoneNumber.text.toString().isEmpty()) binding.etPhoneNumber.setText(getString(R.string.phone_number_prefix))
        }
        binding.etPhoneNumber.setOnKeyListener { _, _, event ->
            event.keyCode == KeyEvent.KEYCODE_DEL && binding.etPhoneNumber.text.toString().length == 4
        }
    }

    private fun swapKeysRequest() {
        saveToSecureStore(Const.VERSION_CODE, BuildConfig.VERSION_CODE.toString())
        saveToSecureStore(Const.VERSION_NAME, BuildConfig.VERSION_NAME)
        saveToSecureStore(Const.DEVICE_CODE, requireContext().getDeviceIds())
        viewModel.swapKeys(
            SwapKeysRequest(
                device_code = requireContext().getDeviceIds(),
                public_key1 = DiffieHellman.getDiffieHellman()._g.toBigInteger(),
                public_key2 = DiffieHellman.getDiffieHellman()._p.toBigInteger(),
                encryptData = DiffieHellman.getDiffieHellman().keyA,
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    setKeyBForDiffieHellman(it.data as SwapKeysResponse)
                    getUserInfo()
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getUserInfo() {
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + requireContext().getIpAddress()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { data ->
                    signUpRequest(data)
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun signUpRequest(userInfo: UserInfo) {
        val model = SignUpCheckRequest(
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
            phone_number = phoneNumberFormatted(),
            device_code = requireContext().getDeviceIds(),
            userInfo = userInfo,
            device_id = requireContext().getDeviceIds(),
            app_version_code = BuildConfig.VERSION_CODE.toString(),
        )
        viewModel.checkSignUpRequest(model).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        gotoSmsConfirmFragment(it, model)
                    }

                    Status.ERROR -> {
                        binding.btnContinue.setProgress(false)
                        val errorCode = it.errorBody?.code ?: 0
                        if (errorCode == 1204) {
                            showSnackbar(it.message.toString()) {
                                requireActivity().openPlayMarket()
                            }
                        } else {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun gotoSmsConfirmFragment(
        response: Resource<BaseResponse>, model: SignUpCheckRequest
    ) {
        val bundle = Bundle()
        val operation = arguments?.getString(OPERATION) ?: ConfirmSmsFragment.SMS_OPERATION_SIGN_UP
        bundle.putString(Const.OPERATION, operation)
        bundle.putString(Const.PHONE_NUMBER, binding.etPhoneNumber.editableText.toString())
        bundle.putSerializable("data", model)
        bundle.putString(Const.RANDOM_TEXT, response.data?.string_line.toString())
        bundle.putString(Const.REF_CODE, binding.etRefCode.editableText.toString())
        gotoWithSlide(R.id.confirmSmsFragmentLogin, bundle)
    }

    private fun setKeyBForDiffieHellman(response: SwapKeysResponse) {
        try {
            val additionalText = CryptoUtil.encrypt(requireContext().getDeviceIds(), requireContext().getDeviceIds())
            DiffieHellman.getDiffieHellman().setKeyBSwapKey(response.ecnryptData, additionalText, requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isValidPhoneNumber(): Boolean {
        val phone = phoneNumberFormatted()
        return phone.length == 12 && phone.startsWith("998")
    }

    private fun phoneNumberFormatted(): String {
        return binding.etPhoneNumber.editableText.toString().replace(" ", "").replace("+", "")
    }

    private fun setTermsOfUseColor() {
        binding.textSingUpTerms.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setLinkTextColor(
                ContextCompat.getColor(requireContext(), R.color.brandRedColor)
            )
        }
    }

    private fun initRecoverPasswordDescription() {
        arguments?.let {
            if (it.getString(OPERATION) == SMS_OPERATION_FORGOT_PASSWORD) {
                binding.appBar.apply {
                    setTitle(getString(R.string.reset_password))
                    setSubtitle(getString(R.string.reset_password_description))
                }
                binding.tvEmployeeCode.visibility = View.GONE
                binding.expandableLayout.visibility = View.GONE
            }
        }
    }
}