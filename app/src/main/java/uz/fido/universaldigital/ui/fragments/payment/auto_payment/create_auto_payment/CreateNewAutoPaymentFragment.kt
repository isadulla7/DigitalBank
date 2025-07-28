package uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateNewAutoPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.AutoPaymentViewModel
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentCustomDateAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentDaysAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.CreateAutoPaymentFragment.Companion.SAVE_AUTO_PAYMENT_MODEL
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.dialog.ChoosePeriodType
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateNewAutoPaymentFragment : BaseFragment<FragmentCreateNewAutoPaymentBinding, AutoPaymentViewModel>
    (FragmentCreateNewAutoPaymentBinding::inflate, AutoPaymentViewModel::class.java), View.OnClickListener, (Int, String) -> Unit {

    private lateinit var choosePeriodType: ChoosePeriodType
    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    private var autoPayment: AutoPayment? = null
    private var autoPaymentType = 0
    private var currentCheck: Boolean = false

    //days
    private var days = ArrayList<String>()
    private var daysList = ArrayList<AllServiceLists>()
    private var daysAdapter: AutoPaymentDaysAdapter? = null
    private var selectedTime: String? = null
    private var selectedCustom: String? = null
    private var selectedMonth: String? = null

    //month
    private var months = ArrayList<String>()
    private var monthsList = ArrayList<AllServiceLists>()
    private var monthsAdapter: AutoPaymentDaysAdapter? = null

    //custom
    private var customDates = java.util.ArrayList<String>()
    private val df = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    private lateinit var customDatesAdapterAdapter: AutoPaymentCustomDateAdapter
    private lateinit var loanMonthDialog: LoanMonthDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            saveAutoPaymentModel = it.serializable(SAVE_AUTO_PAYMENT_MODEL) as SaveAutoPaymentModel?
            autoPayment = it.serializable("item") as AutoPayment?
        }
        initMonths()
        initDays()
        initAutoPayment()
        onClickView()
        initCustomDates()
        buttonCheck()
    }

    override fun onResume() {
        super.onResume()
        when (autoPaymentType) {
            1 -> {
                binding.editTextType.setText(getString(R.string.by_day))
                binding.daily.visibility = View.VISIBLE
                binding.monthly.visibility = View.GONE
                binding.customDateLayout.visibility = View.GONE
            }

            2 -> {
                binding.editTextType.setText(getString(R.string.by_month))
                binding.daily.visibility = View.GONE
                binding.monthly.visibility = View.VISIBLE
                binding.customDateLayout.visibility = View.GONE
            }

            3 -> {
                binding.daily.visibility = View.GONE
                binding.monthly.visibility = View.GONE
                binding.customDateLayout.visibility = View.VISIBLE
                binding.editTextType.setText(getString(R.string.custom))
            }
        }
        buttonCheck()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            buttonCheck()
        }
    }

    private fun buttonCheck() {

        val maxAmount = saveAutoPaymentModel?.sms_control_limit?.toBigDecimalOrNull()?: BigDecimal(0)
        Log.d("TAG", "buttonCheck:${maxAmount} ")
        when (autoPaymentType) {
            1 -> {
                val count = daysList.filter { it.isSelected }
                val amount = binding.editTextAmount.text.toString().replace(" ", "").toDoubleOrNull() ?: 0.0
                binding.btnContinue.isEnabled(
                    !binding.editTextName.text.isNullOrEmpty() &&
                            binding.editTextAmount.text.toString().isNotEmpty() &&
                            amount >= 500 && maxAmount>=amount.toBigDecimal() &&
                            binding.editTextName.toString().isNotEmpty() &&
                            count.isNotEmpty()
                            && !binding.editTextTimeDay.text.isNullOrEmpty()
                )
            }

            2 -> {
                val amount = binding.editTextAmount.text.toString().replace(" ", "").toDoubleOrNull() ?: 0.0
                val count = monthsList.filter { it.isSelected }
                binding.btnContinue.isEnabled(
                    !binding.editTextName.text.isNullOrEmpty() &&
                            !binding.editTextDayOfPayment.text.isNullOrEmpty() &&
                            amount >= 500
                            && maxAmount>=amount.toBigDecimal() && count.isNotEmpty() && !binding.editTextTime.text.isNullOrEmpty()
                )
            }

            3 -> {
                val amount = binding.editTextAmount.text.toString().replace(" ", "").toDoubleOrNull() ?: 0.0
                binding.btnContinue.isEnabled(
                    customDates.isNotEmpty()
                            && !binding.editTextName.text.isNullOrEmpty()
                            && amount >= 500
                            && maxAmount>=amount.toBigDecimal() && !binding.editTextCustom.text.isNullOrEmpty()
                )
            }
        }
    }

    private fun nextWindow() {
        if (autoPayment != null) {
            //  editAutoPayment()
        } else {
            var hour =""
            when (autoPaymentType) {
                1 -> {
                    hour = selectedTime.toString()
                }

                2 -> {
                    hour = selectedMonth.toString()
                }

                3 -> {
                    hour = selectedCustom.toString()
                }
            }
            saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
            saveAutoPaymentModel?.name = binding.editTextName.text.toString()
            saveAutoPaymentModel?.hours = hour
            saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
            saveAutoPaymentModel?.payment_details?.set("AMOUNT", Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", "")))
            when (autoPaymentType) {
                1 -> {
                    val daysArrayList = java.util.ArrayList<Int>()
                    val days = java.util.ArrayList<String>()
                    daysList.forEach {
                        if (it.isSelected) {
                            daysArrayList.add(it.code!!.toInt())
                            days.add(it.name.toString())
                        }
                    }
                    val dayName = TextUtils.join(", ", days)
                    saveAutoPaymentModel?.months = java.util.ArrayList()
                    saveAutoPaymentModel?.days = daysArrayList
                    saveAutoPaymentModel?.daysName = dayName
                    saveAutoPaymentModel?.type = "D"
                    saveAutoPaymentModel?.selected_days = java.util.ArrayList()
                    if (daysArrayList.isNotEmpty())
                        gotoWithSlide(
                            R.id.saveAutoPaymentFinalFragment,
                            bundleOf(
                                SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                                "operation" to "create"
                            )
                        )
                }

                2 -> {
                    val monthsArrayList = java.util.ArrayList<Int>()
                    val months = java.util.ArrayList<String>()
                    monthsList.forEach {
                        if (it.isSelected) {
                            monthsArrayList.add(it.code!!.toInt())
                            months.add(it.name.toString())
                        }
                    }
                    val monthName = TextUtils.join(", ", months)
                    saveAutoPaymentModel?.months = monthsArrayList
                    val daysArrayList = java.util.ArrayList<Int>()
                    daysArrayList.add(binding.editTextDayOfPayment.text.toString().toInt())
                    saveAutoPaymentModel?.days = daysArrayList
                    saveAutoPaymentModel?.daysName = binding.editTextDayOfPayment.text.toString()
                    saveAutoPaymentModel?.monthsName = monthName
                    saveAutoPaymentModel?.type = "M"
                    saveAutoPaymentModel?.selected_days = java.util.ArrayList()
                    gotoWithSlide(
                        R.id.saveAutoPaymentFinalFragment,
                        bundleOf(SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel)
                    )
                }

                3 -> {
                    saveAutoPaymentModel?.monthsName = ""
                    saveAutoPaymentModel?.daysName = ""
                    saveAutoPaymentModel?.months = ArrayList()
                    saveAutoPaymentModel?.days = ArrayList()
                    saveAutoPaymentModel?.type = "S"
                    saveAutoPaymentModel?.selected_days = customDates
                    gotoWithSlide(
                        R.id.saveAutoPaymentFinalFragment,
                        bundleOf(SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel)
                    )
                }
            }
        }
    }

    private fun onClickView() {
        binding.iconTextType.setOnClickListener(this)
        binding.editTextType.setOnClickListener(this)
        binding.editTextTime.setOnClickListener(this)
        binding.customDate.setOnClickListener(this)
        binding.editTextDayOfPayment.setOnClickListener(this)
        binding.iconTextDayOfPayment.setOnClickListener(this)
        binding.editTextTimeDay.setOnClickListener(this)
        binding.iconTextTimeDay.setOnClickListener(this)
        binding.iconTextTime.setOnClickListener(this)
        binding.iconTextCustom.setOnClickListener(this)
        binding.editTextCustom.setOnClickListener(this)
        binding.iconCustomDate.setOnClickListener(this)
        binding.editTextTimeDay.addTextChangedListener(textWatcher)
        binding.editTextDayOfPayment.addTextChangedListener(textWatcher)
        binding.editTextTime.addTextChangedListener(textWatcher)
        binding.editTextName.addTextChangedListener(textWatcher)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            nextWindow()
        }
        binding.editTextAmount.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            private val maxAmount= saveAutoPaymentModel?.sms_control_limit?.toBigDecimalOrNull()?: BigDecimal(0)

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                val digits = s.toString().replace(Regex("[^0-9]"), "")
                if (digits.isNotEmpty()) {
                    val formatted = digits.reversed().chunked(3).joinToString(" ").reversed()
                    binding.editTextAmount.setText(formatted)
                    binding.editTextAmount.setSelection(formatted.length)
                    val amount = digits.toLongOrNull() ?: 0L
                    when{
                        amount < 500->binding.editTextAmount.error = getString(R.string.min_summ)
                        amount.toBigDecimal()>=maxAmount ->binding.editTextAmount.error = getString(R.string.max_amount)+maxAmount
                        else->binding.editTextAmount.error = null
                    }

                } else {
                    binding.editTextAmount.setText("")
                    binding.editTextAmount.error = getString(R.string.min_summ)
                }
                buttonCheck()
                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    private fun initCustomDates() {
        binding.customDates.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            customDatesAdapterAdapter = AutoPaymentCustomDateAdapter(ArrayList()) { date ->
                val index = customDates.indexOf(date)
                customDatesAdapterAdapter.notifyItemRemoved(index)
                customDates.remove(date)
                binding.btnContinue.isEnabled(customDates.isNotEmpty())

            }

            adapter = customDatesAdapterAdapter
            customDatesAdapterAdapter.setList(customDates)
        }
    }

    private fun initAutoPayment() {
        if (autoPayment != null) {
            binding.editTextAmount.setText(Format.convertFromTiynDivide(autoPayment?.amount.toString()))
            binding.editTextName.setText(autoPayment?.name.toString())
        } else {
            val amount = saveAutoPaymentModel?.payment_details?.get("AMOUNT")
            if (amount != null) {
                binding.editTextAmount.setText(Format.convertFromTiynDivide(amount))
            }
            binding.editTextName.setText(saveAutoPaymentModel?.name)
            if (!currentCheck) {
                addAllDays()
                addAllMonths()
                currentCheck = true
            }

        }
    }

    private fun initDays() {
        days = arrayListOf(
            getString(R.string.monday),
            getString(R.string.tuesday),
            getString(R.string.thursday),
            getString(R.string.wednesday),
            getString(R.string.friday),
            getString(R.string.saturday),
            getString(R.string.sunday),
        )
        binding.switchDaily.setOnClickListener {
            daysList.forEach {
                it.isSelected = binding.switchDaily.isChecked
            }
            buttonCheck()
            daysAdapter?.setList(daysList)
        }
        binding.recyclerViewDay.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            daysAdapter = AutoPaymentDaysAdapter(this@CreateNewAutoPaymentFragment, daysList)
            adapter = daysAdapter
        }
    }

    override fun justOperation() {
        super.justOperation()
        var selectedDays = 0
        var selectedMonths = 0
        daysList.forEach {
            if (it.isSelected) {
                ++selectedDays
            }
        }
        monthsList.forEach {
            if (it.isSelected) {
                ++selectedMonths
            }
        }
        if (autoPaymentType == 1)
            binding.switchDaily.isChecked = selectedDays == 7
        else binding.sswitchMonthly.isChecked = selectedMonths == 12
        buttonCheck()
    }

    private fun addAllDays() {
        daysList = ArrayList()
        for (i in days.indices) {
            val autoPaymentDates = AllServiceLists()
            autoPaymentDates.name = days[i]
            autoPaymentDates.code = (i + 1).toString()
            daysList.add(autoPaymentDates)
        }
        daysAdapter?.setList(daysList)
    }

    //month
    private fun initMonths() {
        months = arrayListOf(
            getString(R.string.month_jan),
            getString(R.string.month_fev),
            getString(R.string.month_mar),
            getString(R.string.month_apr),
            getString(R.string.month_may),
            getString(R.string.month_june),
            getString(R.string.month_july),
            getString(R.string.month_aug),
            getString(R.string.month_sep),
            getString(R.string.month_oct),
            getString(R.string.month_nov),
            getString(R.string.month_dec)
        )
        binding.sswitchMonthly.setOnClickListener {
            monthsList.forEach {
                it.isSelected = binding.sswitchMonthly.isChecked
            }
            monthsAdapter?.setList(monthsList)
            buttonCheck()
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            monthsAdapter = AutoPaymentDaysAdapter(this@CreateNewAutoPaymentFragment, monthsList)
            adapter = monthsAdapter
        }
    }

    private fun addAllMonths() {
        monthsList = ArrayList()
        for (i in months.indices) {
            val autoPaymentDates = AllServiceLists()
            autoPaymentDates.name = months[i]
            autoPaymentDates.code = (i + 1).toString()
            monthsList.add(autoPaymentDates)
        }
        monthsAdapter?.setList(monthsList)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.edit_text_type, R.id.icon_text_type -> {
                choosePeriodType = ChoosePeriodType { type ->
                    choosePeriodType.dismiss()
                    autoPaymentType = type
                    when (type) {
                        1 -> {
                            binding.editTextType.setText(getString(R.string.by_day))
                            binding.daily.visibility = View.VISIBLE
                            binding.monthly.visibility = View.GONE
                            binding.customDateLayout.visibility = View.GONE
                        }

                        2 -> {
                            binding.editTextType.setText(getString(R.string.by_month))
                            binding.daily.visibility = View.GONE
                            binding.monthly.visibility = View.VISIBLE
                            binding.customDateLayout.visibility = View.GONE
                        }

                        3 -> {
                            binding.daily.visibility = View.GONE
                            binding.monthly.visibility = View.GONE
                            binding.customDateLayout.visibility = View.VISIBLE
                            binding.editTextType.setText(getString(R.string.custom))
                        }
                    }
                    buttonCheck()
                }
                choosePeriodType.show(childFragmentManager, "")
            }

            R.id.edit_text_day_of_payment, R.id.icon_text_day_of_payment -> {
                loanMonthDialog = LoanMonthDialog(this, "", "day", 0, R.string.add)
                loanMonthDialog.show(childFragmentManager, "")

            }

            R.id.edit_text_time_day, R.id.icon_text_time_day -> {
                val cal = Calendar.getInstance()
                val timePicker = TimePickerDialog(
                    activity, R.style.my_dialog_theme,
                    { _, selectedHour, selectedMinute ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val formattedTime = sdf.format(calendar.time)
                        selectedTime = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextTimeDay.setText(formattedTime)
                        } else binding.btnContinue.isEnabled(false)
                        buttonCheck()

                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.HOUR_OF_DAY),
                    true
                )
                timePicker.setTitle(getString(R.string.choose_hour))
                timePicker.show()
                timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }

            R.id.edit_text_time, R.id.icon_text_time -> {
                val cal = Calendar.getInstance()
                val timePicker = TimePickerDialog(
                    activity, R.style.my_dialog_theme,
                    { _, selectedHour, selectedMinute ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val formattedTime = sdf.format(calendar.time)
                        selectedTime = selectedHour.toString()
                        selectedMonth = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextTime.setText(formattedTime)
                        } else binding.btnContinue.isEnabled(false)
                        buttonCheck()
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.HOUR_OF_DAY),
                    true
                )
                timePicker.setTitle(getString(R.string.choose_hour))
                timePicker.show()
                timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }

            R.id.edit_text_custom, R.id.icon_text_custom -> {
                val cal = Calendar.getInstance()

                val timePicker = TimePickerDialog(
                    activity, R.style.my_dialog_theme,
                    { _, selectedHour, selectedMinute ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val formattedTime = sdf.format(calendar.time)
                        selectedTime = selectedHour.toString()
                        selectedCustom = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextCustom.setText(formattedTime)
                        }
                        buttonCheck()
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.HOUR_OF_DAY),
                    true
                )
                timePicker.setTitle(getString(R.string.choose_hour))
                timePicker.show()
                timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }

            R.id.custom_date, R.id.icon_custom_date -> {
                pickDateTime()
            }
        }
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val currentDateInMillis = currentDateTime.timeInMillis
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.my_dialog_theme,
            { _, year, month, day ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day)
                if (pickedDateTime.timeInMillis < currentDateInMillis) {
                    Toast.makeText(requireContext(), "Faqat bugungi sanadan keyingi sanani tanlash mumkin", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                if (!customDates.contains(df.format(pickedDateTime.time))) {
                    customDates.add(df.format(pickedDateTime.time))
                    customDatesAdapterAdapter.setList(customDates)
                }

                buttonCheck()
            },
            startYear,
            startMonth,
            startDay)
        datePickerDialog.datePicker.minDate = currentDateInMillis

        // DatePickerDialog ni ko'rsatish
        datePickerDialog.show()
    }

    override fun invoke(count: Int, name: String) {
        when (name) {
            "day" -> {
                if (count == 31 || count == 30) {
                    binding.infoView.visibility = View.VISIBLE
                } else binding.infoView.visibility = View.GONE
                binding.editTextDayOfPayment.setText(count.toString())
                buttonCheck()
                loanMonthDialog.dismiss()
            }
        }
    }

}