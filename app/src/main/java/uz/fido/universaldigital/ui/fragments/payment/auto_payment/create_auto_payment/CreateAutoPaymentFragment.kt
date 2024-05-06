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
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateAutoPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.AutoPaymentViewModel
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentCustomDateAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentDaysAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.dialog.ChoosePeriodType
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class CreateAutoPaymentFragment : BaseFragment<FragmentCreateAutoPaymentBinding, AutoPaymentViewModel>
    (FragmentCreateAutoPaymentBinding::inflate, AutoPaymentViewModel::class.java),
    View.OnClickListener, (Int, String) -> Unit, TimePickerDialog.OnTimeSetListener {

    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    private var autoPayment: AutoPayment? = null
    private var autoPaymentType = 0
    private lateinit var loanMonthDialog: LoanMonthDialog
    private var months = ArrayList<String>()
    private var monthsList = ArrayList<AllServiceLists>()
    private var monthsAdapter: AutoPaymentDaysAdapter? = null
    private var daysList = ArrayList<AllServiceLists>()
    private var daysAdapter: AutoPaymentDaysAdapter? = null
    private var customDates = java.util.ArrayList<String>()
    private val df = SimpleDateFormat("dd.MM.yyyy", Locale.US)


    private lateinit var customDatesAdapterAdapter: AutoPaymentCustomDateAdapter
    private var days = ArrayList<String>()
    private lateinit var choosePeriodType: ChoosePeriodType
    private var selectedTime: String? = null

    companion object {
        const val SAVE_AUTO_PAYMENT_MODEL = "save_payment"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            saveAutoPaymentModel = it.serializable(SAVE_AUTO_PAYMENT_MODEL) as SaveAutoPaymentModel?
            autoPayment = it.serializable("item") as AutoPayment?
        }
        setonClickView()
        initDays()
        initMonths()
        addAllMonths()
        initAutoPayment()
        initCustomDates()

    }

    private fun setonClickView() {
        binding.iconTextType.setOnClickListener(this)
        binding.editTextType.setOnClickListener(this)
        binding.editTextTime.setOnClickListener(this)
        binding.customDate.setOnClickListener(this)
        binding.editTextDayOfPayment.setOnClickListener(this)
        binding.iconTextDayOfPayment.setOnClickListener(this)
        binding.editTextName.addTextChangedListener(textWatcher)
        binding.editTextAmount.addTextChangedListener(textWatcher)
        binding.editTextTime.addTextChangedListener(textWatcher)
        binding.editTextDayOfPayment.addTextChangedListener(textWatcher)
        binding.editTextTimeDay.setOnClickListener(this)
        binding.editTextTimeDay.addTextChangedListener(textWatcher)
        binding.iconTextTime.setOnClickListener(this)
        binding.iconTextCustom.setOnClickListener(this)
        binding.editTextCustom.setOnClickListener(this)
        binding.iconCustomDate.setOnClickListener(this)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            nextWindow()
        }
    }


    private fun editAutoPayment() {
        saveAutoPaymentModel = SaveAutoPaymentModel()
        saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
        saveAutoPaymentModel?.name = binding.editTextName.text.toString()
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
                saveAutoPaymentModel?.hours = selectedTime
                saveAutoPaymentModel?.selected_days = java.util.ArrayList()
                saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
                saveAutoPaymentModel?.daysName = dayName
                saveAutoPaymentModel?.type = "D"
                saveAutoPaymentModel?.auto_payment_id = autoPayment?.id.toString()
                gotoWithSlide(
                    R.id.saveAutoPaymentFinalFragment,
                    bundleOf(
                        SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                        "operation" to "edit"
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
                val daysArrayList = java.util.ArrayList<Int>()
                daysArrayList.add(binding.editTextDayOfPayment.text.toString().toInt())
                saveAutoPaymentModel?.days = daysArrayList
                saveAutoPaymentModel?.months = monthsArrayList
                saveAutoPaymentModel?.hours = selectedTime
                saveAutoPaymentModel?.selected_days = java.util.ArrayList()
                saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
                saveAutoPaymentModel?.monthsName = monthName
                saveAutoPaymentModel?.daysName = binding.editTextDayOfPayment.text.toString()
                saveAutoPaymentModel?.type = "M"
                saveAutoPaymentModel?.auto_payment_id = autoPayment?.id.toString()
                gotoWithSlide(
                    R.id.saveAutoPaymentFinalFragment,
                    bundleOf(
                        SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                        "operation" to "edit"
                    )
                )
            }

            3 -> {
                saveAutoPaymentModel?.months = java.util.ArrayList()
                saveAutoPaymentModel?.days = java.util.ArrayList()
                saveAutoPaymentModel?.type = "S"
                saveAutoPaymentModel?.hours = ""
                saveAutoPaymentModel?.selected_days = customDates
                saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
                saveAutoPaymentModel?.daysName = binding.editTextDayOfPayment.text.toString()
                saveAutoPaymentModel?.auto_payment_id = autoPayment?.id.toString()
                gotoWithSlide(
                    R.id.saveAutoPaymentFinalFragment,
                    bundleOf(
                        SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                        "operation" to "edit"
                    )
                )
            }
        }

    }


    private fun nextWindow() {
        if (autoPayment != null) {
            editAutoPayment()
        } else {
            saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
            saveAutoPaymentModel?.name = binding.editTextName.text.toString()
            saveAutoPaymentModel?.hours = selectedTime
            saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
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
                    saveAutoPaymentModel?.months = java.util.ArrayList()
                    saveAutoPaymentModel?.days = java.util.ArrayList()
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

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            checkForButton()
        }
    }

    private fun checkForButton() {
        binding.btnContinue.isEnabled(
            if (autoPaymentType != 3) {
                binding.editTextName.text.toString().isNotEmpty() &&
                        binding.editTextTime.text.toString().isNotEmpty() &&
                        binding.editTextAmount.text.toString().isNotEmpty() && if (autoPayment != null) {
                    if (autoPayment!!.type == "M") monthsList.isNotEmpty() else daysList.isNotEmpty()
                } else if (autoPaymentType == 1) daysList.isNotEmpty() else monthsList.isNotEmpty()
            } else binding.editTextName.text.toString().isNotEmpty() && binding.editTextAmount.text.toString().isNotEmpty() && customDates.isNotEmpty()
        )
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
            daysAdapter?.setList(daysList)
        }
        binding.recyclerViewDay.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            daysAdapter = AutoPaymentDaysAdapter(this@CreateAutoPaymentFragment, daysList)
            adapter = daysAdapter
        }
    }

    override fun justOperation() {
        super.justOperation()
        checkForButton()
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
    }

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
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            monthsAdapter = AutoPaymentDaysAdapter(this@CreateAutoPaymentFragment, monthsList)
            adapter = monthsAdapter
        }
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

    private fun initAutoPayment() {
        if (autoPayment != null) {
            binding.editTextAmount.setText(Format.convertFromTiynDivide(autoPayment?.amount.toString()))
            binding.editTextName.setText(autoPayment?.name.toString())

            val time = autoPayment?.hour
            val timeList = ArrayList<AllServiceLists>()
            for (i in 1..24) {
                val allServiceLists = AllServiceLists()
                allServiceLists.name = ("$i:00")
                allServiceLists.code = i.toString()
                timeList.add(allServiceLists)
            }
            var timeName: String? = ""
            timeList.forEach {
                if (it.code!!.toInt() == time) {
                    timeName = it.name.toString()
                    selectedTime = it.code.toString()
                }
            }

            binding.editTextTime.setText(timeName)
            binding.editTextTimeDay.setText(timeName)
            autoPaymentType = if (autoPayment!!.type == "M") 2 else 1

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

                else -> {
                    binding.customDateLayout.visibility = View.VISIBLE
                    binding.daily.visibility = View.GONE
                    binding.monthly.visibility = View.GONE
                    binding.editTextType.setText(getString(R.string.custom))
                }
            }

            if (autoPayment!!.type == "M") {
                binding.editTextDayOfPayment.setText(autoPayment!!.days[0].toString())
                val currentMonths = autoPayment?.months
                val listOfMonths = ArrayList<AllServiceLists>()
                for (i in months.indices) {
                    val autoPaymentDates = AllServiceLists()
                    autoPaymentDates.name = months[i]
                    autoPaymentDates.code = (i + 1).toString()
                    autoPaymentDates.isSelected = currentMonths!!.contains(i + 1)
                    listOfMonths.add(autoPaymentDates)
                }
                if (currentMonths != null) {
                    binding.sswitchMonthly.isChecked = currentMonths.size == 12
                }
                monthsList = ArrayList()
                listOfMonths.forEach { dayWithName ->
                    monthsList.add(dayWithName)
                }
                monthsAdapter?.setList(monthsList)

            } else {
                val currentDays = autoPayment?.days
                val listOfDays = ArrayList<AllServiceLists>()
                for (i in days.indices) {
                    val autoPaymentDates = AllServiceLists()
                    autoPaymentDates.name = days[i]
                    autoPaymentDates.code = (i + 1).toString()
                    autoPaymentDates.isSelected = currentDays!!.contains(i + 1)
                    listOfDays.add(autoPaymentDates)
                }
                if (currentDays != null) {
                    binding.switchDaily.isChecked = currentDays.size == 7
                }
                daysList = ArrayList()
                listOfDays.forEach { dayWithName ->
                    daysList.add(dayWithName)
                }
                daysAdapter?.setList(daysList)
            }
            checkForButton()

        } else {
            val amount = saveAutoPaymentModel?.payment_details?.get("AMOUNT")
            if (amount != null) {
                binding.editTextAmount.setText(Format.convertFromTiynDivide(amount))
            }
            binding.editTextName.setText(saveAutoPaymentModel?.name)
            addAllDays()
            addAllMonths()
        }
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

    override fun onClick(p0: View?) {
        when (p0!!.id) {
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
                }
                choosePeriodType.show(childFragmentManager, "")
            }

            R.id.edit_text_day_of_payment, R.id.icon_text_day_of_payment -> {

                loanMonthDialog = LoanMonthDialog(this, "", "day", 0, requireContext())
                loanMonthDialog.show(childFragmentManager, "")

            }

            R.id.edit_text_time, R.id.icon_text_time -> {
                val cal = Calendar.getInstance()

                val timePicker = TimePickerDialog(
                    activity, R.style.my_dialog_theme,
                    { _, selectedHour, selectedMinute ->

                        selectedTime = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextTime.setText("$selectedHour:$selectedMinute")
                        } else binding.btnContinue.isEnabled(false)
                        if (binding.editTextDayOfPayment.text.toString().isNotEmpty() &&
                            binding.editTextAmount.text.toString().isNotEmpty() &&
                            binding.editTextName.toString().isNotEmpty() &&
                            binding.editTextTime.text.toString().isNotEmpty()
                        ) {
                            binding.btnContinue.isEnabled(true)
                        } else binding.btnContinue.isEnabled(false)
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

                        selectedTime = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextCustom.setText("$selectedHour:$selectedMinute")
                        }
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.HOUR_OF_DAY),
                    true
                )
                timePicker.setTitle(getString(R.string.choose_hour))
                timePicker.show()
                timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }

            R.id.edit_text_time_day, R.id.icon_text_time_day -> {
                val cal = Calendar.getInstance()
                val timePicker = TimePickerDialog(
                    activity, R.style.my_dialog_theme,
                    { _, selectedHour, selectedMinute ->
                        selectedTime = selectedHour.toString()
                        if (selectedHour.toString() != "0") {
                            binding.editTextTimeDay.setText("$selectedHour:$selectedMinute")
                        } else binding.btnContinue.isEnabled(false)

                        if (binding.editTextAmount.text.toString().isNotEmpty() &&
                            binding.editTextName.text.toString().isNotEmpty() &&
                            binding.editTextTimeDay.text.toString().isNotEmpty()
                        ) {
                            binding.btnContinue.isEnabled(true)
                        } else binding.btnContinue.isEnabled(false)

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

        DatePickerDialog(requireContext(), R.style.my_dialog_theme, { _, year, month, day ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, day)
            if (!customDates.contains(df.format(pickedDateTime.time))) {
                customDates.add(df.format(pickedDateTime.time))
                customDatesAdapterAdapter.setList(customDates)
                binding.btnContinue.isEnabled(customDates.isNotEmpty())
            }
        }, startYear, startMonth, startDay).show()
    }

    override fun invoke(count: Int, name: String) {
        when (name) {
            "day" -> {
                if (count == 31 || count == 30) {
                    binding.infoView.visibility = View.VISIBLE
                } else binding.infoView.visibility = View.GONE
                binding.editTextDayOfPayment.setText(count.toString())
                loanMonthDialog.dismiss()
            }
        }

    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, p2: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, 0)
        selectedTime = SimpleDateFormat("HH", Locale.US).format(cal.time)
        binding.editTextTime.setText(SimpleDateFormat("HH:00", Locale.US).format(cal.time))
        binding.editTextTimeDay.setText(SimpleDateFormat("HH:00", Locale.US).format(cal.time))

    }

}