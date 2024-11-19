package uz.fido.universaldigital.ui.fragments.login.sign_up_password

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpFlagsEnum
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignUpPasswordBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.utils.extensions.containsNumber
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.hasLetter
import uz.fido.universaldigital.ui.utils.extensions.hasSpecialSymbol
import uz.fido.universaldigital.ui.utils.extensions.removeSpace
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.fido.utils.security.encryptPassword
import uz.fido.utils.utility.context.GetDeviceInfo
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.language.Utility.passwordIsValid
import uz.fido.utils.view.custom_text_view.TextViewRegular

@AndroidEntryPoint
class SignUpPasswordFragment : BaseFragment<FragmentSignUpPasswordBinding, SignUpPasswordViewModel>(
    FragmentSignUpPasswordBinding::inflate, SignUpPasswordViewModel::class.java
) {

    companion object {
        const val SIGN_UP_PHONE_NUMBER = "phone_number"
        const val SIGN_UP_SMS_CODE = "sms_code"
        const val SIGN_UP_REF_CODE = "ref_code"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initFieldsListener()
    }

    private fun initFieldsListener() {
        val removeFilter = InputFilter { s, _, _, _, _, _ -> s.toString().removeSpace() }
        binding.etPassword.apply { filters = filters.plus(removeFilter) }
        binding.etPassword.doAfterTextChanged {
            binding.repeatPasswordLayout.isVisible = passwordIsValid(it.toString())
            checkPassword(it.toString())
            checkForButton()
        }
        binding.etRepeatPassword.doAfterTextChanged {
            val pass = binding.etPassword.text.toString()
            val repeatedPass = binding.etRepeatPassword.text.toString()
            binding.passDontMatch.isVisible = pass != repeatedPass
            checkForButton()
        }
    }

    private fun checkForButton() {
        val pass = binding.etPassword.text.toString()
        val repeatedPass = binding.etRepeatPassword.text.toString()
        val isEnable = passwordIsValid(pass) && pass == repeatedPass
        binding.btnContinue.isEnabled(isEnable)
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            if (passwordIsValid(binding.etPassword.text.toString())) {
                binding.btnContinue.setProgress(true)
                getUserInfo()
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getUserInfo() {
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + requireContext().getIpAddress()).observe(viewLifecycleOwner) {
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
            fcm_token = getFromPaper(Const.PAPER_FCM_TOKEN),
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
            phone_number = requireArguments().getString(SIGN_UP_PHONE_NUMBER)?.replace("+", "")?.replace(" ", ""),
            sms_code = requireArguments().getString(SIGN_UP_SMS_CODE),
            sim_iccd = device.sim_iccd.toString(),
            version = "0",
            surname = "",
            password = encryptPassword(binding.etPassword.text.toString()),
            userInfo = data,
            string_line = "",
            emp_ref_code = requireArguments().getString(SIGN_UP_REF_CODE).orEmpty()
        )
        viewModel.finishReg(finishRegRequest).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackbar(getString(R.string.sign_up_success), getString(R.string.successfully)) { requireContext().startActivityWithClearTask(LoginActivity::class.java) }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun checkPassword(password: String) {
        setRequirementState(binding.tvPasswordNumber, password.containsNumber())
        setRequirementState(binding.tvPasswordLength, password.length in 8..15)
        setRequirementState(binding.tvPasswordLetter, password.hasLetter())
        setRequirementState(binding.tvPasswordSpecialSymbol, password.hasSpecialSymbol())
    }

    private fun setRequirementState(textView: TextViewRegular, isEnable: Boolean) {
        if (isEnable) {
            textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.pin_dot_success_12dp), null, null, null)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.monitoring_amount))
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.pin_dot_disable), null, null, null)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor_50))
        }
    }

}
