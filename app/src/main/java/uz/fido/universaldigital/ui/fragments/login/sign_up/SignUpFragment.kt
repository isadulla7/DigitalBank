package uz.fido.universaldigital.ui.fragments.login.sign_up

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSignUpBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.APIServiceConst.USER_INFO_URL
import uz.fido.utils.const.Const
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>(
    FragmentSignUpBinding::inflate, SignUpViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setTermsOfUseColor()
        initSetOnClickListeners()
        initTextChangeListener()
        setPhonePrefix()
    }

    private fun initSetOnClickListeners() {
        binding.tvGotoSignIn.setOnClickListener {
            pop()
        }
        binding.btnContinue.setOnClickListener {
            if (isValidPhoneNumber()) {
                if (isInternetConnected(requireContext())) {
                    binding.btnContinue.setProgress(true)
                    Paper.book().write(Const.PAPER_CLIENT_PHONE, phoneNumberFormatted())
                    swapKeysRequest()
                }
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun setPhonePrefix() {
        binding.etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etPhoneNumber.text.toString()
                    .isEmpty()
            ) binding.etPhoneNumber.setText(getString(R.string.phone_number_prefix))
        }
        binding.etPhoneNumber.setOnKeyListener { _, _, event ->
            event.keyCode == KeyEvent.KEYCODE_DEL && binding.etPhoneNumber.text.toString().length == 4
        }
    }

    private fun initTextChangeListener() {
        binding.etPhoneNumber.addTextChangedListener { phone ->
            binding.btnContinue.isEnabled(phone.toString().length == 17)
        }
    }

    private fun swapKeysRequest() {
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
        viewModel.getUserDetailedInfo(USER_INFO_URL).observe(viewLifecycleOwner) {
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
            device_id = requireContext().getDeviceIds()
        )
        viewModel.checkSignUpRequest(
            model
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        gotoSmsConfirmFragment(it, model)
                    }

                    Status.ERROR -> {
                        binding.btnContinue.setProgress(false)
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun gotoSmsConfirmFragment(
        response: Resource<BaseResponse>,
        model: SignUpCheckRequest
    ) {
        val bundle = Bundle()
        bundle.putString(Const.OPERATION, ConfirmSmsFragment.SMS_OPERATION_SIGN_UP)
        bundle.putString(
            Const.PHONE_NUMBER,
            binding.etPhoneNumber.editableText.toString()
        )
        bundle.putSerializable("data", model)
        bundle.putString(Const.RANDOM_TEXT, response.data?.string_line.toString())
        gotoWithSlide(R.id.confirmSmsFragment, bundle)
    }

    private fun setKeyBForDiffieHellman(response: SwapKeysResponse) {
        DiffieHellman.getDiffieHellman().SetKeyB(response.ecnryptData)
    }

    private fun isValidPhoneNumber(): Boolean {
        val phone = phoneNumberFormatted()
        return phone.length == 12 && phone.startsWith("998")
    }

    private fun phoneNumberFormatted(): String {
        return binding.etPhoneNumber.editableText.toString()
            .replace(" ", "")
            .replace("+", "")
    }

    private fun setTermsOfUseColor() {
        binding.textSingUpTerms.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setLinkTextColor(
                ContextCompat.getColor(requireContext(), R.color.brandRedColor)
            )
        }
    }
}