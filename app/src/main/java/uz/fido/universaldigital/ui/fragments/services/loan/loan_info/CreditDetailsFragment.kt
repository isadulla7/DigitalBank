package uz.fido.universaldigital.ui.fragments.services.loan.loan_info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.FragmentCreditDetailsInfoBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.format.Format

@AndroidEntryPoint
class CreditDetailsFragment(private val clientProduct: CreditProduct) : DialogFragment() {

    private lateinit var binding: FragmentCreditDetailsInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditDetailsInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        initView()

    }

    private fun initView() {
        addView(
            getString(R.string.credit_type),
            getLoanType(context = requireContext(), clientProduct.creditType)
        )
        addView(
            if (isOverdraft(clientProduct.creditType)) getString(R.string.limit_amount) else getString(
                R.string.credit_amount
            ),
            Format.formatAmount(Format.convertFromTiynDivide(clientProduct.amount)) + " UZS"
        )
        addView(getString(R.string.main_credit_percent), clientProduct.perc + "%")
        addView(getString(R.string.perc_dlo), clientProduct.percDLO + " %")

        if (clientProduct.percDLP.isNotEmpty())
            addView(
                getString(R.string.perc_dlp),
                clientProduct.percDLP.toBigDecimal().divide(365.toBigDecimal()).toString() + " %"
            )
        if (!isOverdraft(clientProduct.creditType)) addView(
            getString(R.string.repayment_type),
            if (clientProduct.repaymentType == "1") getString(R.string.annuity) else getString(R.string.differencial)
        )


        addView(getString(R.string.start_contract_date), clientProduct.contractDate)
        addView(getString(R.string.contract_number), clientProduct.contractCode)
        addView(getString(R.string.end_contract_date), clientProduct.closeDate)

        if (!isOverdraft(clientProduct.creditType)) addView(
            getString(R.string.loan_id_for_payment),
            clientProduct.loanContractId
        )
        addView(getString(R.string.filial_name), clientProduct.filialName)
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.addLin.addView(viewDepositCreateBinding.root)
    }

    private fun isOverdraft(creditId: String): Boolean {
        return creditId == "54"
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