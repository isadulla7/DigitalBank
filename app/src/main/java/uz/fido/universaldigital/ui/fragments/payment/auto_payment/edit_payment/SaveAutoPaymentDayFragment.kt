package uz.fido.universaldigital.ui.fragments.payment.auto_payment.edit_payment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSaveAutoPaymentDayBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentDaysAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.SaveAutoPaymentFinalFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SaveAutoPaymentDayFragment : BaseFragment<FragmentSaveAutoPaymentDayBinding, EditAutoPaymentViewModel>
    (FragmentSaveAutoPaymentDayBinding::inflate, EditAutoPaymentViewModel::class.java),
    View.OnClickListener {

    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    private var autoPayment: AutoPayment? = null
    private val days = arrayListOf<String>()

    private var daysList = ArrayList<AllServiceLists>()

    private var daysAdapter: AutoPaymentDaysAdapter? = null
    private var selectedTime: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            saveAutoPaymentModel = it.serializable(SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL) as SaveAutoPaymentModel?
            autoPayment = it.serializable("item") as AutoPayment?
        }
        if (days.isEmpty())
            addDay()

        onClickView()
        recyclerView()

        autoPayments()
    }

    private fun addDay() {
        days.add(getString(R.string.monday))
        days.add(getString(R.string.tuesday))
        days.add(getString(R.string.wednesday))
        days.add(getString(R.string.thursday))
        days.add(getString(R.string.friday))
        days.add(getString(R.string.saturday))
        days.add(getString(R.string.sunday))
    }

    private fun autoPayments() {
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
            binding.editTextTimeDay.setText(timeName)

            val currentDays = autoPayment?.days
            val listOfDays = ArrayList<AllServiceLists>()
            for (i in days.indices) {
                val autoPaymentDates = AllServiceLists()
                autoPaymentDates.name = days[i]
                autoPaymentDates.code = (i + 1).toString()
                listOfDays.add(autoPaymentDates)
            }
            daysList = ArrayList()
            listOfDays.forEach { it.isSelected = false }
            daysList = listOfDays
            listOfDays.forEach { dayWithName ->
                currentDays!!.forEach {
                    if (it == dayWithName.code!!.toInt()) {
                        daysList[it - 1].isSelected = true
                        //daysList.add(dayWithName)
                    }
                }
            }
            daysAdapter?.setList(daysList)

        } else {
            val amount = saveAutoPaymentModel?.payment_details?.get("AMOUNT")
            if (amount != null) {
                binding.editTextAmount.setText(Format.convertFromTiynDivide(amount))
            }
            addAllDays()
            binding.editTextName.setText(saveAutoPaymentModel?.name)
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

    private fun recyclerView() {
        binding.recyclerViewDay.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            daysAdapter = AutoPaymentDaysAdapter(this@SaveAutoPaymentDayFragment, daysList)
            adapter = daysAdapter
        }
    }

    private fun onClickView() {
        binding.editTextName.addTextChangedListener(textWatcher)
        binding.editTextAmount.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                isEditing = true

                val digits = s.toString().replace(Regex("[^0-9]"), "")

                if (digits.isNotEmpty()) {
                    val formatted = digits.reversed().chunked(3).joinToString(" ").reversed()
                    binding.editTextAmount.setText(formatted)
                    binding.editTextAmount.setSelection(formatted.length)

                    val amount = digits.toLongOrNull() ?: 0L
                    if (amount < 500) {
                        binding.editTextAmount.error = getString(R.string.min_summ)
                    } else {
                        binding.editTextAmount.error = null
                    }

                } else {
                    binding.editTextAmount.setText("")
                    binding.editTextAmount.error = getString(R.string.min_summ)
                }

                isEditing = false
                binding.btnContinue.isEnabled(checkForButton())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editTextTimeDay.addTextChangedListener(textWatcher)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.editTextTimeDay.setOnClickListener(this)
        binding.iconTextTimeDay.setOnClickListener(this)
        binding.btnContinue.setOnClickListener {
            saveAutoPayment()
        }

    }

    private fun saveAutoPayment() {
        if (autoPayment != null) {
            editAutoPayment()
        } else {
            saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
            saveAutoPaymentModel?.name = binding.editTextName.text.toString()
            val daysArrayList = ArrayList<Int>()
            val dayNameBuilder = StringBuilder()
            daysList.forEach {
                if (it.isSelected) {
                    daysArrayList.add(it.code!!.toInt())
                    dayNameBuilder.append(" ").append(it.name?.replace("'", " ")).append("',")
                    dayNameBuilder.deleteCharAt(dayNameBuilder.length - 1)
                }
            }
            saveAutoPaymentModel?.months = ArrayList()
            saveAutoPaymentModel?.days = daysArrayList
            saveAutoPaymentModel?.hours = selectedTime
            saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
            saveAutoPaymentModel?.daysName = dayNameBuilder.toString()
            saveAutoPaymentModel?.type = "D"
            if (daysArrayList.isNotEmpty())
                gotoWithSlide(
                    R.id.saveAutoPaymentFinalFragment,
                    bundleOf(
                        SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                        "operation" to "create"
                    )
                )
        }
    }

    private fun editAutoPayment() {
        saveAutoPaymentModel = SaveAutoPaymentModel()
        saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
        saveAutoPaymentModel?.name = binding.editTextName.text.toString()
        val daysArrayList = ArrayList<Int>()
        val dayNameBuilder = StringBuilder()
        daysList.forEach {
            if (it.isSelected) {
                daysArrayList.add(it.code!!.toInt())
                dayNameBuilder.append(" ").append(it.name?.replace("'", " ")).append(" ,")
                dayNameBuilder.deleteCharAt(dayNameBuilder.length - 1)
            }
        }
        saveAutoPaymentModel?.months = ArrayList()
        saveAutoPaymentModel?.days = daysArrayList
        saveAutoPaymentModel?.selected_days = arrayListOf()
        saveAutoPaymentModel?.hours = selectedTime
        saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
        saveAutoPaymentModel?.daysName = dayNameBuilder.toString()
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


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            binding.btnContinue.isEnabled(checkForButton())
        }
    }

    private fun checkForButton(): Boolean {
        if (binding.editTextName.text.toString().isEmpty()) {
            return false
        }
        if (binding.editTextTimeDay.text.toString().isEmpty()) {
            return false
        }
        if (binding.editTextAmount.text.toString().isEmpty()
            || binding.editTextAmount.text.toString().replace(" ", "").toDouble() < 500
        ) {
            return false
        }
        val newLists = daysList.filter { it.isSelected }
        if (newLists.isEmpty()) {
            return false
        }
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
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
                        binding.editTextTimeDay.setText(formattedTime)
                        binding.btnContinue.isEnabled(checkForButton())

                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.HOUR_OF_DAY),
                    true
                )
                timePicker.setTitle(getString(R.string.choose_hour))
                timePicker.show()
                timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)


            }
        }
    }

    override fun justOperation() {
        super.justOperation()
        var selectedDays = 0
        daysList.forEach {
            if (it.isSelected) {
                ++selectedDays
            }
        }
        binding.btnContinue.isEnabled(checkForButton())
        binding.switchDaily.isChecked = selectedDays == 7
    }


}