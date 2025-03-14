package uz.fido.universaldigital.ui.fragments.services.deposit.constructor

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.functions.Function5
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercent
import uz.fido.network.domain.model.deposits.constructor.DepositConstPercentRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstRequest
import uz.fido.network.domain.model.deposits.constructor.DepositConstructor
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentDepositConstructorBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.ConstructorCapitalDialog
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DepositConstructFragment :
    BaseFragment<FragmentDepositConstructorBinding, ConstructorViewModel>
        (FragmentDepositConstructorBinding::inflate, ConstructorViewModel::class.java),
    View.OnClickListener, (Int, String) -> Unit {


    companion object {
        const val CONSTRUCTOR_DEP_ID = "2321"
    }

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()
    private lateinit var loanMonthDialog: LoanMonthDialog
    private var type = "000"
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var finalDay = "0"
    private var totalDays = 0
    private var minAmount = BigDecimal.ZERO
    private var maxAmount = BigDecimal(10000000)
    private var maxDays = 1825
    private var optionCurrent_1 = false
    private var optionCurrent_2 = false
    private var optionCurrent_3 = false
    private var optionCurrent_4 = false
    private var percentCheck = false
    private var optionCheck = false
    private lateinit var cardResponse: CardResponse
    private lateinit var dialogCapital: ConstructorCapitalDialog

    /*С капитализацией  С пополнением*/
    private var inCapitalInAdd: String? = null

    /*С капитализацией  Без пополнения */
    private var inCapitalOutAdd: String? = null

    /*Без капитализации  Без пополнения*/
    private var outCapitalOutAdd: String? = null

    /*Без капитализации  С пополнением*/
    private var outCapitalInAdd: String? = null

    /*Процент для пользователя*/
    private var percent: String? = null
    private var dcParam103 = "D"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        radioButtonOnClick()
        initCards()
        onClickView()
        rxBinding()
    }

    private fun rxBinding() {
        io.reactivex.rxjava3.core.Observable.combineLatest(
            binding.etAmount.textChanges(),
            binding.edMonth.textChanges(),
            binding.edDay.textChanges(),
            binding.edYear.textChanges(),
            binding.etName.textChanges(),
            Function5(this::isValid)
        ).doOnNext {
            binding.btnContinue.isEnabled(it)
        }
            .subscribe()
    }

    private fun isValid(
        amount: CharSequence,
        month: CharSequence,
        day: CharSequence,
        year: CharSequence,
        name: CharSequence
    ): Boolean {

        binding.percentAmount.text = Format().percentAmount(amount.toString(), percent)
        val amountCurrent = if (amount.isEmpty()) false
        else amount.toString().replace(" ", "").toBigDecimal() >= minAmount

        val nameCurrent = name.isNotEmpty()

        val timeCurrent = if (month.isNotEmpty() || day.isNotEmpty() || year.isNotEmpty())
            month.toString() != "0" || day.toString() != "0" || year.toString() != "0"
        else false
        return amountCurrent && nameCurrent && timeCurrent && percentCheck
    }

    override fun onResume() {
        super.onResume()
        if (binding.edDay.text!!.isNotEmpty() || binding.edMonth.text!!.isNotEmpty() || binding.edYear.text!!.isNotEmpty()) {
            getDepositConstructorPercents()
        } else {
            fetchDepositParams()
        }
    }

    private fun fetchDepositParams() {
        binding.progress.visibility = View.VISIBLE
        binding.percent.visibility = View.GONE
        viewModel.getDepositConstParams(getClientToken(), DepositConstRequest(CONSTRUCTOR_DEP_ID))
            .observe(viewLifecycleOwner) {
                binding.progress.visibility = View.GONE
                binding.percent.visibility = View.VISIBLE
                when (it.status) {
                    Status.SUCCESS -> {
                        percentCheck = true
                        if (it.data?.dcParam105 != null && it.data?.dcParam105?.size != 0) {
                            dcParam103 = it.data?.dcParam103?.get(0) ?: "D"
                            setPercentsFromServer(it.data?.dcParam105!![0])
                        }
                        it.data?.dcParam102?.let { amount ->
                            minAmount = amount[0].toBigDecimal()
                            maxAmount = amount[1].toBigDecimal()
                        }
                        binding.amountLayout.hint =
                            "${getString(R.string.min_summa)} ${Format.formatAmount(minAmount.toString())} UZS"
                    }

                    Status.ERROR -> {
                        binding.percentAmount.text = "0.00"
                        percentCheck = false
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }

    private fun onClickView() {
        binding.usd.setOnClickListener(this)
        binding.uzs.setOnClickListener(this)
        binding.yearLayout.setOnClickListener(this)
        binding.monthLayout.setOnClickListener(this)
        binding.edYear.setOnClickListener(this)
        binding.edMonth.setOnClickListener(this)
        binding.edDay.setOnClickListener(this)
        binding.edMonth.setOnClickListener(this)
        binding.edYear.setOnClickListener(this)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.dayLayout.setOnClickListener(this)
        binding.additionalParameters.setOnClickListener(this)
        binding.btnContinue.setOnClickListener {
            openNewDeposit()
        }
    }

    private fun radioButtonOnClick() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.to_card -> {
                    TransitionManager.beginDelayedTransition(binding.chooseCardLayout)
                    binding.chooseCardLayout.visibility = View.VISIBLE
                }

                else -> {
                    TransitionManager.beginDelayedTransition(binding.chooseCardLayout)
                    binding.chooseCardLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>
            ) { cardResponse ->
                cardResponse?.let { card ->
                    this@DepositConstructFragment.cardResponse = card
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.uzs -> {
                binding.uzs.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.cornered_bg_10dp_stroke
                    )
                )
                binding.usd.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.usd_background_color
                    )
                )
                type = "000"
            }

            R.id.usd -> {
                Toast.makeText(requireContext(), "В развитие", Toast.LENGTH_SHORT).show()
//                binding.uzs.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_layout_bg))
//                binding.usd.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.cornered_bg_12dp_stroke))
//                type="USD"
            }

            R.id.year_layout, R.id.ed_year -> {
                yearCheckList()
            }

            R.id.month_layout, R.id.ed_month -> {
                monthCheckList()
            }

            R.id.day_layout, R.id.ed_day -> {
                dayCheckList()
            }

            R.id.additional_parameters -> {
                dialogCapital = ConstructorCapitalDialog { option1, option2, option3, option4 ->
                    optionCurrent_1 = option1
                    optionCurrent_2 = option2
                    optionCurrent_3 = option3
                    optionCurrent_4 = option4
                    dialogCapital.dismiss()
                    setPercent()
                }
                dialogCapital.show(childFragmentManager, "TAG")
            }
        }
    }

    private fun openNewDeposit() {
        val model = DepositConstructor(
            percent = percent.toString(),
            dep_id = CONSTRUCTOR_DEP_ID,
            amount = binding.etAmount.text.toString().replace(" ", ""),
            dep_type = "1",
            date = finalDay,
            dcParam103 = dcParam103,
            dcParam104 = if (binding.addToDeposit.isChecked) "CURR_DEP_ACCOUNT" else "CARD_ACCOUNT",
            pay_to_card = if (binding.addToDeposit.isChecked) "Y" else "N",
            pay_to_object_value = if (binding.addToDeposit.isChecked) "" else cardResponse.object_value,
            replenishment = if (optionCurrent_1) "Y" else "N",
            interest_payment = "har oyda",
            capitalization = if (optionCurrent_3) "Y" else "N",
            partial_withdrawal = if (optionCurrent_4) "Y" else "N",
            dep_name = binding.etName.editableText.toString()
        )

        if (binding.addToDeposit.isChecked)
            gotoWithSlide(R.id.depositConstructorConfirmFragment, bundleOf("model" to model))
        else if (cardResponse.currency_code == "000")
            gotoWithSlide(R.id.depositConstructorConfirmFragment, bundleOf("model" to model))
        else showSnackbar(getString(R.string.card_number), getString(R.string.error_card))
    }

    private fun dayCheckList() {
        loanMonthDialog = LoanMonthDialog(this, "", "day", 0, R.string.select)
        loanMonthDialog.show(childFragmentManager, "")
    }

    private fun monthCheckList() {
        loanMonthDialog = LoanMonthDialog(this, "13", "month", 0, R.string.select)
        loanMonthDialog.show(childFragmentManager, "")
    }

    private fun yearCheckList() {
        loanMonthDialog = LoanMonthDialog(this, "", "year", 0, R.string.select)
        loanMonthDialog.show(childFragmentManager, "")

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setDate() {
        totalDays = year * 365 + month * 30 + day
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.YEAR, year)
        cal.add(Calendar.MONTH, month)
        cal.add(Calendar.DAY_OF_YEAR, day)
        val format2: DateFormat = SimpleDateFormat("dd.MM.yyyy")
        finalDay = format2.format(cal.time)
        binding.calculatedDate.text = "${getString(R.string.to_lower)} $finalDay"
        if (checkForDate()) {
            getDepositConstructorPercents()
        }
    }

    private fun getDepositConstructorPercents() {
        val request = DepositConstPercentRequest(
            CONSTRUCTOR_DEP_ID,
            type,
            finalDay,
            dcParam103
        )
        binding.progress.visibility = View.VISIBLE
        binding.percent.visibility = View.GONE
        viewModel.getDepositConstPercents(getClientToken(), request).observe(viewLifecycleOwner) {
            binding.progress.visibility = View.GONE
            binding.percent.visibility = View.VISIBLE
            when (it.status) {
                Status.SUCCESS -> {
                    optionCheck = true
                    it.data?.let { it1 -> setPercentsFromServer(it1) }
                }

                Status.ERROR -> {
                    optionCheck = false
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setPercentsFromServer(depositConstPercent: DepositConstPercent) {
        inCapitalInAdd = depositConstPercent.inCapitalInAdd
        outCapitalInAdd = depositConstPercent.outCapitalInAdd
        inCapitalOutAdd = depositConstPercent.inCapitalOutAdd
        outCapitalOutAdd = depositConstPercent.outCapitalOutAdd
        if (depositConstPercent.max != null) {
            maxDays = depositConstPercent.max!!.toInt()
        }
        setPercent()
    }

    private fun setPercent() {
        percent = if (optionCurrent_1 && optionCurrent_3) {
            /*С капитализацией  С пополнением*/
            inCapitalInAdd
        } else if (!optionCurrent_1 && optionCurrent_3) {
            /*С капитализацией  Без пополнения */
            inCapitalOutAdd
        } else if (optionCurrent_1 && !optionCurrent_3) {
            /*Без капитализации  С пополнением*/
            outCapitalInAdd
        } else if (!optionCurrent_1 && !optionCurrent_2 && !optionCurrent_3 && !optionCurrent_3 &&
            /*!binding.option4.isChecked && */!optionCurrent_4 && binding.etName.text.isNullOrEmpty() && binding.etAmount.text.isNullOrEmpty() &&
            binding.edYear.text.isNullOrEmpty() && binding.edMonth.text.isNullOrEmpty() && binding.edDay.text.isNullOrEmpty()
        ) {
            "0"
        } else {
            /*Без капитализации  Без пополнения*/
            outCapitalOutAdd
        }
        binding.percent.text = "$percent %"
        binding.percentAmount.text =
            Format().percentAmount(binding.etAmount.text.toString(), percent)

    }

    private fun checkForDate(): Boolean {
        return if (totalDays > maxDays) {
            val cal: Calendar = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, maxDays)
            val format2: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            finalDay = format2.format(cal.time)
            showSnackbar("Максимальная дата до ${format2.format(cal.time)}")
            false
        } else {
            true
        }
    }

    override fun invoke(p1: Int, p2: String) {
        when (p2) {
            "month" -> {
                month = p1
                binding.edMonth.setText("$p1")
            }

            "day" -> {
                day = p1
                binding.edDay.setText("$p1")
            }

            "year" -> {
                year = p1
                binding.edYear.setText(p1.toString())
            }
        }

        setDate()
        loanMonthDialog.dismiss()
    }

}