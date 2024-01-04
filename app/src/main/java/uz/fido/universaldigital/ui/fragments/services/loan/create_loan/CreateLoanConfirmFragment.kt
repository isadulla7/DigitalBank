package uz.fido.universaldigital.ui.fragments.services.loan.create_loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.loan_groups.CreditGroup
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.loans.CreateCreditRequestNew
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateLoanConfirmBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.universaldigital.ui.fragments.services.loan.LoanViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class CreateLoanConfirmFragment:BaseFragment<FragmentCreateLoanConfirmBinding,LoanViewModel>
    (FragmentCreateLoanConfirmBinding::inflate,LoanViewModel::class.java){
    private var creditGroup: CreditGroup? = null
    private var cards: CardResponse? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            creditGroup = it.serializable<CreditGroup>("product") as CreditGroup
            cards = it.serializable<CardResponse>("selectedCard") as CardResponse
        }
        binding.btnContinue.isEnabled(true)
        onClickView()
        addList()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            createCreditRequest()
        }
    }

    private fun createCreditRequest() {
        binding.btnContinue.setProgress(true)
        val formattedAmount = Format.formatAmountToTiyn(creditGroup?.amount?.replace(" ", ""))
        viewModel.createCreditRequest(getClientToken(), CreateCreditRequestNew(
            amount = formattedAmount, to_object_value = cards!!.object_value, productId = creditGroup?.productId.toString()
        )).observe(viewLifecycleOwner){
            binding.btnContinue.setProgress(false)
            when(it.status){
                Status.SUCCESS->{
                   goto(R.id.creditSuccessFragment)
                }
                Status.ERROR->{
                   // goto(R.id.creditSuccessFragment)
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun addList() {
        val names = arrayOf(
            creditGroup?.name,
            Format.formatAmount(creditGroup?.amount) + " " + getString(R.string.summa),
            creditGroup?.percentMax.toString() + " %",
            creditGroup?.time_max
        )

        val hints = arrayOf("Название кредита", "Сумма", "Процент", "Срок кредита")
        for (i in names.indices) {
            addView(hints[i], names[i].toString())
        }

    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.setText(value)
        binding.linMain.addView(viewDepositCreateBinding.root)
    }
}