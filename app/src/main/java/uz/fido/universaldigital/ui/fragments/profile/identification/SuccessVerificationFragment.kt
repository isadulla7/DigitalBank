package uz.fido.universaldigital.ui.fragments.profile.identification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.os.bundleOf
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSuccessVerificationBinding
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.saveSignInPinResponse
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.language.Utility

@AndroidEntryPoint
class SuccessVerificationFragment : BaseFragment<FragmentSuccessVerificationBinding, IdentificationViewModel>(
    FragmentSuccessVerificationBinding::inflate, IdentificationViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadIllustration()
        binding.btnContinue.isEnabled(true)
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            swapKeys()
        }
    }

    private fun gotoWriteWay() {
        binding.btnContinue.setProgress(false)
        if (activity is MainActivity) {
            goto(
                R.id.action_successVerificationFragment2_to_productsFragment, bundleOf(Const.USER_IDENTIFIED to true)
            )
        } else {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadIllustration() {
        binding.imageView.load(R.drawable.ic_illustration_success)
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
                phoneNumber = getFromSecureStore(Const.PAPER_CLIENT_PHONE).replace("", ""),
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
                    gotoWriteWay()
                }
            }
        }
    }

    private fun getUserInfo() {
        viewModel.getUserDetailedInfo(Keys.getUserInfoUrl() + requireContext().getIpAddress()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { data ->
                    signInRequest(data)
                }

                Status.ERROR -> {
                    gotoWriteWay()
                }
            }
        }
    }

    private fun signInRequest(userInfo: UserInfo) {
        val device = GetDeviceInfo(requireContext()).deviceInfo
        val signInRequest = SignInRequestNew(
            phone_number = getFromSecureStore(Const.PAPER_CLIENT_PHONE).replace("", ""),
            device_type = "A",
            device_code = requireContext().getDeviceIds(),
            device_name = Utility.getDeviceName(),
            version = "1",
            ip = requireContext().getIpAddress(),
            client_id = Keys.getClientId(),
            fcm_token = requireContext().getFromSecureStore(Const.PAPER_FCM_TOKEN),
            password = getFromSecureStore(Const.PASSWORD_ENC),
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
                    if (signInResponse?.token != null) {
                        saveSignInResponse(signInResponse)
                    } else {
                        showSnackbar(it.message.toString())
                    }
                    gotoWriteWay()
                }

                Status.ERROR -> {
                    gotoWriteWay()
                }
            }
        }
    }

    private fun saveSignInResponse(signInResponse: SignInResponse) {
        Thread {
            saveSignInPinResponse(signInResponse)
        }.start()
    }

    private fun changeKey(keyK: String) {
        try {
            val key1 = getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("@$#", 3)
            val key2 = getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("&^%", 6)
            if (getFromSecureStore(Const.PASSWORD_ENC).isEmpty() || getFromSecureStore(Const.STRING_LINE).isEmpty()) {
                requireActivity().logOut()
            } else {
                val newKey = CryptoUtil.encrypt(
                    getFromSecureStore(Const.PASSWORD_ENC), key1
                ) + keyK + CryptoUtil.encrypt(
                    getFromSecureStore(Const.STRING_LINE), key2
                )
                saveToSecureStore(Const.KEY_K, newKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}