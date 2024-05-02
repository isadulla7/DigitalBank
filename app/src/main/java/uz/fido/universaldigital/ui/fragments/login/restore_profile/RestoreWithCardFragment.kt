package uz.fido.universaldigital.ui.fragments.login.restore_profile

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpFlagsEnum
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentRestoreWithCardBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignInResponse
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.PHONE_NUMBER
import uz.fido.utils.utility.context.GetDeviceInfo
import uz.fido.utils.utility.context.checkForExpireDate
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.language.Utility.getDeviceName

@AndroidEntryPoint
class RestoreWithCardFragment :
    BaseFragment<FragmentRestoreWithCardBinding, RestoreProfileViewModel>(
        FragmentRestoreWithCardBinding::inflate, RestoreProfileViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initClickListener()
        initFieldsListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListener() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            finishOperation()
        }
    }

    private fun initFieldsListener() {
        binding.cardNumber.addTextChangedListener { checkEditTexts() }
        binding.cardExpire.addTextChangedListener { checkEditTexts() }
    }

    private fun checkEditTexts() {
        val editTexts = listOf(
            binding.cardNumber, binding.cardExpire
        )
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    var isTrueCard = true
                    val et1 = binding.cardNumber.text.toString().trim().replace(" ", "")
                    val et2 = binding.cardExpire.text.toString().trim().replace("/", "")
                    if (et2.length > 1) {
                        requireContext().checkForExpireDate(binding.cardExpire.text.toString(), binding.cardExpire, binding.cardExpireLayout)
                    }
                    if (binding.cardExpire.text.toString().isEmpty()) {
                        isTrueCard = false
                    }
                    if (binding.cardNumberLayout.error != null) {
                        isTrueCard = false
                    }
                    if (binding.cardExpireLayout.error != null) {
                        isTrueCard = false
                    }
                    if (et1.isEmpty()) {
                        binding.cardNumberLayout.error = null
                    }
                    if (et2.isEmpty()) {
                        binding.cardExpireLayout.error = null
                    }
                    binding.btnContinue.isEnabled(et1.length == 16 && et2.length == 4 && isTrueCard)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun finishOperation() {
        val phoneNumber = requireArguments().getString(PHONE_NUMBER)
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val model = FinishRegRequest(
            phone_number = phoneNumber!!,
            sms_code = "",
            device_type = "A",
            device_code = requireContext().getDeviceIds(),
            device_name = getDeviceName(),
            version = "0",
            ip = requireContext().getIpAddress(),
            fcm_token = Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "",
            sim_iccd = device.sim_iccd,
            network_state = device.network_state,
            imei_data = device.imei_data,
            card_number = binding.cardNumber.rawText,
            expire_date = Format.sentExpireDate(binding.cardExpire.editableText.toString().replace("/", "")),
            flag = SignUpFlagsEnum.CardNumber.flag,
            app_version_code = BuildConfig.VERSION_CODE.toString(),
            app_version = BuildConfig.VERSION_NAME,
            os_version = Build.VERSION.SDK_INT.toString()
        )
        viewModel.finishReg(model).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    val signInResponse = it.data
                    signInResponse!!.phone_number = phoneNumber
                    requireContext().saveSignInResponse(signInResponse)

                    gotoWithSlide(
                        R.id.changePasswordFragment2, bundleOf(
                            ChangePasswordFragment.CHANGE_PASSWORD_OPERATION to ChangePasswordFragment.CHANGE_PASSWORD_SIGNUP,
                            ChangePasswordFragment.PHONE_NUMBER to phoneNumber
                        )
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }

    }


}
