package uz.fido.universaldigital.ui.fragments.services.goal.new_goal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.target.EditGoalRequest
import uz.fido.network.domain.model.target.SetTargetRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentConfirmGoalBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal

@AndroidEntryPoint
class ConfirmGoalFragment : BaseFragment<FragmentConfirmGoalBinding, GoalViewModel>(
    FragmentConfirmGoalBinding::inflate, GoalViewModel::class.java
) {

    private lateinit var targetRequest: SetTargetRequest
    private lateinit var selectedCard: CardResponse
    private lateinit var operation: String

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()
    private val add_num: BigDecimal = BigDecimal("100.0")

    private var isOfferta = false
    private var editGoalRequest: EditGoalRequest? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            operation = it.getString(Const.OPERATION).toString()
            if (operation == "create") {
                targetRequest = arguments?.serializable<SetTargetRequest>("target") as SetTargetRequest
                initCards()
                setTextTarget()

            } else {
                binding.btnContinue.isEnabled(true)
                editGoalRequest = it.serializable<EditGoalRequest>("model") as EditGoalRequest
                setTextEdit()
                editInitCatd()

            }
        }


        onCLick()
    }


    private fun setTextEdit() {
        binding.name.text = editGoalRequest?.aim_desc
        binding.termGoal.text = requireArguments().getString("time")
        binding.startAmount.visibility = View.GONE
        binding.fireprool.text =
            (editGoalRequest!!.decreasing_amount.toBigDecimal() / add_num).toString()
        binding.writeOffs.text = requireArguments().getString("week_day")
    }

    private fun onCLick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.iconOkay.setOnClickListener {
            okayAutoPayment()
        }
        binding.btnContinue.setOnClickListener {
            if (isOfferta) checkEdittoCreate()
        }
    }

    private fun checkEdittoCreate() {
        if (operation == "create") {
            setTargetRequest()
        } else {
            editGoalRequestItem()
        }
    }

    private fun editGoalRequestItem() {
        getCardListEdit()
        binding.btnContinue.setProgress(true)
        viewModel.editGoal(getClientToken(), editGoalRequest!!).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    goto(
                        R.id.basicSuccessFragment, bundleOf(
                            Const.OPERATION to BasicSuccessFragment.EDIT_GOAL
                        )
                    )

                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getCardListEdit() {
        val array = arrayListOf<String>()
        array.add(selectedCard.object_id)
        editGoalRequest!!.from_objects = array
    }

    private fun setTargetRequest() {
        getCardList()
        binding.btnContinue.setProgress(true)
        viewModel.setTarget(getClientToken(), targetRequest).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    goto(
                        R.id.basicSuccessFragment, bundleOf(
                            Const.OPERATION to BasicSuccessFragment.CREATE_GOAL,
                            Const.OPERATION_AMOUNT to targetRequest.start_amount
                        )
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getCardList() {
        val array = arrayListOf<String>()
        array.add(selectedCard.object_id)
        targetRequest.from_objects = array
    }

    private fun okayAutoPayment() {
        if (!isOfferta) {
            binding.iconOkay.setImageResource(R.drawable.check_construktor)
            isOfferta = true

        } else {
            binding.iconOkay.setImageResource(R.drawable.check_box_color)
            isOfferta = false
        }
    }

    private fun setTextTarget() {
        binding.name.text = targetRequest.aim_desc
        binding.termGoal.text = requireArguments().getString("time")
        binding.startAmount.text = (targetRequest.start_amount.toBigDecimal() / add_num).toString()
        binding.fireprool.text =
            (targetRequest.decreasing_amount.toBigDecimal() / add_num).toString()
        binding.writeOffs.text = requireArguments().getString("week_day")
    }

    private fun editInitCatd() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, "0", CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                }
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>,
                (targetRequest.start_amount.toBigDecimal() / add_num).toString(),
                CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    if (card.balance.toBigDecimal() >= targetRequest.start_amount.toBigDecimal()
                    ) {
                        binding.btnContinue.isEnabled(true)
                    } else {
                        binding.btnContinue.isEnabled(false)
                    }
                }
            }
        }
    }
}