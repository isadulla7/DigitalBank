package uz.fido.universaldigital.ui.fragments.services.goal.outcome

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentGoalOutcomeBinding
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class GoalOutComeFragment : BaseFragment<FragmentGoalOutcomeBinding, GoalViewModel>(
    FragmentGoalOutcomeBinding::inflate, GoalViewModel::class.java
) {
    private lateinit var goalModel: GoalModel
    private var receiverCardNumber: String = ""
    private var senderCardBalance = 0.0
    private var amount: String = "0"
    private var percent = "0"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goalModel = arguments?.serializable<GoalModel>("goal") as GoalModel
        init()
        initAmountTextChangeListener()
        setOnClick()
    }

    private fun setOnClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (binding.etAmount.editableText.toString() != "") {
                binding.btnContinue.isEnabled(true)
                val p2pRequest = P2PRequest(
                    command = "",
                    amount = amount,
                    from_object_value = goalModel.fund_object_value,
                    from_object_id = "",
                    from_object_expire = "",
                    service_id = "-12",
                    to_object_value = "",
                    to_object_expire = "",
                    target = "Y",
                    target_id = goalModel.target_id
                )
                goto(
                    R.id.confirmGoalOutComeFragment, bundleOf(
                        "p2p_request" to p2pRequest,
                        "goal" to goalModel
                    )
                )
            }
        }
    }

    fun init() {
        binding.appBar.setTitle(goalModel.aim_desc)
        receiverCardNumber = goalModel.fund_object_value
        senderCardBalance = goalModel.amount.toDouble()

        binding.amount.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(goalModel.current_amount)) + " UZS"
    }

    private fun initAmountTextChangeListener() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                amount = binding.etAmount.editableText.toString().replace(" ", "").replace(",", ".")
                if (senderCardBalance >= 500) {
                    if (amount.isNotEmpty()) {
                        val isCorrectAmount =
                            amount.toDouble() + percent.toDouble() * amount.toDouble() / 100 < senderCardBalance || amount.toDouble() + percent.toDouble() * amount.toDouble() / 100 == senderCardBalance
                        if (amount.toDouble() > 500 || amount.toDouble() == 500.0) {
                            if (!isCorrectAmount) {
                                binding.commission.text = getString(R.string.insufficient)
                                binding.btnContinue.isEnabled(false)
                            } else {
                                binding.commission.text =
                                    getString(R.string.commission2) + " " + Format.formatAmount(
                                        amount.toBigDecimal().multiply(percent.toBigDecimal())
                                            .divide(100.toBigDecimal()).toString()
                                    ) + " UZS"

                                binding.btnContinue.isEnabled(true)
                            }
                        } else {
                            binding.commission.text = getString(R.string.min_amount_500)
                            binding.btnContinue.isEnabled(false)
                        }
                    } else binding.btnContinue.isEnabled(false)
                } else binding.btnContinue.isEnabled(false)
            }
        })
    }

}