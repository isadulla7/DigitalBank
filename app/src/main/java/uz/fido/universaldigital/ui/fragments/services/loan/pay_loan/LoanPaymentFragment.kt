package uz.fido.universaldigital.ui.fragments.services.loan.pay_loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.loans.loan_graph.CreditActualGraph
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentLoanPaymentBinding
import uz.fido.universaldigital.databinding.ViewDepositCreateBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientCreditFragment
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientLoanViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.math.BigDecimal

@AndroidEntryPoint
class LoanPaymentFragment:BaseFragment<FragmentLoanPaymentBinding,ClientLoanViewModel>(
    FragmentLoanPaymentBinding::inflate,ClientLoanViewModel::class.java
) {

    private lateinit var clientProduct: CreditProduct
    private lateinit var clientActualGraph: CreditActualGraph
    private var totalAmount: BigDecimal = 0.toBigDecimal()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            clientProduct = it.serializable<CreditProduct>(ClientCreditFragment.CLIENT_CREDIT_MODEL) as CreditProduct
          clientActualGraph = it.serializable<CreditActualGraph>("actualGraph") as CreditActualGraph
        }
        binding.linearSwitch.visibility=View.GONE
        initView()
        textWatchers()
        setonClick()
    }

    private fun textWatchers() {
        binding.etAmount.addTextChangedListener {
            if (!it.isNullOrEmpty()){
                binding.btnContinue.isEnabled(true)
            }else{binding.btnContinue.isEnabled(false)}
        }
    }

    private fun setonClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (binding.etAmount.text.toString().isNotEmpty()){
                clientProduct.paymentAmount = binding.etAmount.text.toString().replace(" ", "")
                clientProduct.earlyClosure = arguments?.getString("earlyClosure")?:"1" /*if (binding.switchId.isChecked) "2" else "1"*/
            }
            gotoWithSlide(R.id.confirmCreditPaymentFragment, bundleOf(ClientCreditFragment.CLIENT_CREDIT_MODEL to clientProduct))

        }
    }

    private fun setText() {
        val recommendedAmount = (totalAmount - clientProduct.mainAccBalance.toBigDecimal()).divide(100.toBigDecimal()).toString()
       // binding.textMaxAmount.text = Format.formatAmount(Format.convertFromTiynDivide(clientProduct.totalDebt))
        binding.maxAmount.text= if (recommendedAmount.startsWith("-")) "0" else Format.formatAmount(
            recommendedAmount
        )+" сум"

    }

    private fun initView() {
        if (clientProduct.saldo5!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() > 0.toBigDecimal()) {
            addView(getString(R.string.overdue_principal_amount), clientProduct.overdueDebt!!)
            addView(getString(R.string.credit_accurated_interest), clientProduct.saldo7!!)
            addView(getString(R.string.overdue_interest_amount), clientProduct.saldo46!!)
            addView(getString(R.string.credit_accurated_penalty), clientProduct.saldo22!!)
            addView(
                getString(R.string.total_amount),
                Format.formatAmount(
                    (clientProduct.saldo7!!.toBigDecimal() + clientProduct.overdueDebt!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() + clientProduct.saldo22!!.toBigDecimal()).divide(
                        100.toBigDecimal()
                    ).toString()
                )
            )
            totalAmount =
                clientProduct.saldo7!!.toBigDecimal() + clientProduct.overdueDebt!!.toBigDecimal() + clientProduct.saldo46!!.toBigDecimal() + clientProduct.saldo22!!.toBigDecimal()
        } else {
            addView(getString(R.string.main_debt), clientActualGraph.factSaldo)
            addView(getString(R.string.percents), clientActualGraph.percent)
            addView(
                getString(R.string.total_amount),
                Format.formatAmount(
                    (clientActualGraph.factSaldo.toBigDecimal() + clientActualGraph.percent.toBigDecimal()).divide(100.toBigDecimal()).toString()
                )
            )
            totalAmount = clientActualGraph.factSaldo.toBigDecimal() + clientActualGraph.percent.toBigDecimal()
            setText()

        }
    }


    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ViewDepositCreateBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.setText(value)
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }
}