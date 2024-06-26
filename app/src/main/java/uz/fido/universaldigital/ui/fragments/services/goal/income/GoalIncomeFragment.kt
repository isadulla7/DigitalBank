package uz.fido.universaldigital.ui.fragments.services.goal.income

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PInfoResponse
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.target.ChangeTargetStateRequest
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.network.domain.model.target.GoalModelResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentGoalIncomeBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.utility.format.Format.Companion.sendFormat
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.view.amount.AmountSuggestionView

@AndroidEntryPoint
class GoalIncomeFragment : BaseFragment<FragmentGoalIncomeBinding, GoalViewModel>(
    FragmentGoalIncomeBinding::inflate, GoalViewModel::class.java
) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var senderCard: CardResponse? = null
    private var fromConfirmPage = false
    private lateinit var goalModel: GoalModel
    private var minAmount = 500.0
    private var percent = "0"
    private var currentAmount = "0"
    private var targetAmount = "0"
    private var maxAmount = 50000000.0
    private var goalModelResponse: GoalModelResponse? = null
    private var receiverCardNumber: String = ""
    private var etAmount: String = "0"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goalModel = arguments?.serializable<GoalModel>("goal") as GoalModel
        initCardList {
            initSenderCards()
            if (!fromConfirmPage) {
                try {
                    senderCard = userSumCards.first()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        initSuggestions()
        initAmountTextWatcher()
        getGoalDetails()
        setOnClick()
    }

    private fun setOnClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (binding.etAmount.editableText.toString() != "") {
                binding.btnContinue.setProgress(true)
                val p2pRequest = P2PRequest(
                    command = if (senderCard?.object_type == WALLET) "purse&purse" else "card&purse",
                    amount = sendFormat(etAmount),
                    from_object_id = senderCard!!.object_id,
                    from_object_expire = senderCard!!.object_expiry,
                    service_id = "-12",
                    to_object_value = goalModel.fund_object_value,
                    to_object_expire = "",
                    target = "Y",
                    target_id = goalModel.target_id
                )
                viewModel.targetTransfer(getClientToken(), p2pRequest).observe(viewLifecycleOwner) {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            gotoWithSlide(
                                R.id.basicSuccessFragment, bundleOf(
                                    Const.OPERATION to BasicSuccessFragment.GOAL_INCOME,
                                    Const.OPERATION_AMOUNT to etAmount
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
    }

    private fun getGoalDetails() {
        showProgress()
        viewModel.getTargetInfo(getClientToken(), ChangeTargetStateRequest(goalModel.target_id))
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        goalModelResponse = it.data as GoalModelResponse
                        receiverCardNumber = goalModelResponse!!.fund_object_value
                        currentAmount = goalModelResponse!!.current_amount
                        targetAmount = goalModelResponse!!.target_amount
                        p2pInfoRequest()
                    }

                    Status.ERROR -> {
                        binding.btnContinue.isEnabled(false)
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun p2pInfoRequest() {
        val p2pINfoRequest = P2PInfoRequest(
            command = if (receiverCardNumber.startsWith("AUZ") || receiverCardNumber.startsWith("DV")) "info&purse" else "info&card",
            from_object_id = senderCard!!.object_id,
            expire = senderCard!!.object_expiry,
            service_id = if (receiverCardNumber.startsWith("AUZ") || receiverCardNumber.startsWith("DV") || senderCard!!.object_type == WALLET) "-12" else "-1",
            to_object_value = receiverCardNumber
        )
        viewModel.p2pInfoRequest(getClientToken(), p2pINfoRequest).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data as P2PInfoResponse
                        Const.request_id = response.request_id
                        percent = response.percent
                    }

                    Status.ERROR -> {
                        binding.btnContinue.isEnabled(false)
                    }
                }
            }
        }
    }


    private fun initCardList(listener: () -> Unit) {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            cardList.forEach {
                if (it.currency_code == CurrencyConst.CURRENCY_CODE_UZS) {
                    if (senderCard == null) {
                        userSumCards.add(it)
                    }
                }
            }
            listener.invoke()
        }
    }

    private fun initSenderCards() {
        val senderCardsAdapter = OverMyCardsAdapter(requireContext(), userSumCards)
        binding.senderCards.adapter = senderCardsAdapter
        binding.senderIndicator.setViewPager(binding.senderCards)
        binding.senderCards.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                vibrateTick(requireContext())
                senderCard = userSumCards[position]
                binding.btnContinue.isEnabled(continueButtonState())
                p2pInfoRequest()
            }
        })
    }

    private fun initSuggestions() {
        binding.amountSuggestions.initAmountSuggestions(
            AmountSuggestionView.AmountType.AMOUNT_TYPE_P2P, binding.etAmount
        )
    }

    private fun initAmountTextWatcher() {
        binding.etAmount.doAfterTextChanged {
            binding.btnContinue.isEnabled(continueButtonState())
        }
    }

    private fun continueButtonState(): Boolean {
        etAmount = binding.etAmount.editableText.toString().replace(" ", "").ifEmpty { "0" }
        val formattedAmount = etAmount.toDouble()
        val totalAmount = formattedAmount + (formattedAmount / 100) * percent.toDouble()
        when {
            senderCard == null -> return false
            senderCard!!.state == CardConst.STATE_PASSIVE -> return false
            formattedAmount < minAmount -> {
                binding.tvMinAmount.text =
                    getString(R.string.min_amount) + " $minAmount ${getString(R.string.sum_text)}"
                return false
            }

            formattedAmount > maxAmount -> {
                binding.tvMinAmount.text =
                    getString(R.string.max_amount) + " $maxAmount ${getString(R.string.sum_text)}"
                return false
            }

            totalAmount > senderCard!!.balance.toDouble() / 100 -> {
                binding.tvMinAmount.text = getString(R.string.insufficient_amount)
                return false
            }

            totalAmount > minAmount && totalAmount < senderCard!!.balance.toDouble() / 100 -> {
                binding.tvMinAmount.text =
                    getString(R.string.min_amount) + " $minAmount ${getString(R.string.sum_text)}"
                return true
            }

            receiverCardNumber == senderCard?.object_value -> {
                return false
            }

            receiverCardNumber.isEmpty() -> {
                return false
            }

            totalAmount > ((targetAmount.toDouble() - currentAmount.toDouble()) / 100) -> return false

            else -> return true
        }
    }


}