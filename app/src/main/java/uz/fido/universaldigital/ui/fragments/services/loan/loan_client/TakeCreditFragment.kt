package uz.fido.universaldigital.ui.fragments.services.loan.loan_client

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.lang3.math.NumberUtils
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.loans.GetLoanRequest
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentTakeCreditBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class TakeCreditFragment : BaseFragment<FragmentTakeCreditBinding, ClientLoanViewModel>(
    FragmentTakeCreditBinding::inflate, ClientLoanViewModel::class.java
) {
    private lateinit var creditProduct: CreditProduct
    private var minAmount = "0"
    private var maxAmount = "0"
    private var selectedCard: CardResponse? = null
    private var amount: String = "0"
    private var smsCode: String = ""

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            creditProduct =
                it.serializable<CreditProduct>(ClientCreditFragment.CLIENT_CREDIT_MODEL) as CreditProduct
        }
        initCards()
        initDetails()
        onClickView()

    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            confirmTakeCredit()
        }
    }

    private fun initDetails() {
        binding.loanAmount.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(creditProduct.amount ?: "0")) + " UZS"
        binding.tvCreditTerm.text =
            creditProduct.lnMonth.toString() + " " + getString(R.string.month_12)
        binding.tvPaymentDay.text = creditProduct.redemptionDay
        binding.tvMonthlyPayment.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(creditProduct.followPaySum ?: "0")) + " UZS"
        binding.tvTotalPayment.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(creditProduct.tRedempTotal ?: "0")) + " UZS"
        binding.tvExceessPayment.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(creditProduct.tRedempPerc ?: "0")) + " UZS"
        binding.tvPercOverPayment.text =
            Format.naiveRound((creditProduct.percOverLoanAmount ?: "0").toFloat(), 2).toString() + " %"
        binding.tvTotalPerc.text = creditProduct.perc + " %"
        binding.tvDeadlineConfirmation.text = creditProduct.deadlineConfirmation

        if (minAmount != "0") {
            minAmount =
                Format.formatAmount(Format.convertFromTiynDivide(minAmount)).replace(" ", "")
        }
        maxAmount = creditProduct.amount ?: "0"
        if (NumberUtils.isParsable(maxAmount)) {
            maxAmount = Format.formatAmount(Format.convertFromTiynDivide(maxAmount))
                .replace(" ", "")
        }

        binding.etAmountMinMax.addTextChangedListener {
            amount = it.toString().replace(" ", "")
            isCurrent()
        }

    }

    private fun isCurrent() {
        if (amount.isNotEmpty()) {
            if (minAmount.toDouble() < amount.toDouble() && maxAmount.toDouble() > amount.toDouble() && selectedCard != null) {
                binding.btnContinue.isEnabled(true)
            } else binding.btnContinue.isEnabled(false)
        } else binding.btnContinue.isEnabled(false)
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            val listnew = arrayListOf<CardResponse>()
            it.forEach { card ->
                if ((card.object_type == UZCARD || card.object_type == HUMO_CARD) && card.state == "0") {
                    listnew.add(card)
                }
            }
            binding.chooseCardLayout.initCards(
                listnew, "0"
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    val amount = binding.etAmountMinMax.text.toString()
                    if (amount.isNotEmpty())
                        isCurrent()
                }
            }
        }
    }

    private fun confirmTakeCredit() {
        val getLoanRequest = GetLoanRequest(
            command = "abs&card",
            loanId = creditProduct.loanId ?: "0",
            amount = amount,
            to_object_value = selectedCard?.object_value.toString(),
            service_id = "-3",
            sms_code = smsCode,
            phone_number = getClientPhoneNumber()
        )

        takeCredit(getLoanRequest)

//        if (binding.checkBox.isChecked) {
//            takeCredit(getLoanRequest)
//        } else {
//            showSnackbar(getString(R.string.please_accept_privacy))
//        }
    }


    private fun takeCredit(getLoanRequest: GetLoanRequest) {
        viewModel.confirmGetLoan(getClientToken(), getLoanRequest).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }

                Status.SUCCESS -> {
                    goto(R.id.basicSuccessFragment, bundleOf(Const.OPERATION to "credit_take"))
                }
            }
        }
    }

    fun getClientPhoneNumber(): String {
        return getFromSecureStore(Const.PAPER_CLIENT_PHONE)
    }
}