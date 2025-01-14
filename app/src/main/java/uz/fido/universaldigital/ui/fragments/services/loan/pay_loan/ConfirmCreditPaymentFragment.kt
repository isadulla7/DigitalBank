package uz.fido.universaldigital.ui.fragments.services.loan.pay_loan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreditConfirmBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_success.SuccessPaymentFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientCreditFragment
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientLoanViewModel
import uz.fido.universaldigital.ui.fragments.transfers.confirm_transfer.ConfirmSmsForTransfer
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConfirmCreditPaymentFragment : BaseFragment<FragmentCreditConfirmBinding, ClientLoanViewModel>(
    FragmentCreditConfirmBinding::inflate, ClientLoanViewModel::class.java
) {
    private lateinit var clientProduct: CreditProduct
    private lateinit var selectedCard: CardResponse

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { clientProduct = it.serializable<CreditProduct>(ClientCreditFragment.CLIENT_CREDIT_MODEL) as CreditProduct }
        setData()
        initCards()
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (checkForPaymentSms(selectedCard, "-1", clientProduct.paymentAmount.toString())) {
                checkForSms(
                    selectedCard, clientProduct.paymentAmount.toString(), "-2"
                ) { isSmsConfirm, stringLine ->
                    if (isSmsConfirm == "Y") {
                        val model = getModel()
                        gotoWithSlide(
                            R.id.confirmSmsForTransfer, bundleOf(
                                ConfirmSmsForTransfer.STRING_LINE to stringLine,
                                "model" to model
                            )
                        )
                    } else {
                        createPayment()
                    }
                }
            } else {
                createPayment()
            }
        }
    }

    private fun getModel(): CreatePaymentRequest {
        val hashMap = HashMap<String, String>()
        hashMap["LOANS_ID"] = clientProduct.loanContractId.orEmpty()
        hashMap["AMOUNT"] = Format.formatAmountToTiyn(clientProduct.paymentAmount!!)
        hashMap["EARLY_CLOSURE"] = clientProduct.earlyClosure!!
        return CreatePaymentRequest(
            service_id = "-2",
            from_object_id = selectedCard.object_id,
            amount = Format.formatAmountToTiyn(clientProduct.paymentAmount!!),
            command = if (selectedCard.object_type == WALLET) "purse&abs" else "card&abs",
            params = hashMap,
            keep_future_percents = clientProduct.keep_future_percents
        )
    }

    private fun createPayment() {
        val hashMap = HashMap<String, String>()
        hashMap["LOANS_ID"] = clientProduct.loanContractId.orEmpty()
        hashMap["AMOUNT"] = Format.formatAmountToTiyn(clientProduct.paymentAmount!!)
        hashMap["EARLY_CLOSURE"] = clientProduct.earlyClosure!!
        val createPayment = CreatePaymentRequest(
            service_id = "-2",
            from_object_id = selectedCard.object_id,
            amount = Format.formatAmountToTiyn(clientProduct.paymentAmount!!),
            command = if (selectedCard.object_type == WALLET) "purse&abs" else "card&abs",
            params = hashMap,
            keep_future_percents = clientProduct.keep_future_percents
        )
        binding.btnContinue.setProgress(true)
        viewModel.loanRepayment(getClientToken(), createPayment).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    goto(
                        R.id.successPaymentFragment, bundleOf(
                            Const.OPERATION to SuccessPaymentFragment.CREDIT_PAYMENT, Const.OPERATION_AMOUNT to createPayment.amount, "transactId" to it.data?.request_id!!
                        )
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        binding.apply {
            textCreditName.text = getLoanType(requireContext(), clientProduct.creditType.orEmpty())
            textContractNumber.text = clientProduct.contractCode
            textContractDate.text = clientProduct.contractDate
            loanAmount.text = "${Format.formatAmount(Format.formatAmountFromTiynToInteger(clientProduct.amount.orEmpty()))} UZS"
            accountNumberForRepayment.text = clientProduct.loan2
            repaymentAmount.text = "${Format.formatAmount(clientProduct.paymentAmount.toString())} UZS"
            commission.text = "0 UZS (0%)"
            textTotalAmount.text = "${Format.formatAmount(clientProduct.paymentAmount.toString())} UZS"
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(it as ArrayList<CardResponse>, clientProduct.paymentAmount, CurrencyConst.CURRENCY_CHAR_UZS) { cardResponse ->
                cardResponse?.let { card ->
                    try {
                        if (card.balance.toBigDecimal().divide(100.toBigDecimal()).compareTo((clientProduct.paymentAmount ?: "0").toBigDecimal()) == -1) {
                            binding.btnContinue.isEnabled(false)
                        } else {
                            selectedCard = cardResponse
                            binding.btnContinue.isEnabled(true)
                        }
                    } catch (e: Exception) {
                        recordException(e, ::initCards.name)
                    }
                }
            }
        }
    }

    private fun getLoanType(context: Context, loanId: String): String {
        return when (loanId) {
            "24" -> context.getString(R.string.loan_type_1)
            "30" -> context.getString(R.string.loan_type_2)
            "32" -> context.getString(R.string.loan_type_3)
            "34" -> context.getString(R.string.loan_type_4)
            "54" -> context.getString(R.string.loan_type_5)
            "59" -> context.getString(R.string.loan_type_6)
            else -> context.getString(R.string.loan)
        }
    }

}