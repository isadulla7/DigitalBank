package uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSuccessBasicBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.add_card.AddCardFragment
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BasicSuccessFragment : BaseFragment<FragmentSuccessBasicBinding, MainDepositViewModel>(
    FragmentSuccessBasicBinding::inflate, MainDepositViewModel::class.java
) {

    val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    private var operation = ""

    companion object {
        const val DEPOSIT_OPEN = "deposit_open"
        const val ADD_CARD = "add_card"
        const val ORDER_VIRTUAL_CARD = "virtual_card"
        const val LOCAL_PAYMENT = "local_payment"
        const val SAVE_MY_HOME = "save_my_home"
        const val HOME_ID = "home_id"
        const val HOME_NAME = "home_name"
        const val LIMIT = "limit"
        const val AUTO_PAYMENT_CREATED = "auto_payment"
        const val OPEN_WALLET_SUCCESS = "OPEN_WALLET_SUCCESS"
        const val SWIFT_SUCCESS = "SWIFT_SUCCESS"
        const val ORDER_CARD = "ORDER_CARD"
        const val MONEY_TRANSFER = "MONEY_TRANSFER"
        const val SAVE_TEMPLATE = "SAVE_TEMPLATE"
        const val DEPOSIT_EDIT_NAME = "deposit_name_edit"
        const val DEPOSIT_FILLING = "deposit_filling"
        const val DEPOSIT_CLOSE = "deposit_close"
        const val CREATE_GOAL = "create_goal"
        const val EDIT_GOAL = "edit_goal"
        const val GOAL_INCOME = "goal_income"
        const val MY_HOME_PAYMENT_LIST = "my_home_payment_list"
        const val DEPOSIT_WITH_DRAW_PERCENT = "deposit_with_draw_percent"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (operation) {
                    LOCAL_PAYMENT -> {
                        gotoMainPage()
                    }

                    SAVE_MY_HOME -> {
                        gotoMainPage()
                    }

                    AUTO_PAYMENT_CREATED -> {
                        gotoMainPage()
                    }

                    DEPOSIT_EDIT_NAME, DEPOSIT_FILLING, DEPOSIT_CLOSE, DEPOSIT_WITH_DRAW_PERCENT -> {
                        gotoMainPage()
                    }

                    CREATE_GOAL, GOAL_INCOME, EDIT_GOAL -> {
                        gotoMainPage()
                    }

                    MY_HOME_PAYMENT_LIST -> {
                        gotoMainPage()
                    }

                    else -> {
                        gotoMainPage()
                    }

                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            operation = it.getString(Const.OPERATION).toString()
        }
        setToText()
        setOnClickView()
        setOperationTime()
        onBackPressCallback()
    }

    private fun setToText() {
        when (operation) {
            SAVE_MY_HOME, ORDER_VIRTUAL_CARD, SAVE_TEMPLATE -> {
                binding.amount.visibility = View.GONE
                binding.successTitle.text = getString(R.string.successfully)
            }

            "credit_take" -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.visibility = View.GONE
            }

            DEPOSIT_OPEN -> {
                binding.amount.text =
                    "${Format.formatAmount(requireArguments().getString("amount"))} UZS"
                binding.successTitle.text = getString(R.string.open_deposit_success)
            }

            LOCAL_PAYMENT -> {
                binding.amount.text =
                    "${Format.formatAmount(requireArguments().getString("amount"))} UZS"
                binding.successTitle.text = getString(R.string.payment_completed_successfully)

            }

            ADD_CARD -> {
                arguments?.let {
                    if (it.getString(Const.ADD_CARD_OPERATION) != null) {
                        binding.gotoMainPage.text = getString(R.string.continue_text)
                    }
                }
                binding.successTitle.text = getString(R.string.add_card_successfully)
            }

            LIMIT -> {
                binding.amount.visibility = View.GONE
                binding.successTitle.text = getString(R.string.set_limit_successful)
            }

            OPEN_WALLET_SUCCESS -> {
                binding.amount.visibility = View.GONE
                binding.successTitle.text = getString(R.string.wallet_successful_created)
            }

            AUTO_PAYMENT_CREATED -> {
                binding.successTitle.text = getString(R.string.auto_payment_create)
                binding.amount.visibility = View.GONE
            }

            DEPOSIT_EDIT_NAME -> {
                binding.successTitle.text = getString(R.string.deposit_edit_name)
                binding.amount.visibility = View.GONE
            }

            DEPOSIT_FILLING -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.text =
                    "${Format.formatAmount(requireArguments().getString(Const.OPERATION_AMOUNT))} UZS"
            }

            DEPOSIT_WITH_DRAW_PERCENT -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.text =
                    "${Format.formatAmount(requireArguments().getString(Const.OPERATION_AMOUNT))} UZS"
            }

            DEPOSIT_CLOSE -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.visibility = View.GONE
            }

            CREATE_GOAL, GOAL_INCOME -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.text =
                    "${Format.formatAmount(requireArguments().getString(Const.OPERATION_AMOUNT))} UZS"
            }

            EDIT_GOAL, MY_HOME_PAYMENT_LIST -> {
                binding.successTitle.text = getString(R.string.successfully)
                binding.amount.visibility = View.GONE
            }

            SWIFT_SUCCESS, ORDER_CARD, MONEY_TRANSFER -> {
                binding.lottieAnimation.setAnimation("process_anim.json")
                binding.successTitle.text = getString(R.string.accepted_your_app)
                if (operation == MONEY_TRANSFER || operation == SWIFT_SUCCESS) {
                    binding.amount.text =
                        "${Format.formatAmount(requireArguments().getString(Const.OPERATION_AMOUNT))} UZS"
                } else binding.amount.visibility = View.GONE
            }

        }
    }

    private fun setOnClickView() {
        binding.gotoMainPage.setOnClickListener {
            when (operation) {
                DEPOSIT_OPEN -> {
                    gotoMainPage()
                }

                LOCAL_PAYMENT -> {
                    gotoMainPage()
                }

                ADD_CARD -> {
                    if (requireArguments().getString(Const.ADD_CARD_OPERATION) != null) {
                        showProgress()
                        getCardList()
                    } else {
                        gotoMainPage()
                    }
                }

                SAVE_MY_HOME -> {
                    gotoMainPage()
                }

                LIMIT, ORDER_VIRTUAL_CARD -> {
                    gotoMainPage()
                }

                OPEN_WALLET_SUCCESS -> {
                    gotoMainPage()
                }

                AUTO_PAYMENT_CREATED -> {
                    gotoMainPage()
                }

                SAVE_TEMPLATE -> {
                    gotoMainPage()
                }

                SWIFT_SUCCESS -> {
                    gotoMainPage()
                }

                CREATE_GOAL, GOAL_INCOME, EDIT_GOAL -> {
                    gotoMainPage()
                }

                MY_HOME_PAYMENT_LIST -> {
                    gotoMainPage()
                }

                DEPOSIT_EDIT_NAME, DEPOSIT_FILLING, DEPOSIT_CLOSE -> {
                    goto(R.id.action_basicSuccessFragment_to_clientDepositListFragment)
                }

                "credit_take" -> {
                    gotoMainPage()
                }
            }
        }
    }

    private fun setOperationTime() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.US)
        val now = Calendar.getInstance().time
        binding.date.text = dateFormat.format(now)
    }

    private fun gotoMainPage() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun getCardList() {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            it?.let {
                if (it.status == Status.SUCCESS) {
                    it.data?.let { cardListResponse ->
                        if (cardListResponse.objects != null) {
                            val cards = cardListResponse.objects
                            menuProductsViewModel.updateCards(cards!!)
                            for (i in 0 until cards.size) {
                                getCardInfo(i, cards)
                            }
                        } else {
                            menuProductsViewModel.updateCards(ArrayList())
                        }
                    }
                }
            }
        }
    }

    private fun getCardInfo(
        position: Int, cardList: ArrayList<CardResponse>
    ) {
        val ids = arrayListOf(cardList[position].object_id)
        val cardInfoRequest = CardInfoRequest(ids)
        menuProductsViewModel.getCardInfoRequest(getClientToken(), cardInfoRequest)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data!!.objects
                        if (response != null && response.size > 0) {
                            cardList[position].apply {
                                balance = response[0].balance
                                processing_server_status =
                                    response[0].state.toString()
                                stateName = response[0].state_name
                                owerdraft_limit = response[0].overdraft_limit
                                pin_counter = response[0].pin_counter
                                overdraft_limit = response[0].overdraft_limit
                                object_status = response[0].object_status
                            }
                            menuProductsViewModel.updateCards(cardList)
                            if (position == cardList.size - 1) {
                                hideProgress()
                                when (requireArguments().getString(Const.ADD_CARD_OPERATION)) {
                                    AddCardFragment.OPERATION_CARD_TO_CARD -> {
                                        goto(R.id.action_basicSuccessFragment_to_transferToCardFragment)
                                    }

                                    AddCardFragment.OPERATION_BY_WALLET -> {
                                        goto(R.id.action_basicSuccessFragment_to_transferByWalletFragment)
                                    }

                                    AddCardFragment.OPERATION_BY_PHONE -> {
                                        goto(R.id.action_basicSuccessFragment_to_transferByPhoneFragment)
                                    }

                                    AddCardFragment.OPERATION_OVER_MY_CARDS -> {
                                        goto(R.id.action_basicSuccessFragment_to_overMyCardsFragment)
                                    }

                                    else -> {
                                        gotoMainPage()
                                    }
                                }
                            }
                        } else {
                            showSnackbar(it.data.toString())
                        }
                    }

                    Status.ERROR -> {
                        cardList[position].apply {
                            processing_server_status = "-100"
                        }
                        menuProductsViewModel.updateCards(cardList)
                    }
                }
            }
    }

    private fun onBackPressCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gotoMainPage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}

