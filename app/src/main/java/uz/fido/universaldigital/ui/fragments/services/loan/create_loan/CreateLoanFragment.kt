package uz.fido.universaldigital.ui.fragments.services.loan.create_loan

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.SpannedString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.loans.loan_groups.CreditGroup
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentCreateLoanBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.dialog.DepositConstructorTimeDialog
import uz.fido.universaldigital.ui.fragments.services.loan.LoanGroupListFragment
import uz.fido.universaldigital.ui.fragments.services.loan.LoanViewModel
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.universaldigital.ui.fragments.services.loan.loan_info.LoanUserInfo1Fragment
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.loan.CreditCalculatorUtil
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class CreateLoanFragment : BaseFragment<FragmentCreateLoanBinding, LoanViewModel>(
    FragmentCreateLoanBinding::inflate, LoanViewModel::class.java
), (Int, String) -> Unit {
    private var dialog: DepositConstructorTimeDialog? = null
    private lateinit var loanMonthDialog: LoanMonthDialog
    private lateinit var creditGroup: CreditGroup
    private var minAmount = 0.00
    private var maxAmount = 0.00
    private var days = ArrayList<AllServiceLists>()
    private var creditReplenishmentTypes = ArrayList<AllServiceLists>()
    private var percent = ""
    private var monthlyAmount = BigDecimal(0)
    private var selectedDate: Int = 0
    private var selectedCard: CardResponse? = null
    private var cardResponse: List<CardResponse> = listOf()
    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creditGroup =
            requireArguments().getSerializable(LoanGroupListFragment.CREATE_LOAN) as CreditGroup

        initCards()
        setInfoText()
        seekBarAmount()
        // lifeTime()
        paymentTime()
        loanType()
        //  calculateMonthlyAmount(minAmount.toBigDecimal())
        onClickView()
        initOffer()
        textWatchers()

    }

    private fun textWatchers() {
        binding.etAmountMinMax.addTextChangedListener {
            val text = it.toString().replace(" ", "")
            if (text.isNotEmpty()) {
                if (text.toInt() <= maxAmount) {
                    creditGroup.amount = text
                    calculateMonthlyAmount(text.toBigDecimal())
                }
                isCheckAmount(text.toInt())
            }
        }
    }

    private fun isCheckAmount(amount: Int) {
        if (amount > minAmount && amount <= maxAmount &&
            /*binding.etTime.text.toString().isNotEmpty()
            &&*/ hasCardUniversalFirst() && hasAsiaCard(cardResponse)
            && selectedCard?.state == "A" && selectedCard?.processing_server_status == "0"
        ) {
            binding.btnContinue.isEnabled(true)
        } else binding.btnContinue.isEnabled(false)
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {

            if (binding.checkBox.isChecked && cardResponse.isNotEmpty()) {
                if (hasAsiaCard(cardResponse)) {
                    myidIsCheck()

                    //getClientAsia()
                }
            }
        }
    }

    private fun myidIsCheck() {
        binding.btnContinue.setProgress(true)
        goto(
            R.id.createLoanConfirmFragment,
            bundleOf("product" to creditGroup, "selectedCard" to selectedCard)
        )
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            this.cardResponse = it
            val listnew = arrayListOf<CardResponse>()
            it.forEach { card ->
                if ((card.object_type == "SV" || card.object_type == "GL") && card.state == "0") {
                    listnew.add(card)
                }
            }
            binding.chooseCardLayout.initCards(
                listnew, "0"
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    val amount = binding.etAmountMinMax.text.toString()
                    if (amount.isNotEmpty())
                        isCheckAmount(amount.replace(" ", "").toInt())
                }
            }
        }
    }


    private fun getUserInfo() {
        binding.btnContinue.setProgress(true)
        viewModel.getUserInfo(getClientToken()).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    creditGroup.creditAmount =
                        binding.etAmountMinMax.text.toString().replace(" ", "")
                    creditGroup.monthlyAmount = binding.etAmount.toString()
                    creditGroup.selectedDate = selectedDate.toString()
                    creditGroup.selectedPercent = "$percent%"
                    creditGroup.paymentDate = binding.etPaymentTime.text.toString()
                    saveCreditProgress(creditGroup, it.data?.client_info!!, null, 1)
                    gotoWithSlide(
                        R.id.loanUserInfo1Fragment, bundle =
                        bundleOf(
                            LoanUserInfo1Fragment.CREDIT_ITEM to creditGroup,
                            LoanUserInfo1Fragment.CLIENT_INFO to it.data!!.client_info
                        )
                    )

                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun calculateMonthlyAmount(amount: BigDecimal) {
        val amountWithPercent = amount.multiply(percent.toBigDecimal()).divide(BigDecimal(100))
        val amountForMonth = amountWithPercent.divide(12.toBigDecimal(), 2, RoundingMode.CEILING)
        monthlyAmount = CreditCalculatorUtil.calculate(
            amount,
            percent.toBigDecimal().divide(BigDecimal(100)),
            selectedDate
        )
        binding.etAmount.setText(Format.formatAmount(monthlyAmount.toString()))
    }

    private fun loanType() {
        if (creditReplenishmentTypes.size == 0) {
            val names = arrayOf("Аннуитетный", "Дифференцированный")
            for (i in 0 until 1) {
                val model = AllServiceLists()
                model.name = names[i]
                model.code = i.toString()
                creditReplenishmentTypes.add(model)
            }
        }
        binding.etLoanType.setOnClickListener {
            dialog = DepositConstructorTimeDialog(object : BaseInterface {
                override fun setToEditText(allServiceLists: AllServiceLists, tag: String) {
                    binding.etLoanType.setText(allServiceLists.name)
                    isCheckAmount(binding.etAmountMinMax.text.toString().replace(" ", "").toInt())
                    dialog!!.dismiss()
                }
            }, creditReplenishmentTypes)
            dialog!!.show(childFragmentManager, "")
        }
    }

    private fun paymentTime() {
        if (days.size == 0) {
            for (i in 5 until 25) {
                val model = AllServiceLists()
                model.name = i.toString()
                model.code = i.toString()
                days.add(model)
            }
        }
        binding.etPaymentTime.setOnClickListener {
            loanMonthDialog = LoanMonthDialog(this, "", "day", selectedDate, requireContext())
            loanMonthDialog.show(childFragmentManager, "")

        }
    }

    private fun seekBarAmount() {
        binding.etAmountMinMax.setText("0")
        binding.seekBar.max = maxAmount.toInt()
        binding.seekBar.progress = 0
        binding.seekBar.incrementProgressBy(0)

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onProgressChanged(seekBar: SeekBar?, amount: Int, p2: Boolean) {
                binding.etAmountMinMax.setText(amount.toString())
                calculateMonthlyAmount(amount.toBigDecimal())
                isCheckAmount(amount)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun setInfoText() {
        selectedDate = creditGroup.time_max.toInt()
        percent = creditGroup.percentMin
        minAmount = Format.convertFromTiynDivide(creditGroup.minSumma).toDouble()
        maxAmount = Format.convertFromTiynDivide(creditGroup.maxSumma).toDouble()
        //    binding.etAmountMinMax.setText(Format.convertFromTiynDivide(creditGroup.minSumma))
        binding.loanAmount.text = "${getString(R.string.loan_amount)} ${
            Format.formatAmount(
                Format.convertFromTiynDivide(creditGroup.maxSumma)
            )
        }  ${getString(R.string.summa)}"
        binding.percentText.text = "${getString(R.string.percent_text)} ${creditGroup.percentMin} %"
        binding.tvTime.text =
            "${getString(R.string.loan_time)} ${creditGroup.time_max} ${getString(uz.fido.utils.R.string.month)}"
        binding.etTime.setText(selectedDate.toString())
    }


    private fun initOffer() {
        val fullText = getText(R.string.accept_deposit_privacy) as SpannedString
        val spannableString = SpannableString(fullText)
        val annotations = fullText.getSpans(0, fullText.length, Annotation::class.java)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {

                val website = Keys.getDepositOfferBaxtliBolalik()
                val webIntent = Intent(Intent.ACTION_VIEW)
                webIntent.data = Uri.parse(website)
                requireActivity().startActivity(webIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        annotations?.find {
            it.value == "help_link"
        }?.let {
            spannableString.setSpans(it, clickableSpan, fullText, requireContext())
        }

        binding.textPrivacy.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun invoke(p1: Int, type: String) {
        if (type == "month") {
            binding.etTime.setText("$p1 ${getString(uz.fido.utils.R.string.month)}")
            selectedDate = p1
            binding.btnContinue.isEnabled(true)
            calculateMonthlyAmount(
                binding.etAmountMinMax.text.toString().replace(" ", "").toBigDecimal()
            )
        } else {
            binding.etPaymentTime.setText(p1.toString())
        }
        isCheckAmount(binding.etAmountMinMax.text.toString().replace(" ", "").toInt())
        loanMonthDialog.dismiss()
    }

    private fun isUniversalSumCard(cardResponse: CardResponse): Boolean =
        cardResponse.object_value.startsWith("860048") ||
                cardResponse.object_value.startsWith("626283") ||
                cardResponse.object_value.startsWith("986023")

    private fun hasAsiaCard(cardList: List<CardResponse>): Boolean {
        var response = false
        cardList.forEach {
            response = response || isUniversalSumCard(it)
        }
        return response
    }

    private fun hasCardUniversalFirst(): Boolean {
        if (selectedCard == null) return false
        return selectedCard!!.object_value.startsWith("860055") ||
                selectedCard!!.object_value.startsWith("626283") ||
                selectedCard!!.object_value.startsWith("986009")
    }
}