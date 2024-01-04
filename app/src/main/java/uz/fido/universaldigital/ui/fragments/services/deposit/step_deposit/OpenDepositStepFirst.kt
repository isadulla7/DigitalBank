package uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDepositStepFirstBinding
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.dialog.CalculatorDialog
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class OpenDepositStepFirst : BaseFragment<FragmentDepositStepFirstBinding, MainDepositViewModel>
    (FragmentDepositStepFirstBinding::inflate, MainDepositViewModel::class.java), (String) -> Unit {

    private lateinit var deposit: Deposit
    private var operation: String? = null
    private lateinit var dialog: CalculatorDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            operation = it.getString("operation")
            deposit = it.getSerializable("deposit") as Deposit
        }
        init()
    }

    fun init() {
        setTextView()
        textWatchers()
        onclickView()
        percentCurrent()
    }

    private fun percentCurrent() {
        if (deposit.percent == "0") {
            binding.etAmount.setText("0")
            binding.amountLin.visibility = View.GONE
            binding.maxAmount.visibility = View.GONE
            binding.appBar.setAdditionalBtnVisibility(false)
        } else {
            binding.appBar.setAdditionalBtnVisibility(true)

        }
    }

    private fun onclickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            deposit.pay_to_card = "Y"
            val amount = binding.etAmount.editableText.toString().replace(" ", "")
            val type = requireArguments().getBoolean("isSum")

            goto(
                R.id.openDepositStepTwoFragment, bundleOf(
                    "deposit" to deposit,
                    "amount" to amount,
                    "isSum" to type
                )
            )
        }
        binding.appBar.setOnAdditionalBtnClickListener {
            dialog = CalculatorDialog(
                this,
                deposit.min_sum.toString()
            )
            dialog.show(childFragmentManager, "")
        }
    }

    private fun textWatchers() {
        binding.etAmount.addTextChangedListener { text ->
            if (deposit.percent != "0") {
                if (text!!.isEmpty()) {
                    binding.btnContinue.isEnabled(false)
                } else if (text.toString().replace(" ", "")
                        .toDouble() >= Format.formatAmountFromTiynToInteger(deposit.min_sum.toString())
                        .toDouble()
                ) {
                    binding.btnContinue.isEnabled(true)
                } else binding.btnContinue.isEnabled(false)
            } else binding.btnContinue.isEnabled(true)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setTextView() {
        binding.appBar.setTitle(deposit.dep_name)
        binding.percentText.text = "${getString(R.string.percent_text)} ${deposit.percent} %"
        binding.minAmount.text = getString(R.string.min_summa) + " " +
                Format.formatAmount(Format.formatAmountFromTiynToInteger(deposit.min_sum.toString())) + " " + Format().getCurrencyChar(
            deposit.currency_code
        )
        binding.tvTime.text =
            getString(R.string.time_deposit) + " " + Format().formattedDepositExpire(
                requireContext(),
                deposit.keeping_time
            )
    }

    override fun invoke(amount: String) {
        gotoWithSlide(
            R.id.depositCalculatorResultFragment,
            bundleOf("dep_id" to deposit.dep_id, "amount" to amount)
        )
        dialog.dismiss()
    }
}