package uz.fido.universaldigital.ui.fragments.login.sign_up_password

import android.os.Build
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpFlagsEnum
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignUpPasswordBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignUpRequest
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.utils.const.APIServiceConst.USER_INFO_URL
import uz.fido.utils.const.Const
import uz.fido.utils.security.encryptPassword
import uz.fido.utils.utility.context.GetDeviceInfo
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName

@AndroidEntryPoint
class SignUpPasswordFragment : BaseFragment<FragmentSignUpPasswordBinding, SignUpPasswordViewModel>(
    FragmentSignUpPasswordBinding::inflate, SignUpPasswordViewModel::class.java
) {

    companion object {
        const val SIGN_UP_PHONE_NUMBER = "phone_number"
        const val SIGN_UP_SMS_CODE = "sms_code"
    }


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initFieldsListener()
    }

    private fun initFieldsListener() {
        binding.etPassword.addTextChangedListener {
            checkForButton()
        }
        binding.etRepeatPassword.addTextChangedListener {
            checkForButton()
        }

    }

    private fun checkForButton() {
        val pass = binding.etPassword.text.toString()
        val repeatedPass = binding.etRepeatPassword.text.toString()
        val isEnable = pass.length > 7 && pass == repeatedPass
        binding.btnContinue.isEnabled(isEnable)
        binding.passDontMatch.isVisible = !isEnable
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            getUserInfo()
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getUserInfo() {
        viewModel.getUserDetailedInfo(USER_INFO_URL + requireContext().getIpAddress())
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> it.data?.let { data ->
                        finishRegistration(data)
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun finishRegistration(data: UserInfo) {
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val finishRegRequest = FinishRegRequest(
            app_version = BuildConfig.VERSION_NAME,
            app_version_code = BuildConfig.VERSION_CODE.toString(),
            device_code = requireContext().getDeviceIds(),
            device_name = getDeviceName(),
            device_type = "A",
            email = "",
            fcm_token = Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "",
            flag = SignUpFlagsEnum.Continue.flag,
            imei_data = device.imei_data.toString(),
            invited_user_id = "",
            ip = requireContext().getIpAddress(),
            name = "",
            network_state = device.network_state.toString(),
            nick_name = "",
            os_system_version_api = device.os_system_version_api.toString(),
            os_version = Build.VERSION.SDK_INT.toString(),
            patronymic = "",
            phone_number = requireArguments().getString(SIGN_UP_PHONE_NUMBER)?.replace("+", "")
                ?.replace(" ", ""),
            sms_code = requireArguments().getString(SIGN_UP_SMS_CODE),
            sim_iccd = device.sim_iccd.toString(),
            version = "0",
            surname = " ",
            password = encryptPassword(binding.etPassword.text.toString()),
            userInfo = data,
            string_line = ""
        )
        viewModel.finishReg(finishRegRequest).observe(viewLifecycleOwner) {
            it.let {
                binding.btnContinue.setProgress(false)
                when (it.status) {
                    Status.SUCCESS -> {
                        val signInResponse = it.data
                        finishRegRequest.fcm_token = signInResponse?.token!!
                        finishRegRequest.client_id = signInResponse.user_id
                        requireContext().saveSignUpRequest(finishRegRequest)
                        Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
                        val bundle = Bundle()
                        bundle.putString(
                            PinCodeFragment.PIN_OPERATION,
                            PinCodeFragment.PIN_OPERATION_SIGN_UP
                        )
                        gotoWithSlide(R.id.pinCodeFragment, bundle)
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }

                }
            }
        }
    }

}