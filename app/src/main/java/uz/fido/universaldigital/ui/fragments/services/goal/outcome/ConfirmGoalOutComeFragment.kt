package uz.fido.universaldigital.ui.fragments.services.goal.outcome

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PInfoResponse
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOutcomeCofirmBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format.Companion.sendFormat
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ConfirmGoalOutComeFragment : BaseFragment<FragmentOutcomeCofirmBinding, GoalViewModel>(
    FragmentOutcomeCofirmBinding::inflate, GoalViewModel::class.java
) {
    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()
    private lateinit var selectedCard: CardResponse
    private lateinit var goalModel: GoalModel
    private lateinit var p2PRequest: P2PRequest
    private var percent = "0"
    var current = false
    var amount = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goalModel = arguments?.serializable<GoalModel>("goal") as GoalModel
        p2PRequest = arguments?.serializable<P2PRequest>("p2p_request") as P2PRequest
        init()
        initCards()
        setonClick()
        binding.btnContinue.isEnabled(true)
    }

    private fun setonClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            val p2PRequest = P2PRequest(
                command = if (selectedCard.object_type == WALLET) "purse&purse" else "purse&card",
                amount = sendFormat(amount),
                from_object_value = goalModel.fund_object_value,
                from_object_id = "",
                from_object_expire = "",
                service_id = "-12",
                to_object_value = selectedCard.object_value,
                to_object_expire = selectedCard.object_expiry,
                target = "Y",
                target_id = goalModel.target_id
            )
            binding.btnContinue.setProgress(true)
            viewModel.targetTransfer(getClientToken(), p2PRequest).observe(viewLifecycleOwner) {
                binding.btnContinue.setProgress(false)
                when (it.status) {
                    Status.SUCCESS -> {

                        goto(
                            R.id.basicSuccessFragment, bundleOf(
                                Const.OPERATION to BasicSuccessFragment.GOAL_INCOME,
                                Const.OPERATION_AMOUNT to BasicSuccessFragment.GOAL_INCOME
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

    fun init() {
        amount = p2PRequest.amount
        binding.name.text = goalModel.aim_desc
        binding.appBar.setTitle(goalModel.aim_desc)
        binding.totalAmount.text =
            uz.fido.utils.utility.format.Format.formatAmount((amount.toDouble() + percent.toDouble() * amount.toDouble() / 100).toString())
        binding.amount.text =
            uz.fido.utils.utility.format.Format.formatAmount((amount.toDouble()).toString())
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, "0",
                CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    p2pInfoRequest()
                    binding.btnContinue.isEnabled(true)
                }
            }
        }
    }

    private fun p2pInfoRequest() {
        val p2pINfoRequest = P2PInfoRequest(
            command = if (goalModel.fund_object_value.startsWith("AUZ") || goalModel.fund_object_value.startsWith(
                    "DV"
                )
            ) "info&purse" else "info&card",
            from_object_id = selectedCard.object_id,
            expire = selectedCard.object_expiry,
            service_id = if (goalModel.fund_object_value.startsWith("AUZ") || selectedCard.object_type == WALLET) "-12" else "-1",
            to_object_value = goalModel.fund_object_value
        )

        showProgress()
        viewModel.p2pInfoRequest(getClientToken(), p2pINfoRequest).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data as P2PInfoResponse
                    Const.request_id = response.request_id
                    percent = response.percent
                    current = true
                }

                Status.ERROR -> {
                    current = false
                    binding.btnContinue.isEnabled(false)
                }
            }
        }
    }

}