package uz.fido.universaldigital.ui.fragments.transfers.confirm_transfer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmTransferBinding
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.fragments.transfers.utils.getServiceIdInfo
import uz.fido.universaldigital.ui.fragments.transfers.utils.setCommand
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setBankLogo
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNumberFormatted
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ConfirmTransferFragment : BaseFragment<FragmentConfirmTransferBinding, ConfirmTransferViewModel>(
    FragmentConfirmTransferBinding::inflate, ConfirmTransferViewModel::class.java
) {

    companion object {
        const val NAVIGATION_BACK = "navigation_back"
        const val AMOUNT = "amount"
    }

    private lateinit var transferDto: TransferDto
    private lateinit var p2pRequest: P2PRequest
    private var smsCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferDto = requireArguments().serializable<TransferDto>(SuccessTransferFragment.TRANSFER_DTO) as TransferDto
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDetails()
        initSetOnClickListeners()
        initReceiverCardDetails()
        handleOnBackPress()
    }

    private fun initDetails() {
        binding.tvReceiver.text = transferDto.receiverCard?.card_owner
        binding.tvReceiverCard.text = Format.formatCardNumber(transferDto.receiverCard?.card_number ?: "")
        binding.tvReceivedAmount.text = "${Format.conversionFormat(transferDto.transferAmount?.toDouble()?.div(100) ?: 0.0)} ${getString(uz.fido.utils.R.string.sum)}"
        setCommission(transferDto.commission.toString(), transferDto.transferAmount?.toDouble()?.div(100) ?: 0.0)
        setTotalAmount(transferDto.commission.toString(), transferDto.transferAmount?.toDouble()?.div(100) ?: 0.0)
        binding.btnContinue.isEnabled(true)
    }

    private fun initReceiverCardDetails() {
        transferDto.senderCard?.let {
            binding.cardNumber.setCardNumberFormatted(it)
            binding.cardBalance.setCardBalance(it)
            binding.cardType.setCardTypeImage(it)
            binding.bankLogo.setBankLogo(it)
            binding.cardBackground.load(requireContext().getDrawableFromRes(it.bg_icon_name))
        }
    }

    private fun setTotalAmount(commission: String, amount: Double) {
        val calculatedCommission = amount * commission.toDouble() / 100
        val totalAmount = amount + calculatedCommission
        binding.tvTotalAmount.text = "${Format.conversionFormat(totalAmount)} ${getString(uz.fido.utils.R.string.sum)}"
    }

    private fun setCommission(commission: String, amount: Double) {
        val calculatedCommission = amount * commission.toDouble() / 100
        binding.tvCommission.text = commission + " % ($calculatedCommission ${getString(uz.fido.utils.R.string.sum)})"
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener {
            setFragmentResult(NAVIGATION_BACK, bundleOf("confirm" to true))
            findNavController().navigateUp()
        }
        binding.btnContinue.setOnClickListener {
            transferDto.senderCard?.let { senderCard ->
                p2pRequest = P2PRequest(
                    command = setCommand(senderCard.object_type, transferDto.receiverCard?.card_number.orEmpty()),
                    amount = transferDto.transferAmount.toString(),
                    from_object_id = senderCard.object_id,
                    from_object_expire = senderCard.object_expiry,
                    service_id = getServiceIdInfo(transferDto.receiverCard?.card_number.orEmpty(), senderCard.object_value),
                    to_object_value = transferDto.receiverCard?.card_number.orEmpty(),
                    to_object_expire = transferDto.receiverCard?.card_expire.orEmpty(),
                    to_object_id = transferDto.receiverCard?.card_id,
                    request_id = transferDto.requestId.orEmpty(),
                    to_embossed_name = transferDto.receiverCard?.card_owner.orEmpty(),
                    phone_number = if (transferDto.operation == SuccessTransferFragment.TRANSFER_BY_PHONE) transferDto.phoneNumber else ""
                )
                checkForSmsConfirmation()
            }
        }
    }

    private fun checkForSmsConfirmation() {
        if (!checkForPaymentSms(
                card = transferDto.senderCard!!,
                smsControlLimit = "0",
                amount = Format.formatAmountToTiyn(transferDto.transferAmount)
            )
        ) {
            p2pRequest()
        } else {
            checkForSms(
                card = transferDto.senderCard!!,
                amount = transferDto.transferAmount!!,
                serviceId = getServiceIdInfo(transferDto.receiverCard?.card_number!!, transferDto.senderCard!!.object_value),
            ) { needConfirmSms, stringLine ->
                if (needConfirmSms == "Y") {
                    gotoWithSlide(
                        R.id.confirmSmsForTransfer, bundleOf(
                            ConfirmSmsForTransfer.STRING_LINE to stringLine,
                            ConfirmSmsForTransfer.TRANSFER_REQUEST to p2pRequest,
                            SuccessTransferFragment.TRANSFER_DTO to transferDto
                        )
                    )
                } else {
                    p2pRequest()
                }
            }
        }
    }

    private fun p2pRequest() {
        binding.btnContinue.setProgress(true)
        p2pRequest.sms_code = smsCode
        viewModel.p2pRequest(getClientToken(), p2pRequest).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(R.id.successTransferFragment, bundleOf(SuccessTransferFragment.TRANSFER_DTO to transferDto))
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun handleOnBackPress() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setFragmentResult(NAVIGATION_BACK, bundleOf("confirm" to true))
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}