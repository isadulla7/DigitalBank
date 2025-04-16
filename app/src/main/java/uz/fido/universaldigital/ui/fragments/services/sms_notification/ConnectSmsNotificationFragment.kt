package uz.fido.universaldigital.ui.fragments.services.sms_notification

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.home.CheckSMSActivateRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConnectSmsNotificationBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConnectSmsNotificationFragment :
    BaseFragment<FragmentConnectSmsNotificationBinding, ConnectSmsNotificationViewModel>(
        FragmentConnectSmsNotificationBinding::inflate, ConnectSmsNotificationViewModel::class.java
    ) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initTextChangeListener()
        initSetOnClickListeners()
    }

    private fun initTextChangeListener() {
        binding.cardNumber.doAfterTextChanged {
            it?.let {
                if (it.toString().length == 19) {
                    binding.btnContinue.isEnabled(true)
                    val cardNumberFormatted = binding.cardNumber.text.toString().replace(" ", "")
                    getObjectInfo(cardNumberFormatted)
                } else {
                    binding.btnContinue.isEnabled(false)
                    binding.ownerName.isVisible = false
                }
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            val cardNumberFormatted = binding.cardNumber.text.toString().replace(" ", "")
            checkSmsActivate(cardNumberFormatted)
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getObjectInfo(cardNumber: String) {
        viewModel.getCardInfo(getClientToken(), CheckCardRequestP2p("card", cardNumber))
            .observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    val response = it.data as CheckCardResponse
                    setOwnerName(response.empbossed_name)
                }
            }
    }

    private fun checkSmsActivate(cardNumber: String) {
        binding.btnContinue.setProgress(true)
        viewModel.checkSMSActivate(
            getClientToken(), CheckSMSActivateRequest(
                object_value = cardNumber,
                app_key_hash = AppSignatureHelper(requireContext()).appKeyHash
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val line = it.data?.string_line ?: ""
                    gotoWithSlide(
                        R.id.confirmSmsFragment, bundleOf(
                            Const.OPERATION to ConfirmSmsFragment.SMS_OPERATION_CONNECT_SMS_INFO,
                            Const.PHONE_NUMBER to getClientPhoneNumber(),
                            Const.CARD_NUMBER to cardNumber,
                            ConfirmSmsFragment.STRING_LINE to line
                        )
                    )
                }

                Status.ERROR -> {
                    binding.btnContinue.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setOwnerName(ownerName: String) {
        binding.ownerName.isVisible = true
        binding.ownerName.text = ownerName
    }

}