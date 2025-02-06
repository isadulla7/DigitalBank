package uz.fido.universaldigital.ui.fragments.login.restore_profile

import android.os.Build
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpFlagsEnum
import uz.fido.network.domain.model.sms.SendEmailCode
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentRestoreWithEmailBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignInResponse
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.EMAIL
import uz.fido.utils.const.Const.PHONE_NUMBER
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.utility.context.GetDeviceInfo
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName

@AndroidEntryPoint
class RestoreWithEmailFragment : BaseFragment<FragmentRestoreWithEmailBinding, RestoreProfileViewModel>(
    FragmentRestoreWithEmailBinding::inflate, RestoreProfileViewModel::class.java
) {

    private var phoneNumber: String? = null
    private var stringLine: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = requireArguments().getString(EMAIL)!!
        phoneNumber = requireArguments().getString(PHONE_NUMBER)!!
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListener()
        initUI()
    }

    private fun initUI() {
        val regex = """^([^@]{2})([^@]+)([^@]{0}@)""".toRegex()
        val emailMask = email!!.replace(regex) {
            it.groupValues[1] + "*".repeat(it.groupValues[2].length) + it.groupValues[3]
        }
        binding.subtitle.text = "${getString(R.string.enter_sms_code_from_mail)} $emailMask"
        binding.etSms.addTextChangedListener {
            binding.btnContinue.isEnabled(binding.etSms.text.toString().length == 8)
        }
        sendEmailCode()
    }

    private fun initSetOnClickListener() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            finishOperation()
        }
    }

    private fun sendEmailCode() {
        if (phoneNumber != null) {
            viewModel.sendEmailCode(SendEmailCode(email = email!!, phone_number = phoneNumber!!, device_id = requireContext().getDeviceIds())).observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data
                        stringLine = response?.string_line.toString()
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun finishOperation() {
        if (binding.etSms.text.toString().isNotBlank()) {
            binding.btnContinue.setProgress(true)
            val device = GetDeviceInfo(requireContext()).deviceInfo
            val model = FinishRegRequest(
                phone_number = phoneNumber!!,
                string_line = CryptoUtil.encrypt(stringLine.toString(), binding.etSms.text.toString().trim()),
                device_type = "A",
                device_code = requireContext().getDeviceIds(),
                device_name = getDeviceName(),
                version = "0",
                ip = requireContext().getIpAddress(),
                fcm_token = getFromPaper(Const.PAPER_FCM_TOKEN),
                sim_iccd = device.sim_iccd.toString(),
                network_state = device.network_state.toString(),
                imei_data = device.imei_data.toString(),
                flag = SignUpFlagsEnum.Email.flag,
                os_version = Build.VERSION.SDK_INT.toString(),
                app_version_code = BuildConfig.VERSION_CODE.toString(),
                app_version = BuildConfig.VERSION_NAME,
                email = email.toString()
            )
            viewModel.finishReg(model).observe(viewLifecycleOwner) {
                it.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            val signInResponse = it.data
                            if (signInResponse!!.msg.isNullOrEmpty()) {
                                signInResponse.phone_number = phoneNumber!!
                                requireContext().saveSignInResponse(signInResponse)
                                saveToPaper(Const.PASSWORD_ENC, signInResponse.password)
                                val bundle = Bundle()
                                bundle.putString(
                                    ChangePasswordFragment.CHANGE_PASSWORD_OPERATION,
                                    ChangePasswordFragment.CHANGE_PASSWORD_SIGNUP
                                )
                                gotoWithSlide(R.id.changePasswordFragment2, bundle)
                            } else {
                                showSnackbar(signInResponse.msg.toString())
                            }
                        }

                        Status.ERROR -> {
                            showSnackbar(it.data?.msg.toString())
                        }
                    }
                }
            }
        }


    }
}