package uz.fido.universaldigital.ui.fragments.login.pin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricPrompt
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import coil.load
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPassCodeBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.dialogs.LogOutDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs.UnableGetProfileDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignInPinResponse
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.DeviceIdentifyState
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.UserIdentifyState
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isFullyIdentified
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isIdentifiedByCard
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isNotIdentified
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isUserIdentifiedButDeviceNot
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.isUserNotIdentifiedButDeviceIdentified
import uz.fido.universaldigital.ui.fragments.login.pin.PinDotsAnimation.zoomInAndOutAnim
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.extensions.pendingTransition
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_LOGGED
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.getDecodedString
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.view.custom_text_view.TextViewMedium
import java.util.Calendar
import java.util.concurrent.Executors

@AndroidEntryPoint
class PassCodeFragment : BaseFragment<FragmentPassCodeBinding, PinCodeViewModel>(FragmentPassCodeBinding::inflate, PinCodeViewModel::class.java), View.OnClickListener {

    companion object {
        const val PASS_OPERATION_POP = "PASS_OPERATION_POP"
        const val DEEP_LINK_OBJECT_VALUE = "DEEP_LINK_OBJECT_NUMBER"
        const val DEEP_LINK_OBJECT_ID = "DEEP_LINK_OBJECT_ID"
        const val DEEP_LINK_AMOUNT = "DEEP_LINK_AMOUNT"
        const val DEEP_LINK_COMMENT = "DEEP_LINK_COMMENT"
        const val NOTIFICATION_OPERATION = "notification_operation"
    }

    private lateinit var signInResponseResource: Resource<SignInResponse>
    private var operation: String = ""
    private var incorrectPinCount = 0
    private var secondPin: String = ""
    private var pin: String = ""
    private var dot1X = 0f
    private var dot2X = 0f
    private var dot3X = 0f
    private var dot4X = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            operation = arguments?.getString(Const.OPERATION, "").orEmpty()
        } catch (e: Exception) {
            recordException(e, ::onCreate.name)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initFingerprint()
        onBackPressCallback()
        initSetOnClickListeners()
        setGreetingText()
        loadProfileImage()
        DiffieHellman.clearDiffieHellman()
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

            R.id.btn_call -> {
                callToBank()
            }

            R.id.btn_logout -> {
                showLogOutDialog()
            }

            else -> {
                vibrateTick(requireContext())
                val textView = view as TextViewMedium
                onNumberClicked(textView.text.toString())
            }
        }
    }

    private fun initPinCodeOperation() {
        if (pin == getDecodedString(requireContext().getFromSecureStore(Const.PAPER_CLIENT_PIN))) {
            if (isInternetConnected(requireContext())) {
                if (operation == PASS_OPERATION_POP) {
                    pop()
                    return
                } else {
                    operationSignIn()
                }
            } else {
                clearDots()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                errorPin()
            }, 50)
        }
    }

    private fun operationSignIn() {
        if (pin == getDecodedString(requireContext().getFromSecureStore(Const.PAPER_CLIENT_PIN))) {
            fillDots()
            swapKeys()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
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
        binding.btnCall.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
    }

    private fun fingerAuth() {
        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                requireActivity().runOnUiThread {
                    fillDots()
                    pin = getDecodedString(requireContext().getFromSecureStore(Const.PAPER_CLIENT_PIN))
                    swapKeys()
                }
            }

        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.enter_app_with_touch_id))
            .setNegativeButtonText(getString(R.string.cancel)).build()
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

    private fun swapKeys() {
        saveToSecureStore(Const.DEVICE_CODE, requireContext().getDeviceIds())
        saveToSecureStore(Const.VERSION_CODE, BuildConfig.VERSION_CODE.toString())
        saveToSecureStore(Const.VERSION_NAME, BuildConfig.VERSION_NAME)
        viewModel.swapKeysPin(
            SwapKeysRequest(
                device_code = requireContext().getDeviceIds(),
                public_key1 = DiffieHellman.getDiffieHellman()._g.toBigInteger(),
                public_key2 = DiffieHellman.getDiffieHellman()._p.toBigInteger(),
                encryptData = DiffieHellman.getDiffieHellman().keyA,
                phoneNumber = requireContext().getFromSecureStore(Const.PAPER_CLIENT_PHONE).replace("", ""),
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data as SwapKeysResponse
                    val diffieHellman = DiffieHellman.getDiffieHellman()
                    diffieHellman.SetKeyB(response.ecnryptData)
                    changeKey(diffieHellman.keyK)
                    getUserInfo()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                    secondPin = ""
                    pin = ""
                    clearDots()
                }
            }
        }
    }

    private fun getUserInfo() {
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + requireContext().getIpAddress())
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> it.data?.let { data ->
                        signInRequest(data)
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                        secondPin = ""
                        pin = ""
                        clearDots()
                    }
                }
            }
    }

    private fun signInRequest(userInfo: UserInfo) {
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val signInRequest = SignInRequestNew(
            phone_number = requireContext().getFromSecureStore(Const.PAPER_CLIENT_PHONE).replace("", ""),
            device_type = "A",
            device_code = requireContext().getDeviceIds(),
            device_name = getDeviceName(),
            version = "1",
            ip = requireContext().getIpAddress(),
            client_id = Keys.getClientId(),
            fcm_token = requireContext().getFromSecureStore(Const.PAPER_FCM_TOKEN),
            password = requireContext().getFromSecureStore(Const.PASSWORD_ENC),
            is_pin = 1,
            sim_iccd = device.simCcd.toString(),
            network_state = device.networkState.toString(),
            imei_data = device.imeiData.toString(),
            os_system_version_api = "A",
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = BuildConfig.VERSION_CODE.toString(),
            app_version = BuildConfig.VERSION_NAME,
            userInfo = userInfo,
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash
        )
        viewModel.signIn(signInRequest = signInRequest).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val signInResponse = it.data
                    signInResponseResource = it
                    if (signInResponse?.token != null) {
                        saveSignInResponse(signInResponse)
                        if (requireActivity() is MainActivity) {
                            pop()
                        } else {
                            PinDotsAnimation.stopPinDotsAnimation()
                            validateUserIdentity()
                        }
                    } else {
                        clearDots()
                        showSnackbar(it.message.toString())
                    }
                }

                Status.ERROR -> {
                    clearDots()
                    PinDotsAnimation.stopPinDotsAnimation()
                    val errorCode = it.errorBody?.code ?: 0
                    if (errorCode == 1204) {
                        showSnackbar(it.message.toString()) {
                            requireActivity().openPlayMarket()
                        }
                    } else {
                        showSnackbar(it.message.toString())
                    }

                    removeUnregisteredDevice(it.message.toString())
                }
            }
        }
    }

    private fun changeKey(keyK: String) {
        try {
            val key1 = requireContext().getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("@$#", 3)
            val key2 = requireContext().getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("&^%", 6)
            if (requireContext().getFromSecureStore(Const.PASSWORD_ENC).isEmpty() || requireContext().getFromSecureStore(Const.STRING_LINE).isEmpty()) {
                requireActivity().logOut()
            } else {
                val newKey = CryptoUtil.encrypt(
                    requireContext().getFromSecureStore(Const.PASSWORD_ENC),
                    key1
                ) + keyK + CryptoUtil.encrypt(
                    requireContext().getFromSecureStore(Const.STRING_LINE),
                    key2
                )
                saveToSecureStore(Const.KEY_K, newKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSignInResponse(signInResponse: SignInResponse) {
        Thread {
            saveSignInPinResponse(signInResponse)
        }.start()
    }

    private fun openMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            addDeepLinkExtras(this)
            addNotificationExtras(this)
        }
        startActivity(intent)
        requireActivity().apply {
            pendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun addDeepLinkExtras(intent: Intent) {
        arguments?.let {
            val deepLinkValue = it.getString(DEEP_LINK_OBJECT_VALUE)
            if (!deepLinkValue.isNullOrEmpty()) {
                intent.putExtra(DEEP_LINK_OBJECT_VALUE, deepLinkValue)
                intent.putExtra(DEEP_LINK_OBJECT_ID, it.getString(DEEP_LINK_OBJECT_ID))
                intent.putExtra(DEEP_LINK_AMOUNT, it.getString(DEEP_LINK_AMOUNT))
                intent.putExtra(DEEP_LINK_COMMENT, it.getString(DEEP_LINK_COMMENT))
            }
        }
    }

    private fun addNotificationExtras(intent: Intent) {
        arguments?.getString(NOTIFICATION_OPERATION)?.takeIf { it.isNotEmpty() }?.let {
            intent.putExtra(NOTIFICATION_OPERATION, "notification")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun removeUnregisteredDevice(message: String) {
        if (message == "Этот номер телефона не зарегистрирован") {
            Handler(Looper.getMainLooper()).postDelayed({
                Paper.book().write(USER_LOGGED, false)
                requireActivity().startActivityWithClearTask(LoginActivity::class.java)
            }, 1000)
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
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded && binding != null) {
                    clearDots()
                    binding.errorText.text = ""
                }
            }, 1000)
        } catch (e: Exception) {
            recordException(e, ::errorPin.name)
        }
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

    @SuppressLint("SetTextI18n")
    private fun setGreetingText() {
        val calendar = Calendar.getInstance()
        val partOfDay = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> getString(R.string.good_morning)
            in 12..16 -> getString(R.string.good_afternoon)
            else -> getString(R.string.good_evening)
        }
        val name = requireContext().getFromSecureStore(Const.FIRST_NAME)
        if (name.isNotEmpty()) {
            binding.welcomeText.text = "$partOfDay, $name"
        } else {
            binding.welcomeText.text = partOfDay
        }
    }

    private fun showLogOutDialog() {
        LogOutDialog {
            requireActivity().logOut()
        }.show(childFragmentManager, "")
    }

    private fun callToBank() {
        val phone = "tel: +998712001110"
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = phone.toUri()
        startActivity(intent)
    }

    private fun loadProfileImage() {
        if (requireContext().getFromSecureStore(Const.PAPER_USER_PHOTO_PATH).isNotEmpty()) {
            Picasso.get()
                .load(requireContext().getFromSecureStore(Const.PAPER_USER_PHOTO_PATH))
                .placeholder(R.drawable.ic_profile_image_empty)
                .error(R.drawable.ic_profile_image_empty)
                .into(binding.userAvatar)
        } else {
            binding.userAvatar.load(R.drawable.ic_profile_image_empty)
        }
    }

    private fun validateUserIdentity() {
        val signInResponse = signInResponseResource.data
        val userDeviceState = signInResponse?.device_myid_state ?: DeviceIdentifyState.DEFAULT
        val userIdentifyState = signInResponse?.user_type_id ?: UserIdentifyState.DEFAULT
        val passportData = signInResponse?.passport_serial + signInResponse?.passport_number
        val dateOfBirth = signInResponse?.birthday
        val pinfl = signInResponse?.pnfl
        when {
            isIdentifiedByCard(userIdentifyState) -> openMyIdPage(passportData, dateOfBirth)

            isFullyIdentified(userIdentifyState, userDeviceState) -> openMainActivity()

            isNotIdentified(userIdentifyState, userDeviceState) -> openMainActivity()

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

    private fun openMyIdPage(passportData: String? = null, dateOfBirth: String? = null, pinfl: String? = null) {
        clearDots()
        val bundle = bundleOf(Const.PASSPORT_DATA to passportData.orEmpty(), Const.DATE_OF_BIRTH to dateOfBirth.orEmpty(), Const.PINFL to pinfl.orEmpty(), Const.IS_PIN to true)
        gotoWithSlide(R.id.mainIdentificationForSignInFragment, bundle)
    }
}