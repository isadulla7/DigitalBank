package uz.fido.universaldigital.ui.fragments.transfers.conversion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmConversionBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardBalance
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardNumberFormatted
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setCardTypeImage
import uz.fido.universaldigital.ui.utils.extensions.getDrawableFromRes
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ConfirmConversionFragment :
    BaseFragment<FragmentConfirmConversionBinding, ConversionViewModel>(
        FragmentConfirmConversionBinding::inflate, ConversionViewModel::class.java
    ) {

    companion object {
        const val CONVERSION_REQUEST = "conversion_request"
        const val CURRENCY_CODE = "currency_code"
        const val RECEIVER_CARD = "receiver_card"
        const val TOTAL_AMOUNT = "total_amount"
        const val CURRENT_RATE = "current_rate"
        const val SENDER_CARD = "sender_card"
    }

    private lateinit var conversionRequest: ConversionRequest
    private lateinit var senderCard: CardResponse
    private lateinit var receiverCard: CardResponse

    private var smsCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        senderCard = requireArguments().serializable<CardResponse>(SENDER_CARD) as CardResponse
        receiverCard = requireArguments().serializable<CardResponse>(RECEIVER_CARD) as CardResponse
        conversionRequest =
            requireArguments().serializable<ConversionRequest>(CONVERSION_REQUEST) as ConversionRequest
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            smsCode = bundle.getString("sms_code").toString()
            conversionRequest()
        }
        initSetOnClickListeners()
        initDetails()
    }

    private fun initDetails() {
        binding.apply {
            val currency =
                if (requireArguments().getString(CURRENCY_CODE) == "000") "UZS" else "USD"
            tvSender.text = Format.formatCardNumber(conversionRequest.from_object_value ?: "")
            tvTotalAmount.text =
                "${Format.conversionFormat(conversionRequest.amount.toDouble() / 100)} $currency"
            tvRate.text = requireArguments().getString(CURRENT_RATE).toString()
            cardNumber.setCardNumberFormatted(receiverCard)
            cardBalance.setCardBalance(receiverCard)
            cardType.setCardTypeImage(receiverCard)
            cardBackground.load(requireContext().getDrawableFromRes(receiverCard.bg_icon_name))
            btnContinue.isEnabled(true)
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener { checkForSmsConfirmation() }
    }

    private fun checkForSmsConfirmation() {
        if (!checkForPaymentSms(
                card = senderCard,
                smsControlLimit = "-1",
                amount = Format.formatAmountToTiyn(conversionRequest.amount)
            )
        ) {
            conversionRequest()
        } else {
            checkForSms(
                card = senderCard,
                amount = Format.formatAmountToTiyn(conversionRequest.amount),
                serviceId = conversionRequest.service_id
            ) { needSmsConfirm, stringLine ->
                if (needSmsConfirm == "Y") {
                    goto(
                        R.id.confirmSmsFragment,
                        bundleOf(
                            Const.OPERATION to ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY,
                            ConfirmSmsFragment.STRING_LINE to stringLine
                        )
                    )
                } else {
                    conversionRequest()
                }
            }
        }
    }

    private fun conversionRequest() {
        val currency = if (requireArguments().getString(CURRENCY_CODE) == "000") "UZS" else "USD"
        binding.btnContinue.setProgress(true)
        conversionRequest.sms_code = smsCode
        viewModel.conversion(
            getClientToken(), conversionRequest
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, SuccessPaymentFragment.CONVERSION)
                    bundle.putString(
                        Const.OPERATION_AMOUNT,
                        "${Format.conversionFormat(conversionRequest.amount.toDouble() / 100)} $currency"
                    )
                    bundle.putSerializable(Const.SENDER_CARD, senderCard)
                    gotoWithSlide(R.id.successPaymentFragment, bundle)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

}