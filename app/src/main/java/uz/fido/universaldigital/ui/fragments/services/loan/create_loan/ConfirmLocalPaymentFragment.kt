package uz.fido.universaldigital.ui.fragments.services.loan.create_loan

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentRequest
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmLocalPaymentBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.services.map_payment.PaymentBranchViewModel
import uz.fido.utils.const.CardConst.CURRENCY_CARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConfirmLocalPaymentFragment :
    BaseFragment<FragmentConfirmLocalPaymentBinding, PaymentBranchViewModel>
        (FragmentConfirmLocalPaymentBinding::inflate, PaymentBranchViewModel::class.java) {

    private var amount: String = ""
    private var cardResponse: CardResponse? = null
    private var localPayment: LocalPayment? = null
    private var localPaymentType: LocalPaymentType? = null
    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            amount = arguments?.getString("amount").toString()
            localPayment = it.getSerializable("model") as LocalPayment?
            localPaymentType = it.getSerializable("type") as LocalPaymentType?
        }

        setTextInit()
        initCards()
        onCLickView()
    }

    private fun onCLickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            val model = LocalPaymentRequest(
                amount = Format.formatAmountToTiyn(amount),
                from_object_id = cardResponse!!.object_id,
                pay_onspot_id = localPayment?.id.toString(),
                receiver_phone = localPayment?.phone.toString(),
                service_id = "-19",
                command = if (cardResponse!!.object_type == WALLET) "purse&onspot" else "card&onspot"
            )
            viewModel.getLocalPayment(getClientToken(), model).observe(viewLifecycleOwner) {
                binding.btnContinue.setProgress(false)
                when (it.status) {
                    Status.SUCCESS -> {
                        gotoWithSlide(
                            R.id.basicSuccessFragment, bundleOf(
                                Const.OPERATION to BasicSuccessFragment.LOCAL_PAYMENT,
                                "amount" to amount
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

    private fun setTextInit() {
        binding.value.text = localPayment?.sv_merchant_name
        binding.textAmount.text = "${Format.formatAmount(amount)} UZS"
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, amount
            ) { cardResponse ->
                cardResponse?.let { card ->
                    this.cardResponse = card
                    if (card.balance.toBigDecimal().divide(100.toBigDecimal())
                            .compareTo(amount.toBigDecimal()) == -1
                        || card.currency_code == CURRENCY_CARD
                    ) {
                        binding.btnContinue.isEnabled(false)
                    } else {
                        binding.btnContinue.isEnabled(true)
                    }

                }
            }
        }
    }

}