package uz.fido.universaldigital.ui.fragments.login.restore_profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.password.ChangePasswordRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentChangePasswordBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.getUserQwerty
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveUserQwerty
import uz.fido.utils.const.APIServiceConst.USER_CLIENT_ID
import uz.fido.utils.security.encryptPassword
import uz.fido.utils.utility.context.startActivityWithClearTask
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.isValidPasswordFormat
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding, RestoreProfileViewModel>(
    FragmentChangePasswordBinding::inflate, RestoreProfileViewModel::class.java
) {

    companion object {
        const val CHANGE_PASSWORD_SIGNUP = "change_password_signup"
        const val CHANGE_PASSWORD_FORGOT = "forgot_password"
        const val CHANGE_PASSWORD_OPERATION = "operation"
        const val CHANGE_PASSWORD = "change_password"
        const val PHONE_NUMBER = "phone_number"
        const val SMS_CODE = "sms_code"
    }

    private lateinit var phoneNumber: String
    private lateinit var operation: String
    private lateinit var smsCode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentsFromBundle()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initListeners()
        initDefaults()

    }

    private fun initListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.etPassword.addTextChangedListener { checkForButton() }
        binding.etRepeatPassword.addTextChangedListener { checkForButton() }
        binding.btnContinue.setOnClickListener {
            if (isValidPasswordFormat(binding.etPassword.text.toString())){
            binding.btnContinue.setProgress(true)
            changePasswordOperation()
            }else{
                showSnackbar(requireContext().getString(R.string.pass_check))
            }
        }
    }

    private fun initDefaults() {
        when (operation) {
            CHANGE_PASSWORD -> {
                binding.oldPasswordLayout.visibility = View.VISIBLE
            }

            CHANGE_PASSWORD_SIGNUP -> {
                binding.etOldPassword.visibility = View.GONE
                binding.passdescription.visibility = View.VISIBLE
            }
        }
    }

    private fun changePasswordOperation() {
        when (operation) {
            CHANGE_PASSWORD -> {
                if (encryptPassword(
                        binding.etOldPassword.text.toString().trim()
                    ) == requireContext().getUserQwerty()
                ) {
                    changePassword()
                } else {
                    showSnackbar(getString(R.string.old_password_is_wrong))
                }
            }

            CHANGE_PASSWORD_FORGOT -> {
                changePasswordWithSmsRequest()
            }

            CHANGE_PASSWORD_SIGNUP -> {
                if (isValidPasswordFormat(binding.etPassword.text.toString())) changePassword()
            }
        }
    }

    private fun changePassword() {
        showProgress()
        val model = ChangePasswordRequest(
            new_password = encryptPassword(binding.etPassword.text.toString().trim()),
            current_password = requireContext().getUserQwerty()
        )
        viewModel.changePassword(getClientToken(), model).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    showSnackbar(getString(R.string.success_change_password))
                    Handler(Looper.getMainLooper()).postDelayed(
                        { requireActivity().logOut() }, 500
                    )
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message!!)
                }
            }
        }
    }

    private fun changePasswordWithSmsRequest() {
        showProgress()
        val changePasswordRequest = ChangePasswordRequest(
            phone_number = phoneNumber,
            sms_code = smsCode,
            password = binding.etPassword.text.toString(),
            client_id = USER_CLIENT_ID
        )
        viewModel.changePasswordWithSMS(changePasswordRequest).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    requireContext().saveUserQwerty(
                        encryptPassword(
                            binding.etPassword.text.toString().trim()
                        )
                    )
                    showSnackbar(getString(R.string.success_change_password))
                    requireContext().startActivityWithClearTask(LoginActivity::class.java)
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message!!)
                }
            }
        }
    }


    private fun checkForButton() {
        val pass = binding.etPassword.text.toString()
        val repeatedPass = binding.etRepeatPassword.text.toString()
        if (pass.length >= 8 && repeatedPass.length >= 8) {
            if (pass == repeatedPass) {
                binding.btnContinue.isEnabled(true)
                binding.passDontMatch.visibility = View.GONE
            } else {
                binding.btnContinue.isEnabled(false)
                binding.passDontMatch.visibility = View.VISIBLE
            }
        } else {
            binding.btnContinue.isEnabled(false)
        }
    }

    private fun getArgumentsFromBundle() {
        arguments?.let {
            operation = it.getString(CHANGE_PASSWORD_OPERATION).toString()
            when (operation) {
                CHANGE_PASSWORD_FORGOT -> {
                    phoneNumber = it.getString(PHONE_NUMBER).toString()
                    smsCode = it.getString(SMS_CODE).toString()
                }

                CHANGE_PASSWORD_SIGNUP -> {
                    phoneNumber = it.getString(PHONE_NUMBER).toString()
                }
            }
        }
    }

}