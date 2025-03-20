package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.cards.AddCardRequest
import uz.fido.network.domain.model.cards.ResetPinCount
import uz.fido.network.domain.model.home.GlSMSActivateRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.sign_up.CheckUserSms
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.network.domain.model.sign_up.SignUpFlagsEnum
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmSmsBinding
import uz.fido.universaldigital.services.SMSBroadcastReceiver
import uz.fido.universaldigital.ui.activities.security.FaceIdActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs.UnableGetProfileDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs.YouDontHaveAccountDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs.YouHaveAccountDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignInResponse
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveUserSms
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.DeviceIdentifyState
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.UserIdentifyState
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isFullyIdentified
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isIdentifiedByCard
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isNotIdentified
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isUserIdentifiedButDeviceNot
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isUserNotIdentifiedButDeviceIdentified
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.login.restore_profile.ChangePasswordFragment
import uz.fido.universaldigital.ui.fragments.login.sign_in.SignInViewModel
import uz.fido.universaldigital.ui.fragments.login.sign_up.SignUpViewModel
import uz.fido.universaldigital.ui.fragments.login.sign_up_password.SignUpPasswordFragment
import uz.fido.universaldigital.ui.fragments.profile.security.MyDevicesFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.getFCMToken
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.APIServiceConst.profileImageUrl
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.PHONE_NUMBER
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.encryptPassword
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.bundle.serializable
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithPopupSlide
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.user.getFormattedClientPhone
import java.text.DecimalFormat
import java.text.NumberFormat

@AndroidEntryPoint
@SuppressLint("UnspecifiedRegisterReceiverFlag")
class ConfirmSmsFragment : BaseFragment<FragmentConfirmSmsBinding, ConfirmSmsViewModel>(
    FragmentConfirmSmsBinding::inflate, ConfirmSmsViewModel::class.java
) {

    private lateinit var checkSmsCodeResponse: Resource<SignInResponse>
    private lateinit var checkSmsResponse: SignInResponse
    private lateinit var countDownTimer: CountDownTimer

    private val signInViewModel: SignInViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    private var operation: String = ""
    private var smsCode = ""
    private var stringLine = ""

    companion object {
        const val SMS_OPERATION_CONNECT_SMS_INFO = "connect_sms_notification"
        const val SMS_OPERATION_TERMINATE_SESSION = "terminate_session"
        const val SMS_OPERATION_FORGOT_PASSWORD = "forgot_password"
        const val SMS_DEPOSIT_OPERATION = "sms_deposit_operation"
        const val SMS_OPERATION_PAYMENT_KEY = "payment_key"
        const val SMS_OPERATION_SIGN_UP = "sign_up"
        const val SMS_OPERATION_SIGN_IN = "sign_in"
        const val ADD_CARD = "add_card"
        const val STRING_LINE = "string_line"
        const val SMS_AMOUNT = "amount"
        const val SMS_SERVICE_ID = "service_id"
        const val SMS_FROM_OBJECT_VALUE = "from_object_value"
        const val SMS_MAX_LENGTH = "SMS_MAX_LENGTH"
        const val SMS_RESET_PIN = "sms_reset_pin"
        const val SIGN_IN_REQUEST = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            operation = requireArguments().getString(Const.OPERATION).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        startResendTimer()
        setSubTitle()
        registerSMSReceiver()
        initTextChangeListener()
        initSetOnClickListeners()
        requestPaymentSms()
    }

    private fun requestPaymentSms() {
        if (operation == SMS_OPERATION_PAYMENT_KEY || operation == SMS_DEPOSIT_OPERATION) {
            checkForSmsPaymentRequest()
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            continueButtonClickEvent()
        }
        binding.resendButton.setOnClickListener {
            resendButtonClickEvent()
        }
    }

    private fun initTextChangeListener() {
        val smsLength = if (arguments != null) {
            arguments?.getInt(SMS_MAX_LENGTH) ?: 8
        } else 8
        val newSmsLength = if (smsLength == 0) 8 else smsLength
        binding.etSms.addTextChangedListener {
            binding.btnContinue.isEnabled(it.toString().length == newSmsLength)
        }
    }

    private fun continueButtonClickEvent() {
        when (operation) {

            SMS_OPERATION_SIGN_UP -> {
                checkRegUser()
            }

            SMS_OPERATION_SIGN_IN -> {
                getUserInfo()
            }

            SMS_OPERATION_CONNECT_SMS_INFO -> {
                humoSmsActivate()
            }

            SMS_DEPOSIT_OPERATION, SMS_OPERATION_PAYMENT_KEY -> {
                smsCode = binding.etSms.text.toString().replace(" ", "")
                val encryptedStringLine = CryptoUtil.encryptWithoutSalt(
                    stringLine, smsCode
                )
                setFragmentResult(
                    SMS_OPERATION_PAYMENT_KEY, bundleOf("sms_code" to smsCode, "string_line" to encryptedStringLine)
                )
                findNavController().navigateUp()
            }

            ADD_CARD -> {
                addCard()
            }

            SMS_OPERATION_TERMINATE_SESSION -> {
                val userDevice = requireArguments().serializable<UserDevices>("user_device") as UserDevices
                terminateSessionRequest(userDevice, requireArguments().getString("type").toString())
            }

            SMS_RESET_PIN -> {
                checkResetPin()
            }
        }
    }

    private fun checkResetPin() {
        binding.btnContinue.setProgress(true)
        val smsCode = binding.etSms.editableText.toString()
        val objectValue = requireArguments().getString(Const.CARD_NUMBER).toString()
        val objectExp = requireArguments().getString("object_data").toString()
        val stringLine = requireArguments().getString(STRING_LINE).toString()
        val stringLineEnc = CryptoUtil.encryptWithoutSalt(
            stringLine, smsCode
        )
        val item = ResetPinCount(
            "card", objectValue, objectExp, null, getClientPhoneNumber(), stringLineEnc
        )
        viewModel.resetPinCount(getClientToken(), item).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    saveToPaper(Const.STRING_LINE, stringLineEnc)
                    binding.btnContinue.setProgress(false)
                    val bundle = Bundle().apply {
                        this.putString(Const.OPERATION, BasicSuccessFragment.HUMO_ACTIVATION)
                    }
                    goto(R.id.basicSuccessFragment, bundle)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun terminateSessionRequest(item: UserDevices, terminateType: String) {
        smsCode = binding.etSms.text.toString().replace(" ", "")
        val stringLine = requireArguments().getString(STRING_LINE).toString()
        val stringLineEnc = CryptoUtil.encryptWithoutSalt(
            stringLine, smsCode
        )
        showProgress()
        viewModel.terminateSession(
            getClientToken(), DeleteUserDeviceRequest(
                selected_device_code = if (terminateType == MyDevicesFragment.OPERATION_DELETE_ALL) null else item.device_code,
                current_device_code = requireActivity().getDeviceIds(),
                del_req_type = terminateType,
                user_id = getClientId(),
                string_line = stringLineEnc
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    saveToPaper(Const.STRING_LINE, stringLineEnc)
                    hideProgress()
                    pop()
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun checkForSmsPaymentRequest() {
        showProgress()
        val model = CheckSmsForPayment(
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
            from_object_id = requireArguments().getString(SMS_FROM_OBJECT_VALUE).toString(),
            amount = requireArguments().getString(SMS_AMOUNT).toString(),
            service_id = requireArguments().getString(SMS_SERVICE_ID).toString(),
            device_code = requireContext().getDeviceIds()
        )
        viewModel.checkForSmsPaymentRequest(getClientToken(), model).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    stringLine = it.data?.string_line.toString()
                    if (it.data?.msg == "100") {
                        setFragmentResult(SMS_OPERATION_PAYMENT_KEY, bundleOf("sms_code" to smsCode, "string_line" to stringLine))
                        findNavController().navigateUp()
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getUserInfo() {
        binding.btnContinue.setProgress(true)
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + requireContext().getIpAddress()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { data ->
                    signInRequest(data)
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun signInRequest(userInfo: UserInfo) {
        if (context != null && !isDetached) {
            val smsCode = binding.etSms.editableText.toString()
            val data = requireArguments().serializable<SignInRequestNew>("data") as SignInRequestNew
            val device = GetDeviceInfo(requireContext()).deviceInfo
            val stringLineEnc = CryptoUtil.encryptWithoutSalt(data.string_line.toString().replace(" ", ""), smsCode)
            val signInRequest = CheckUserSms(
                phone_number = data.phone_number.replace("+", ""),
                string_line = stringLineEnc,
                fcm_token = getFromPaper(Const.PAPER_FCM_TOKEN),
                device_code = requireContext().getDeviceIds(),
                device_name = getDeviceName(),
                device_type = "A",
                os_version = Build.VERSION.SDK_INT.toString(),
                app_version_code = BuildConfig.VERSION_CODE.toString(),
                app_version = BuildConfig.VERSION_NAME,
                ip = requireContext().getIpAddress(),
                sim_iccd = device.simCcd,
                network_state = device.networkState,
                imei_data = device.imeiData,
                sms_type = 3,
                is_pin = 1,
                version = "0",
                password = null,
                os_system_version_api = "A",
                userInfo = userInfo
            )
            viewModel.checkUserSms(signInRequest).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.btnContinue.setProgress(false)
                            checkSmsCodeResponse = it
                            saveLoginInfo()
                            validateUserIdentity()
                        }

                        Status.ERROR -> {
                            binding.btnContinue.setProgress(false)
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun changeKey() {
        val key1 = getFromPaper(Const.PAPER_CLIENT_PHONE).insertStringBetween("@$#", 3)
        val key2 = getFromPaper(Const.PAPER_CLIENT_PHONE).insertStringBetween("&^%", 6)
        val newKey = CryptoUtil.encrypt(getFromPaper(Const.PASSWORD_ENC), key1) + getFromPaper(Const.KEY_K) + CryptoUtil.encrypt(getFromPaper(Const.STRING_LINE), key2)
        saveToPaper(Const.KEY_K, newKey)
    }

    private fun checkRegUser() {
        val smsCode = binding.etSms.editableText.toString()
        val smsType = if (operation == SMS_OPERATION_SIGN_UP || operation == SMS_OPERATION_FORGOT_PASSWORD) 1 else 5
        if (binding.etSms.text.toString().isNotEmpty()) {
            val stringLineEnc = CryptoUtil.encryptWithoutSalt(requireArguments().getString("random_text") ?: "", smsCode)
            binding.btnContinue.setProgress(true)
            val phoneNumber = requireArguments().getString("phone_number")!!.replace("+", "").replace(" ", "")
            val model = CheckUserSms(
                phone_number = phoneNumber,
                string_line = stringLineEnc,
                device_id = requireContext().getDeviceIds(),
                sms_type = smsType,
                device_code = requireContext().getDeviceIds()
            )
            viewModel.checkUserSms(model).observe(viewLifecycleOwner) {
                it.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            saveToPaper(Const.STRING_LINE, stringLineEnc)
                            if (it.data != null) {
                                if (operation == SMS_OPERATION_SIGN_UP) {
                                    requireContext().saveUserSms(smsCode)
                                }
                                checkSmsResponse = it.data!!
                                if (checkSmsResponse.user_exist == "Y" && checkSmsResponse.is_authenticate == "Y") {
                                    initDialog()
                                } else {
                                    continueSignUpOperation()
                                }
                            } else {
                                binding.btnContinue.performClick()
                            }
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun humoSmsActivate() {
        binding.btnContinue.setProgress(true)
        val smsCode = binding.etSms.editableText.toString().replace(" ", "")
        val objectValue = requireArguments().getString(Const.CARD_NUMBER).toString()
        val stringLine = requireArguments().getString(STRING_LINE).toString()
        val stringLineEnc = CryptoUtil.encryptWithoutSalt(stringLine, smsCode)
        viewModel.glSMSActivate(
            getClientToken(), GlSMSActivateRequest(
                object_value = objectValue,
                string_line = stringLineEnc
            )
        ).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    saveToPaper(Const.STRING_LINE, stringLineEnc)
                    binding.btnContinue.setProgress(false)
                    val bundle = Bundle().apply {
                        this.putString(Const.OPERATION, BasicSuccessFragment.HUMO_ACTIVATION)
                    }
                    goto(R.id.basicSuccessFragment, bundle)
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun addCard() {
        if (binding.etSms.editableText.toString() != "") {
            val smsCode = binding.etSms.editableText.toString().replace(" ", "")
            val stringLine = requireArguments().getString(STRING_LINE).toString()
            val stringLineEnc = CryptoUtil.encryptWithoutSalt(
                stringLine, smsCode
            )
            binding.btnContinue.setProgress(true)
            val data = requireArguments().serializable<AddCardRequest>(SIGN_IN_REQUEST) as AddCardRequest
            viewModel.addCard(
                getClientToken(), AddCardRequest(
                    object_value = data.object_value,
                    object_expiry = data.object_expiry,
                    phone_number = getFromPaper(Const.PAPER_CLIENT_PHONE),
                    object_name = data.object_name,
                    sms_code = binding.etSms.editableText.toString(),
                    string_line = stringLineEnc,
                    is_main = data.is_main,
                    bg_icon_name = data.bg_icon_name,
                    otp_id = data.otp_id
                )
            ).observe(viewLifecycleOwner) {
                it?.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            val bundle = Bundle()
                            bundle.putString(Const.OPERATION, ADD_CARD)
                            if (arguments?.getString(Const.ADD_CARD_OPERATION) != null) {
                                bundle.putString(
                                    Const.ADD_CARD_OPERATION, arguments?.getString(Const.ADD_CARD_OPERATION, "")
                                )
                            }
                            gotoWithSlide(R.id.basicSuccessFragment, bundle)
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun initDialog() {
        if (operation == SMS_OPERATION_SIGN_UP) {
            if (checkSmsResponse.is_authenticate == "Y") {
                YouHaveAccountDialog(openMyId = {
                    openMyIdActivity()
                }, continueSignUp = {
                    continueSignUpOperation()
                }).show(childFragmentManager, "")
            }
        } else if (operation == SMS_OPERATION_FORGOT_PASSWORD) {
            if (checkSmsResponse.is_authenticate == "Y") {
                openMyIdActivity()
            } else {
                YouDontHaveAccountDialog(cancelOperation = {
                    pop()
                }, continueSignUp = {
                    continueSignUpOperation()
                }).show(childFragmentManager, "")
            }
        }
    }

    private fun openMyIdActivity() {
        val intent = Intent(requireActivity(), FaceIdActivity::class.java)
        intent.putExtra("mode", "strong")
        intent.putExtra(FaceIdActivity.CLIENT_PASSPORT, checkSmsResponse.passport_serial + checkSmsResponse.passport_number)
        intent.putExtra(FaceIdActivity.CLIENT_DATE_OF_BIRTH, checkSmsResponse.birthday)
        faceIdActivityResult.launch(intent)
    }

    private fun finishOperation() {
        binding.btnContinue.setProgress(true)
        val phoneNumber = requireArguments().getString("phone_number").toString().replace(" ", "").replace("+", "")
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val model = FinishRegRequest(
            phone_number = phoneNumber,
            sms_code = "",
            device_type = "A",
            device_code = requireContext().getDeviceIds(),
            device_name = getDeviceName(),
            version = "0",
            ip = requireContext().getIpAddress(),
            fcm_token = getFromPaper(Const.PAPER_FCM_TOKEN),
            sim_iccd = device.simCcd,
            network_state = device.networkState,
            imei_data = device.imeiData,
            flag = SignUpFlagsEnum.Authenticate.flag,
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = BuildConfig.VERSION_CODE.toString(),
            app_version = BuildConfig.VERSION_NAME
        )
        viewModel.finishReg(model).observe(viewLifecycleOwner) {
            it.let {
                binding.btnContinue.setProgress(false)
                when (it.status) {
                    Status.SUCCESS -> {
                        val signInResponse = it.data!!
                        requireContext().saveSignInResponse(signInResponse)
                        saveToPaper(Const.PASSWORD_ENC, signInResponse.password)
                        gotoWithSlide(
                            R.id.changePasswordFragment2, bundleOf(ChangePasswordFragment.CHANGE_PASSWORD_OPERATION to ChangePasswordFragment.CHANGE_PASSWORD_SIGNUP)
                        )
                    }

                    Status.ERROR -> {}
                }
            }
        }
    }

    private fun continueSignUpOperation() {
        val phoneNumber = requireArguments().getString(PHONE_NUMBER)
        smsCode = binding.etSms.editableText.toString().replace(" ", "")
        if (operation == SMS_OPERATION_SIGN_UP || operation == SMS_OPERATION_FORGOT_PASSWORD) {
            gotoWithSlide(
                R.id.signUpPasswordFragment, bundleOf(
                    SignUpPasswordFragment.SIGN_UP_PHONE_NUMBER to phoneNumber,
                    SignUpPasswordFragment.SIGN_UP_SMS_CODE to smsCode,
                    SignUpPasswordFragment.SIGN_UP_REF_CODE to requireArguments().getString(Const.REF_CODE),
                )
            )
        } else {
            gotoWithSlide(
                R.id.changePasswordFragment, bundleOf(
                    ChangePasswordFragment.CHANGE_PASSWORD_OPERATION to ChangePasswordFragment.CHANGE_PASSWORD_FORGOT,
                    ChangePasswordFragment.PHONE_NUMBER to phoneNumber,
                    ChangePasswordFragment.SMS_CODE to smsCode
                )
            )
        }
    }

    private fun setSubTitle() {
        binding.appBar.setSubtitle(getString(R.string.sent_to_phone, getFormattedClientPhone()))
    }

    private fun startResendTimer() {
        countDownTimer = object : CountDownTimer(60000L, 1000) {
            override fun onFinish() {
                binding.tvResendAfter.visibility = View.GONE
                binding.resendButton.visibility = View.VISIBLE
                countDownTimer.cancel()
            }

            override fun onTick(p0: Long) {
                updateResendTime(p0)
            }
        }
        countDownTimer.start()
    }

    private fun updateResendTime(timeInMilliSeconds: Long) {
        if (context != null) {
            val minute = (timeInMilliSeconds / 1000) / 60
            val seconds = (timeInMilliSeconds / 1000) % 60
            val f: NumberFormat = DecimalFormat("00")
            binding.tvResendAfter.text = getString(R.string.left_time, "${f.format(minute)}:${f.format(seconds)}")
        }
    }

    private fun resendButtonClickEvent() {
        binding.resendButton.visibility = View.GONE
        binding.tvResendAfter.visibility = View.VISIBLE
        startResendTimer()
        when (operation) {
            SMS_OPERATION_SIGN_IN -> {
                resendSignInSms()
            }

            SMS_OPERATION_SIGN_UP -> {
                resendSignUpSms()
            }
        }
    }

    private fun registerSMSReceiver() {
        try {
            val task: Task<Void> = SmsRetriever.getClient(activity ?: return).startSmsRetriever()
            task.addOnSuccessListener {
                activity?.let { a ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        a.registerReceiver(
                            smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_EXPORTED
                        )
                    } else {
                        a.registerReceiver(
                            smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                        )
                    }
                }
            }
            task.addOnFailureListener {}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resendSignInSms() {
        val model = requireArguments().serializable<SignInRequestNew>("data") as SignInRequestNew
        signInViewModel.checkUserSignInRequest(model).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getFCMToken()
                        model.string_line = it.data?.string_line.orEmpty()
                    }

                    Status.ERROR -> {}
                }
            }
        }
    }

    private fun resendSignUpSms() {
        val model = requireArguments().serializable<SignUpCheckRequest>("data") as SignUpCheckRequest
        signUpViewModel.checkSignUpRequest(model).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {}
                    Status.ERROR -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        try {
            countDownTimer.cancel()
            requireActivity().unregisterReceiver(smsBroadcastReceiver)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        super.onDestroyView()
    }

    private val smsBroadcastReceiver: SMSBroadcastReceiver = object : SMSBroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val otpCode = intent.getStringExtra("otp")
                if (!otpCode.isNullOrEmpty()) {
                    binding.etSms.setText(otpCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val faceIdActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            finishOperation()
        }
    }

    private fun saveLoginInfo() {
        val data = requireArguments().serializable<SignInRequestNew>("data") as SignInRequestNew
        val smsCode = binding.etSms.editableText.toString()
        val stringLineEnc = CryptoUtil.encryptWithoutSalt(data.string_line.toString().replace(" ", ""), smsCode)
        val signInResponse = checkSmsCodeResponse.data
        if (signInResponse?.token != null) {
            saveToPaper(Const.STRING_LINE, stringLineEnc)
            saveToPaper(Const.PASSWORD_ENC, data.password)
            saveToPaper(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
            signInResponse.password = encryptPassword(data.password)
            requireContext().saveSignInResponse(signInResponse)
            requireContext().saveUserSms(smsCode)
            changeKey()
        } else {
            showSnackbar(checkSmsCodeResponse.message.toString())
        }
    }

    private fun validateUserIdentity() {
        val checkSmsCodeData = checkSmsCodeResponse.data
        val userDeviceState = requireArguments().getString(Const.DEVICE_MY_ID_STATE) ?: DeviceIdentifyState.DEFAULT
        val userIdentifyState = checkSmsCodeData?.user_type_id ?: UserIdentifyState.DEFAULT
        val passportData = checkSmsCodeData?.passport_serial + checkSmsCodeData?.passport_number
        val dateOfBirth = checkSmsCodeData?.birthday
        val pinfl = checkSmsCodeData?.pnfl
        when {
            isIdentifiedByCard(userIdentifyState) -> openMyIdPage(passportData, dateOfBirth)

            isFullyIdentified(userIdentifyState, userDeviceState) -> gotoPinCodeFragment()

            isNotIdentified(userIdentifyState, userDeviceState) -> gotoPinCodeFragment()

            isUserNotIdentifiedButDeviceIdentified(userIdentifyState, userDeviceState) -> openMyIdPage()

            isUserIdentifiedButDeviceNot(userIdentifyState, userDeviceState) -> handleUserNoPassportData(passportData, dateOfBirth, pinfl)
        }
    }

    private fun handleUserNoPassportData(passportData: String?, dateOfBirth: String?, pinfl: String?) {
        if (isValidUserData(passportData, pinfl, dateOfBirth)) {
            openMyIdPage(passportData, dateOfBirth, pinfl)
        } else {
            showUnableGetProfileDialog()
        }
    }

    private fun isValidUserData(passportData: String?, pinfl: String?, dateOfBirth: String?): Boolean {
        return (!passportData.isNullOrBlank() || !pinfl.isNullOrBlank()) && !dateOfBirth.isNullOrBlank()
    }

    private fun showUnableGetProfileDialog() {
        UnableGetProfileDialog {}.show(childFragmentManager, "")
    }

    private fun gotoPinCodeFragment() {
        val bundle = bundleOf(PinCodeFragment.PIN_OPERATION to PinCodeFragment.PIN_OPERATION_SET_PIN)
        gotoWithPopupSlide(R.id.action_confirmSmsFragment_to_pinCodeFragment, R.id.signInFragment, bundle)
    }

    private fun openMyIdPage(passportData: String? = null, dateOfBirth: String? = null, pinfl: String? = null) {
        val bundle = bundleOf(Const.PASSPORT_DATA to passportData.orEmpty(), Const.DATE_OF_BIRTH to dateOfBirth.orEmpty(), Const.PINFL to pinfl.orEmpty(), Const.IS_PIN to false)
        gotoWithPopupSlide(R.id.action_confirmSmsFragmentLogin_to_mainIdentificationForSignInFragment, R.id.signInFragment, bundle)
    }

}