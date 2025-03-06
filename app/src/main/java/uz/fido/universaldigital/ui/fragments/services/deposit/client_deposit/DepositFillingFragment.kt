package uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.deposits.operations.EarlyClosureRequest
import uz.fido.network.domain.model.deposits.operations.InvestMoneyToDepositRequest
import uz.fido.network.domain.model.deposits.operations.PartialWithdrawMoneyDepositRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDepositFillingBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class DepositFillingFragment : BaseFragment<FragmentDepositFillingBinding, ClientDepositViewModel>(
    FragmentDepositFillingBinding::inflate, ClientDepositViewModel::class.java
) {

    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()
    private lateinit var chosenCard: CardResponse
    private lateinit var deposit: ClientDeposit
    private var operation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            deposit = it.serializable<ClientDeposit>("deposit") as ClientDeposit
            operation = it.serializable<String>(Const.OPERATION) as String
        }
        setFragmentResultListener(ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY) { _, bundle ->
            val stringLine = bundle.getString("string_line").orEmpty()
            if (operation == ClientDepositFragment.TOP_UP_DEPOSIT) {
                investMoney(stringLine)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCards()
        textWatchers()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            when (operation) {
                ClientDepositFragment.TOP_UP_DEPOSIT -> smsCheck()
                ClientDepositFragment.EARLY_CLOSE_DEPOSIT -> earlyCloseDeposit()
                ClientDepositFragment.CLOSE_DEPOSIT -> closeDeposit()
                ClientDepositFragment.WITH_DRAW_PERCENT -> withDrawPercent()
            }
        }
        binding.createMobileDv.setOnClickListener {
            showProgress()
            getDepositProducts()
        }
    }

    private fun withDrawPercent() {
        val amount = binding.etAmount.editableText.toString().replace(" ", "")
        binding.btnContinue.setProgress(false)
        viewModel.partialWithDraw(
            getClientToken(),
            PartialWithdrawMoneyDepositRequest(
                command = if (chosenCard.object_type == WALLET) "dep&purse" else "dep&card",
                savDepId = deposit.savDepId.orEmpty(),
                amount = Format.formatAmountToTiyn(amount),
                to_object_value = chosenCard.object_value,
                to_object_id = chosenCard.object_id,
                service_id = "-7",
                to_object_expire = chosenCard.object_expiry
            )
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    getClientDeposits(amount)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun earlyCloseDeposit() {
        if (this::chosenCard.isInitialized) {
            try {
                val amount = deposit.sumDep
                binding.btnContinue.setProgress(true)
                viewModel.earlyCloseDeposit(
                    getClientToken(),
                    EarlyClosureRequest(
                        command = if (chosenCard.object_type == WALLET) "dep&purse" else "dep&card",
                        to_object_value = chosenCard.object_value,
                        to_object_id = chosenCard.object_id,
                        to_object_expire = chosenCard.object_expiry,
                        savDepId = deposit.savDepId.orEmpty(),
                        credit_amount = (amount ?: "0").replace(",", "."),
                        client_id = getClientId(),
                        service_id = "-8",
                        status = deposit.status.orEmpty(),
                        closing_date = deposit.closingDate.orEmpty()
                    )
                ).observe(viewLifecycleOwner) {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            getClientDeposits("0")
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                recordException(e, ::initCards.name)
            }
        }
    }

    private fun closeDeposit() {
        if (this::chosenCard.isInitialized) {
            try {
                val amount = deposit.sumDep
                binding.btnContinue.setProgress(true)
                viewModel.closeDeposit(
                    getClientToken(),
                    EarlyClosureRequest(
                        command = if (chosenCard.object_type == WALLET) "dep&purse" else "dep&card",
                        to_object_value = chosenCard.object_value,
                        to_object_id = chosenCard.object_id,
                        to_object_expire = chosenCard.object_expiry,
                        savDepId = deposit.savDepId.orEmpty(),
                        credit_amount = (amount ?: "0").replace(",", "."),
                        client_id = getClientId(),
                        service_id = "-8",
                        status = deposit.status.orEmpty(),
                        closing_date = deposit.closingDate.orEmpty()
                    )
                ).observe(viewLifecycleOwner) {
                    binding.btnContinue.setProgress(false)
                    when (it.status) {
                        Status.SUCCESS -> {
                            getClientDeposits("0")
                        }

                        Status.ERROR -> {
                            showSnackbar(it.message.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                recordException(e, ::initCards.name)
            }
        }
    }

    private fun smsCheck() {
        val amount = binding.etAmount.editableText.toString().replace(" ", "")
        binding.btnContinue.setProgress(true)
        if (!checkForPaymentSms(chosenCard, "-1", Format.formatAmountToTiyn(amount))) {
            investMoney()
        } else {
            checkForSms(
                chosenCard,
                Format.formatAmountToTiyn(amount),
                "-6"
            ) { sms_yes, string_line ->
                if (sms_yes == "Y") {
                    goto(
                        R.id.confirmSmsFragment, bundleOf(Const.OPERATION to ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY)
                    )
                } else {
                    investMoney()
                }
            }
        }
    }

    private fun investMoney(stringLine: String? = "") {
        val amount = binding.etAmount.editableText.toString().replace(" ", "")
        val model = InvestMoneyToDepositRequest(
            command = if (chosenCard.object_type == WALLET) "purse&dep" else "card&dep",
            savDepId = deposit.savDepId.orEmpty(),
            amount = Format.formatAmountToTiyn(amount),
            from_object_id = chosenCard.object_id,
            service_id = "-6",
            string_line = stringLine
        )
        viewModel.investMoney(getClientToken(), model).observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled(false)
            when (it.status) {
                Status.SUCCESS -> {
                    getClientDeposits(amount)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun textWatchers() {
        when (operation) {
            ClientDepositFragment.TOP_UP_DEPOSIT -> setAmount()
            ClientDepositFragment.EARLY_CLOSE_DEPOSIT -> removeDeposit()
            ClientDepositFragment.CLOSE_DEPOSIT -> close()
            ClientDepositFragment.WITH_DRAW_PERCENT -> percentWithDraw()
        }
    }

    private fun percentWithDraw() {
        binding.appBar.setTitle(getString(R.string.percents_withdraw))
        binding.etAmount.addTextChangedListener { s ->
            deposit.interestPayable?.let {
                val balanceTiyn = it.replace(" ", "").toBigDecimal()
                val amountTiyn =
                    Format.formatAmountToTiyn(s.toString().replace(" ", "")).toBigDecimal()
                binding.btnContinue.isEnabled(balanceTiyn >= amountTiyn)
            }
        }
    }

    private fun removeDeposit() {
        binding.appBar.setTitle(getString(R.string.early_closing_of_the_deposit))
        binding.info.visibility = View.VISIBLE
        binding.etAmount.isLongClickable = false
        binding.etAmount.isFocusable = false
        binding.etAmount.setText(Format.formatAmount(((deposit.sumDep ?: "0").toDouble() / 100).toString()))
        binding.btnContinue.isEnabled(true)
    }

    private fun close() {
        binding.appBar.setTitle(getString(R.string.close_deposit))
        binding.info.visibility = View.VISIBLE
        binding.etAmount.isLongClickable = false
        binding.etAmount.isFocusable = false
        binding.etAmount.setText(Format.formatAmount(((deposit.sumDep ?: "0").toDouble() / 100).toString()))
        binding.btnContinue.isEnabled(true)
    }

    private fun setAmount() {
        binding.appBar.setTitle(getString(R.string.top_up))
        checkItem(binding.etAmount.text.toString().replace(" ", ""))
        binding.etAmount.addTextChangedListener {
            val amount = it.toString().replace(" ", "")
            checkItem(amount)
        }
    }

    private fun checkItem(amount: String) {
        if (amount != "" && this::chosenCard.isInitialized) {
            if (chosenCard.balance.toBigDecimal() > amount.toBigDecimal()) {
                binding.btnContinue.isEnabled(true)
            } else {
                binding.btnContinue.isEnabled(false)
            }
        } else binding.btnContinue.isEnabled(false)
    }

    private fun initCards() {
        var minAmount = ""
        when (operation) {
            ClientDepositFragment.TOP_UP_DEPOSIT -> minAmount = "100"
            ClientDepositFragment.EARLY_CLOSE_DEPOSIT, ClientDepositFragment.CLOSE_DEPOSIT -> minAmount = "0"
            ClientDepositFragment.WITH_DRAW_PERCENT -> minAmount = "0"
        }
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            if (operation == ClientDepositFragment.EARLY_CLOSE_DEPOSIT || operation == ClientDepositFragment.CLOSE_DEPOSIT) {
                binding.chooseCardLayout.getUniversalDvCards(
                    it as ArrayList<CardResponse>, minAmount
                ) { cardResponse ->
                    if (cardResponse != null) {
                        chosenCard = cardResponse
                        textWatchers()
                    } else {
                        binding.createMobileDv.visibility = View.VISIBLE
                        binding.chooseCardLayout.visibility = View.GONE
                    }
                }
            } else {
                binding.chooseCardLayout.getUniversalCards(
                    it as ArrayList<CardResponse>, minAmount
                ) { cardResponse ->
                    cardResponse?.let { card ->
                        chosenCard = cardResponse
                        textWatchers()
                    }
                }
            }
        }
    }

    private fun getClientDeposits(amount: String) {
        viewModel.getClientDepositList(getClientToken()).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.data != null) {
                        menuProductsViewModel.updateClientDepositList(it.data!!.data)
                        when (operation) {
                            ClientDepositFragment.TOP_UP_DEPOSIT -> {
                                goto(
                                    R.id.basicSuccessFragment, bundleOf(
                                        Const.OPERATION to BasicSuccessFragment.DEPOSIT_FILLING,
                                        Const.OPERATION_AMOUNT to amount
                                    )
                                )
                            }

                            ClientDepositFragment.EARLY_CLOSE_DEPOSIT, ClientDepositFragment.CLOSE_DEPOSIT -> {
                                goto(
                                    R.id.basicSuccessFragment,
                                    bundleOf(Const.OPERATION to BasicSuccessFragment.DEPOSIT_CLOSE)
                                )
                            }

                            ClientDepositFragment.WITH_DRAW_PERCENT -> {
                                goto(
                                    R.id.basicSuccessFragment, bundleOf(
                                        Const.OPERATION to BasicSuccessFragment.DEPOSIT_WITH_DRAW_PERCENT,
                                        Const.OPERATION_AMOUNT to amount
                                    )
                                )
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getDepositProducts() {
        menuProductsViewModel.getDeposits(getClientToken(), GetDepositListRequest("dep"))
            .observe(viewLifecycleOwner) { resource ->
                hideProgress()
                when (resource.status) {
                    Status.SUCCESS -> {
                        menuProductsViewModel.updateDepositProducts(
                            resource.data?.deposit_types ?: ArrayList()
                        )
                        resource.data?.deposit_types?.let { deposits ->
                            val mobileDv = deposits.find { it.dep_id == 853 }
                            if (mobileDv != null) {
                                gotoWithSlide(
                                    R.id.openDepositStepFirst, bundleOf(
                                        "deposit" to mobileDv,
                                        "operation" to "deposit",
                                        "isSum" to true
                                    )
                                )
                            } else {
                                goto(R.id.uzsDepositFragment)
                            }
                        }
                    }

                    Status.ERROR -> {
                        goto(R.id.uzsDepositFragment)
                    }
                }
            }
    }

}