package uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.confirm_payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmRequisitesPaymentBinding
import uz.fido.universaldigital.databinding.ItemConfirmPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.transfers.transfer_to_account.RequisitesViewModel
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Command.ABS
import uz.fido.utils.const.Command.CARD
import uz.fido.utils.const.Command.PURSE
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_UZS
import uz.fido.utils.const.ServiceId
import uz.fido.utils.format.Format
import uz.fido.utils.format.Format.formatDecimalSeparator
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ConfirmRequisitesPayment :
    BaseFragment<FragmentConfirmRequisitesPaymentBinding, RequisitesViewModel>(
        FragmentConfirmRequisitesPaymentBinding::inflate, RequisitesViewModel::class.java
    ) {

    private lateinit var paymentParamsArrayList: ArrayList<PaymentParams>
    private lateinit var params: HashMap<String, String>
    private lateinit var paymentService: PaymentService

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var currency = CURRENCY_CHAR_UZS
    private var senderCard: CardResponse? = null
    private var amount: String = ""
    private var percent = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentParamsArrayList =
            requireArguments().serializable<ArrayList<PaymentParams>>("list") as ArrayList<PaymentParams>
        currency = requireArguments().getString("currency").toString()
        percent = requireArguments().getDouble("percent")
        amount = requireArguments().getString("amount").toString()
        paymentService =
            requireArguments().serializable<PaymentService>("paymentService") as PaymentService
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initCards()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawView()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            senderCard?.let {
                getParams()
                createPayment()
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, amount, currency
            ) { cardResponse ->
                cardResponse?.let { card ->
                    binding.btnContinue.isEnabled(
                        !BaseCardUtils.compareWithBalance(
                            getTotalAmount(), card
                        )
                    )
                    senderCard = card
                }
            }
        }
    }

    private fun getTotalAmount(): String {
        return (amount.toBigDecimal() + amount.toBigDecimal() * percent.toBigDecimal() / BigDecimal(
            100
        )).toString()
    }

    private fun drawView() {
        for (paymentParams in paymentParamsArrayList) {
            if (paymentParams.is_visible.equals("Y")) {
                if (paymentParams.def_value.trim().isNotEmpty()) {
                    if ((paymentParams.name?.trim() ?: "").isNotEmpty()) {
                        if (paymentParams.code == "AMOUNT") {
                            addViews(getString(R.string.commission), "$percent %")
                            addViews(
                                paymentParams.name.toString(),
                                "${amount.toBigDecimal().formatDecimalSeparator()} $currency"
                            )
                        } else {
                            addViews(paymentParams.name.toString(), paymentParams.def_value)
                        }
                    }
                }
            }
        }
    }

    private fun addViews(name: String, value: String) {
        val mainBlockBinding = ItemConfirmPaymentBinding.inflate(
            LayoutInflater.from(requireContext()), requireView().parent as ViewGroup, false
        )
        mainBlockBinding.textName.text = name
        mainBlockBinding.textValue.text = value
        binding.content.addView(mainBlockBinding.root)
    }

    private fun getParams(): HashMap<String, String> {
        params = HashMap()
        for (i in paymentParamsArrayList.indices) {
            params[paymentParamsArrayList[i].code] = paymentParamsArrayList[i].def_value
        }
        return params
    }

    private fun createPayment() {
        binding.btnContinue.setProgress(true)
        val model = CreatePaymentRequest(
            service_id = ServiceId.SERVICE_ID__4,
            params = params,
            from_object_id = senderCard?.object_id.toString(),
            amount = params["AMOUNT"].toString(),
            command = if (senderCard!!.object_type == WALLET) "$PURSE&$ABS" else "$CARD&$ABS",
            sms_code = "",
            i_request_id = ""
        )

        viewModel.createPaymentRequest(getClientToken(), model, "ONE_TIME_PAY/")
            .observe(viewLifecycleOwner) {
                it?.let {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }

                        Status.SUCCESS -> {
                            val bundle = bundleOf(
                                Const.OPERATION to "payment",
                                SuccessPaymentFragment.CONFIRM_PAYMENT_OPERATION to "requisites",
                                Const.OPERATION_AMOUNT to Format.formatMoney(amount) + " $currency",
                                Const.OPERATION_CURRENCY to currency,
                                Const.SENDER_CARD to senderCard,
                                Const.PAYMENT_SERVICE to Gson().toJson(paymentService),
                                Const.EXTRA_PARAMS to Gson().toJson(paymentParamsArrayList),
                                Const.TRANSACTION_ID to it.data?.request_id!!,
                                SuccessPaymentFragment.PAYMENT_KEY_VALUES to requireArguments().getSerializable(
                                    SuccessPaymentFragment.PAYMENT_KEY_VALUES
                                )
                            )
                            goto(R.id.successPaymentFragment, bundle)
                        }
                    }
                }
            }
    }

}