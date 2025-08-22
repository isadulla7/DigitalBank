package uz.fido.universaldigital.ui.fragments.transfers.conversion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.conversion.ConversionRequest
import uz.fido.network.domain.model.conversion.ConversionResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmConversionBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment.Companion.SMS_MAX_LENGTH
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setBankLogo
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
import java.math.BigDecimal

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
    var extId:String=""

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

        setFragmentResultListener(ConfirmSmsFragment.SMS_CONVERSION_CONFIRM) { _, bundle ->
            smsCode = bundle.getString("sms_code").toString()
            confirmConversion(smsCode)
        }
        initSetOnClickListeners()
        initDetails()
    }

    private fun initDetails() {
        val senderAmount= if (senderCard.currency_code=="840") Format.formatAmount(conversionRequest.amount.toBigDecimal().divide(BigDecimal(100)).toString())+" ${senderCard.currency_char}"
        else Format.formatAmount(conversionRequest.amount_equivalent.toBigDecimal().divide(BigDecimal(100)).toString())+" ${senderCard.currency_char}"
        val receiverAmount= if (receiverCard.currency_code=="840") Format.formatAmount(conversionRequest.amount.toBigDecimal().divide(BigDecimal(100)).toString())+" ${receiverCard.currency_char}"
        else Format.formatAmount(conversionRequest.amount_equivalent.toBigDecimal().divide(BigDecimal(100)).toString())+" ${receiverCard.currency_char}"
        binding.apply {
            val currency =
                if (requireArguments().getString(CURRENCY_CODE) == "000") "UZS" else "USD"
            val amount= if (currency=="UZS") conversionRequest.amount_equivalent.toDouble() / 100 else conversionRequest.amount.toDouble() / 100
            tvSender.text = Format.formatCardNumber(conversionRequest.from_object_value ?: "")
            tvTotalAmount.text =
                "${Format.conversionFormat(amount)} $currency"
            tvRate.text = requireArguments().getString(CURRENT_RATE).toString()
            tvSenderAmount.text=senderAmount
            tvReceivedAmount.text=receiverAmount
            cardNumber.setCardNumberFormatted(receiverCard)
            cardBalance.setCardBalance(receiverCard)
            cardType.setCardTypeImage(receiverCard)
            bankLogo.setBankLogo(receiverCard)
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

                    checkConfirmSms(currency,it.data)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun checkConfirmSms(currency: String, resource: ConversionResponse?) {
        if (senderCard.currency_code=="840"){
            val bundle = Bundle()
            bundle.putString(Const.OPERATION, SuccessPaymentFragment.CONVERSION)
            bundle.putString(
                Const.OPERATION_AMOUNT,
                "${Format.conversionFormat(conversionRequest.amount.toDouble() / 100)} $currency"
            )
            bundle.putSerializable(Const.SENDER_CARD, senderCard)
            gotoWithSlide(R.id.successPaymentFragment, bundle)
        }else{
            extId = resource?.ext_id?:""
            val smsLen= resource?.sms_length?:6
            goto(
                R.id.confirmSmsFragment,
                bundleOf(
                    Const.OPERATION to ConfirmSmsFragment.SMS_CONVERSION_CONFIRM,
                    SMS_MAX_LENGTH to smsLen,
                )
            )
        }
    }

    private fun confirmConversion(smsCode: String) {
        val currency = if (requireArguments().getString(CURRENCY_CODE) == "000") "UZS" else "USD"
        binding.btnContinue.setProgress(true)
        conversionRequest.sms_code=smsCode
        conversionRequest.ext_id=extId
        viewModel.conversionConfirm(getClientToken(), conversionRequest).observe(viewLifecycleOwner){
            binding.btnContinue.setProgress(false)
            when(it.status){
                Status.SUCCESS->{

                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, SuccessPaymentFragment.CONVERSION)
                    bundle.putString(
                        Const.OPERATION_AMOUNT,
                        "${Format.conversionFormat(conversionRequest.amount.toDouble() / 100)} ${currency}"
                    )
                    bundle.putSerializable(Const.SENDER_CARD, senderCard)
                    gotoWithSlide(R.id.successPaymentFragment, bundle)
                }
                Status.ERROR->{
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

}