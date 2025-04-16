package uz.fido.universaldigital.ui.fragments.services.order_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.OrderCardRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmOrderCardBinding
import uz.fido.universaldigital.databinding.ItemConfirmPaymentBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConfirmOrderCardFragment : BaseFragment<FragmentConfirmOrderCardBinding, OrderCardViewModel>(
    FragmentConfirmOrderCardBinding::inflate, OrderCardViewModel::class.java
) {
    private lateinit var request: OrderCardRequest
    private lateinit var orderType: OrderCardStep2Fragment.OrderType

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var senderCard: CardResponse? = null
    private var container: ViewGroup? = null
    private var amount: Int = 0
    private var smsCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderType =
                it.serializable<OrderCardStep2Fragment.OrderType>("deliveryType") as OrderCardStep2Fragment.OrderType
            request = it.serializable<OrderCardRequest>("request") as OrderCardRequest
            amount = it.getInt("amount")
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
        initDetails()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.orderCard.setOnClickListener {
            checkSMS()
        }
        initCards()
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            smsCode = bundle.getString("sms_code").toString()
            orderCard()
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, (amount / 100).toString()
            ) { cardResponse ->
                cardResponse?.let { card ->
                    binding.orderCard.isEnabled(
                        !BaseCardUtils.compareWithBalanceNd(
                            amount.toString(), card
                        )
                    )
                    senderCard = card
                }
            }
        }
    }

    private fun orderCard() {
        binding.orderCard.setProgress(true)
        request.sms_code = smsCode
        viewModel.orderCard(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.orderCard.setProgress(false)
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, BasicSuccessFragment.ORDER_CARD)
                    gotoWithSlide(R.id.basicSuccessFragment, bundle)
                }

                Status.ERROR -> {
                    binding.orderCard.setProgress(false)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initDetails() {
        addView(getString(R.string.product), requireArguments().getString("productName").toString())
        if (requireArguments().getInt("cardType") != 3 && requireArguments().getInt("cardType") != 4) addView(
            getString(R.string.filial_name), requireArguments().getString("filial").toString()
        )
        if (orderType == OrderCardStep2Fragment.OrderType.DELIVERY) addView(
            getString(R.string.address), requireArguments().getString("address").toString()
        )
        if (request.orderType == "GL_NEW_PHIS_CARD") addView(
            getString(R.string.pin_code_for_card), request.pin_code.toString()
        )
        addView(getString(R.string.phone_for_contact), "+${request.contact}")
        if (requireArguments().getInt("cardType") != 1) addView(
            getString(R.string.phone_for_sms), "+${request.smsMobilePhone}"
        )
        addView(getString(R.string.card_expire), requireArguments().getString("expire").toString())
        addView(
            getString(R.string.total_amount),
            Format.formatAmount((amount / 100).toString(), 0) + " UZS"
        )
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding = ItemConfirmPaymentBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        viewDepositCreateBinding.textName.text = name
        viewDepositCreateBinding.textValue.text = value
        binding.content.addView(viewDepositCreateBinding.root)
    }

    private fun checkSMS() {
        senderCard?.let {
            if (!checkForPaymentSms(
                    card = it,
                    smsControlLimit = "-1",
                    amount = Format.formatAmountToTiyn(request.amount)
                )
            ) {
                orderCard()
            } else {
                checkForSms(
                    card = it,
                    amount = Format.formatAmountToTiyn(request.amount),
                    serviceId = request.service_id
                ) { needConfirmSms, stringLine ->
                    if (needConfirmSms == "Y") {
//                        gotoWithSlide(
//                            R.id.confirmSmsFragment2, bundleOf("operation" to ConfirmSmsFragment.SMS_OPERATION_PAYMENT)
//                        )
                    } else {
                        orderCard()
                    }
                }
            }
        }
    }


}