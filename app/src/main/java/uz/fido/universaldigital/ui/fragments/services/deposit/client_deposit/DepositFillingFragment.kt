package uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.deposits.operations.EarlyClosureRequest
import uz.fido.network.domain.model.deposits.operations.InvestMoneyToDepositRequest
import uz.fido.network.domain.model.deposits.operations.PartialWithdrawMoneyDepositRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDepositFillingBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_UZS
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class DepositFillingFragment : BaseFragment<FragmentDepositFillingBinding, ClientDepositViewModel>(
    FragmentDepositFillingBinding::inflate, ClientDepositViewModel::class.java
) {

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    private lateinit var selectedCard: CardResponse
    private lateinit var deposit: ClientDeposit

    private var depositType: String = ""
    private var operation = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            deposit = it.serializable<ClientDeposit>("deposit") as ClientDeposit
            operation = it.serializable<String>(Const.OPERATION) as String
            depositType = it.serializable<String>("card_type") as String
        }

        initCards()
        textWatchers()
        setOnClick()
    }

    private fun setOnClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            when (operation) {
                "top_up" -> smsCheck()
                "delete" -> closeDeposit()
                "with_draw_percent" -> withDrawPercent()
                "with_draw" -> withDrawPercent()
            }

        }
    }

    private fun withDrawPercent() {
        val amount = binding.etAmount.editableText.toString().replace(" ", "")
        binding.btnContinue.setProgress(false)
        viewModel.partialWithDraw(
            getClientToken(),
            PartialWithdrawMoneyDepositRequest(
                command = if (selectedCard.object_type == WALLET) "dep&purse" else "dep&card",
                savDepId = deposit.savDepId,
                amount = Format.formatAmountToTiyn(amount),
                to_object_value = selectedCard.object_value,
                to_object_id = selectedCard.object_id,
                service_id = "-7",
                to_object_expire = selectedCard.object_expiry
            )
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    getDeposits(amount)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }

    }

    private fun closeDeposit() {
        val amount = deposit.sumDep
        binding.btnContinue.setProgress(true)
        viewModel.closeDeposit(
            getClientToken(),
            EarlyClosureRequest(
                command = if (selectedCard.object_type == WALLET) "dep&purse" else "dep&card",
                to_object_value = selectedCard.object_value,
                to_object_id = selectedCard.object_id,
                to_object_expire = selectedCard.object_expiry,
                savDepId = deposit.savDepId,
                credit_amount = amount.replace(",", "."),
                client_id = getClientId(),
                service_id = "-8",
                status = deposit.status,
                closing_date = deposit.closingDate
            )
        ).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    getDeposits("0")
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun smsCheck() {
        binding.btnContinue.setProgress(true)
        if (!checkForPaymentSms(selectedCard, "-1", Format.formatAmountToTiyn(deposit.amount))) {
            investMoney()
        } else {
            checkForSms(
                selectedCard,
                Format.formatAmountToTiyn(deposit.amount),
                "-6"
            ) { sms_yes, string_line ->
                if (sms_yes == "Y") {

                } else {
                    investMoney()
                }
            }
        }
    }

    private fun investMoney() {
        val amount = binding.etAmount.editableText.toString().replace(" ", "")
        val model = InvestMoneyToDepositRequest(
            command = if (selectedCard.object_type == WALLET) "purse&dep" else "card&dep",
            savDepId = deposit.savDepId,
            amount = Format.formatAmountToTiyn(amount),
            from_object_id = selectedCard.object_id,
            service_id = "-6",
        )
        viewModel.investMoney(
            getClientToken(), model

        ).observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled(false)
            when (it.status) {
                Status.SUCCESS -> {
                    getDeposits(amount)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun textWatchers() {
        when (operation) {
            "top_up" -> setAmount()
            "delete" -> removeDeposit()
            "with_draw_percent" -> percentWithDraw()
            "with_draw" -> withDraw()
        }

    }

    private fun withDraw() {
        binding.appBar.setTitle(getString(R.string.take_off))
        binding.etAmount.addTextChangedListener { s ->
            val balanceTiyn = deposit.sumDep.replace(" ", "").toBigDecimal()
            val amountTiyn = Format.formatAmountToTiyn(s.toString().replace(" ", "")).toBigDecimal()
            binding.btnContinue.isEnabled(balanceTiyn >= amountTiyn)
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
        binding.appBar.setTitle(getString(R.string.close_deposit))
        binding.etAmount.isLongClickable = false
        binding.etAmount.isFocusable = false
        binding.etAmount.setText(Format.formatAmount((deposit.sumDep.toDouble() / 100).toString()))
        binding.btnContinue.isEnabled(true)
    }

    private fun setAmount() {
        checkItem(binding.etAmount.text.toString().replace(" ", ""))
        binding.etAmount.addTextChangedListener {
            val amount = it.toString().replace(" ", "")
            checkItem(amount)
        }
    }

    private fun checkItem(amount: String) {
        if (amount != "") {
            if (selectedCard.balance.toBigDecimal() > amount.toBigDecimal()
            ) {
                binding.btnContinue.isEnabled(true)
            } else {
                binding.btnContinue.isEnabled(false)
            }
        } else
            binding.btnContinue.isEnabled(false)
    }

    private fun initCards() {
        var type = ""
        var min_amount = ""

        when (operation) {
            "top_up" -> min_amount = "100"
            "delete" -> min_amount = "0"
            "with_draw_percent" -> min_amount = "0"
            "with_draw" -> min_amount = "0"
        }
        type = when (depositType) {
            CURRENCY_CODE_UZS -> {
                CurrencyConst.CURRENCY_CHAR_UZS
            }

            else -> {
                CurrencyConst.CURRENCY_CHAR_USD
            }
        }
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.getUniversalCards(
                it as ArrayList<CardResponse>, min_amount, type
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = cardResponse
                    textWatchers()
                }
            }
        }
    }

    private fun getDeposits(amount: String) {
        viewModel.getClientDepositList(getClientToken()).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.data != null) {
                        menuProductsViewModel.updateClientDepositList(it.data!!.data)
                        when (operation) {
                            "top_up" -> {
                                goto(
                                    R.id.basicSuccessFragment, bundleOf(
                                        Const.OPERATION to BasicSuccessFragment.DEPOSIT_FILLING,
                                        Const.OPERATION_AMOUNT to amount
                                    )
                                )
                            }

                            "delete" -> {
                                goto(
                                    R.id.basicSuccessFragment,
                                    bundleOf(Const.OPERATION to BasicSuccessFragment.DEPOSIT_CLOSE)
                                )
                            }

                            "with_draw_percent", "with_draw" -> {
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
}