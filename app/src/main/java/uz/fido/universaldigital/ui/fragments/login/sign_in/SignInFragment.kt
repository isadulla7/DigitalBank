package uz.fido.universaldigital.ui.fragments.login.sign_in

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignInBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.login.sign_up.SignUpFragment
import uz.fido.universaldigital.ui.utils.extensions.getFCMToken
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.encryptPassword
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>(
    FragmentSignInBinding::inflate, SignInViewModel::class.java
) {
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setMask()
        initSetOnClickListeners()
        setTermsOfUseColor()
        initTextChangeListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setMask() {
        binding.etPhoneNumber.setText("+998")
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
                binding.btnContinue.isEnabled(text.toString().length == 17 && passwordFormatted().length > 7)
                isEditing = false
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun formatPhoneNumber(text: String): String {
        val digits = text.replace(Regex("[^0-9]"), "")
        val builder = StringBuilder("+998 ")
        if (digits.length > 3) {
            builder.append(digits.substring(3, minOf(5, digits.length))) // XX
        }
        if (digits.length > 5) {
            builder.append(" ").append(digits.substring(5, minOf(8, digits.length))) // XXX
        }
        if (digits.length > 8) {
            builder.append(" ").append(digits.substring(8, minOf(10, digits.length))) // XX
        }
        if (digits.length > 10) {
            builder.append(" ").append(digits.substring(10, minOf(12, digits.length))) // XX
        }

        return builder.toString()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.tvGotoSignUp.setOnClickListener { goto(R.id.signUpFragment) }
        binding.tvResetPassword.setOnClickListener { goto(R.id.signUpFragment, bundleOf(SignUpFragment.OPERATION to SignUpFragment.OPERATION_RECOVER_PASSWORD)) }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            swapKeysRequest()
        }
    }

    private fun initTextChangeListeners() {
        binding.etPassword.addTextChangedListener { password ->
            binding.btnContinue.isEnabled(password.toString().length > 7 && phoneNumberFormatted().length == 12)
        }
    }

    private fun setTermsOfUseColor() {
        binding.textSingUpTerms.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
        }
    }

    private fun swapKeysRequest() {
        saveToPaper(Const.DEVICE_CODE, requireContext().getDeviceIds())
        viewModel.swapKeys(
            SwapKeysRequest(
                device_code = requireContext().getDeviceIds(),
                public_key1 = DiffieHellman.getDiffieHellman()._g.toBigInteger(),
                public_key2 = DiffieHellman.getDiffieHellman()._p.toBigInteger(),
                encryptData = DiffieHellman.getDiffieHellman().keyA
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
        val ip: String = requireContext().getIpAddress()
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + ip).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { data ->
                    checkUserSignInRequest(data)
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setKeyBForDiffieHellman(response: SwapKeysResponse) {
        try {
            val additionalText = CryptoUtil.encrypt(requireContext().getDeviceIds(), requireContext().getDeviceIds())
            DiffieHellman.getDiffieHellman().setKeyBSwapKey(response.ecnryptData, additionalText, requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkUserSignInRequest(data: UserInfo) {
        saveToPaper("VERSION_CODE", BuildConfig.VERSION_CODE.toString())
        saveToPaper("VERSION_NAME", BuildConfig.VERSION_NAME)
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val model = SignInRequestNew(
            phone_number = phoneNumberFormatted(),
            password = encryptPassword(passwordFormatted()),
            device_type = "A",
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = BuildConfig.VERSION_CODE.toString(),
            app_version = BuildConfig.VERSION_NAME,
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
            is_pin = 0,
            device_code = requireContext().getDeviceIds(),
            device_name = getDeviceName(),
            userInfo = data,
            fcm_token = getFromPaper(Const.PAPER_FCM_TOKEN),
            version = "0",
            sim_iccd = device.simCcd.toString(),
            os_system_version_api = "A",
            network_state = device.networkState.toString(),
            client_id = Keys.getClientId(),
            ip = requireContext().getIpAddress(),
            imei_data = device.imeiData.toString()
        )
        viewModel.checkUserSignInRequest(model).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getFCMToken()
                        model.string_line = it.data!!.string_line
                        gotoConfirmSmsFragment(model, it.data?.device_myid_state ?: "N")
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

    private fun gotoConfirmSmsFragment(model: SignInRequestNew, deviceMyIdState: String) {
        saveToPaper(Const.PAPER_PAYMENT_VERSION, model.version)
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUMBER, binding.etPhoneNumber.editableText.toString())
            putString(Const.OPERATION, ConfirmSmsFragment.SMS_OPERATION_SIGN_IN)
            putString(Const.DEVICE_MY_ID_STATE, deviceMyIdState)
            putSerializable(ConfirmSmsFragment.SIGN_IN_REQUEST, model)
        }
        saveToPaper(Const.PAPER_CLIENT_PHONE, phoneNumberFormatted())
        gotoWithSlide(R.id.confirmSmsFragmentLogin, bundle)
    }

    private fun phoneNumberFormatted(): String {
        return binding.etPhoneNumber.editableText.toString().replace(" ", "").replace("+", "")
    }

    private fun passwordFormatted(): String {
        return binding.etPassword.editableText.toString().replace(" ", "")
    }

}