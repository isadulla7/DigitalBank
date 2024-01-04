package uz.fido.universaldigital.ui.fragments.services.goal.operation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDeleteGoalBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class DeleteGoalFragment : BaseFragment<FragmentDeleteGoalBinding, GoalViewModel>(
    FragmentDeleteGoalBinding::inflate, GoalViewModel::class.java
), View.OnClickListener {

    private lateinit var goalModel: GoalModel
    private lateinit var selectedCard: CardResponse

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            goalModel = it.serializable<GoalModel>("goal") as GoalModel
        }
        setText()
        onCLick()
        initCards()
    }

    private fun onCLick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnEnter.setOnClickListener(this)
    }

    private fun setText() {
        binding.name.text = goalModel.aim_desc
        binding.amount.text = Format.formatAmountFromTiynToInteger(goalModel.current_amount)
    }

    override fun onClick(p0: View?) {
        showProgress()
        val p2pRequest = P2PRequest(
            command = "purse&card",
            amount = goalModel.current_amount,
            from_object_value = goalModel.fund_object_value,
            from_object_expire = "",
            from_object_id = "",
            service_id = "-12",
            to_object_value = selectedCard.object_value,
            to_object_expire = selectedCard.object_expiry,
            target = "",
            target_id = goalModel.target_id
        )
        viewModel.closeTarget(getClientToken(), p2pRequest).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    goto(
                        R.id.basicSuccessFragment, bundleOf(
                            Const.OPERATION to BasicSuccessFragment.CREATE_GOAL,
                            Const.OPERATION_AMOUNT to goalModel.current_amount.toString()
                        )
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, "0",
                CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card

                }
            }
        }
    }
}