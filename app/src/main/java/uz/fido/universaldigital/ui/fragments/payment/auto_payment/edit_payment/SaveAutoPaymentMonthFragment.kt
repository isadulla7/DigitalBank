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
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.SimpleAbstractFragment
import uz.fido.universaldigital.databinding.FragmentSavePaymentMonthBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentDaysAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.SaveAutoPaymentFinalFragment
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import java.util.Calendar

@AndroidEntryPoint
class SaveAutoPaymentMonthFragment : SimpleAbstractFragment<FragmentSavePaymentMonthBinding>(
    FragmentSavePaymentMonthBinding::inflate
), BaseInterface, View.OnClickListener, (Int, String) -> Unit {

    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    private var months = ArrayList<String>()
    private lateinit var loanMonthDialog: LoanMonthDialog

    private var monthsAdapter: AutoPaymentDaysAdapter? = null
    private var monthsList = ArrayList<AllServiceLists>()
    private var selectedTime: String? = null
    private var autoPayment: AutoPayment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            saveAutoPaymentModel = it.getSerializable(SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL) as SaveAutoPaymentModel?
            autoPayment = it.getSerializable("item") as AutoPayment?
        }

        if (monthsList.isEmpty())
            getMonthsList()

        onClickView()
        recylerView()
        getAutoPayment()

    }

    private fun recylerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            monthsAdapter = AutoPaymentDaysAdapter(this@SaveAutoPaymentMonthFragment, monthsList)
            adapter = monthsAdapter
        }
    }

    private fun getAutoPayment() {
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
            binding.editTextDayOfPayment.setText(autoPayment!!.days[0].toString())

            val currentMonths = autoPayment?.months
            val listOfMonths = ArrayList<AllServiceLists>()
            for (i in months.indices) {
                val autoPaymentDates = AllServiceLists()
                autoPaymentDates.name = months[i]
                autoPaymentDates.code = (i + 1).toString()
                listOfMonths.add(autoPaymentDates)
            }
            monthsList = ArrayList()
            listOfMonths.forEach { it.isSelected = false }
            monthsList = listOfMonths

            listOfMonths.forEach { dayWithName ->
                currentMonths!!.forEach {
                    if (it == dayWithName.code!!.toInt()) {
                        monthsList[it - 1].isSelected = true
                        //monthsList.add(dayWithName)
                    }
                }
            }
            monthsAdapter?.setList(monthsList)
            //  binding.btnContinue.isEnabled(true)
            checkForButton()
        } else {
            val amount = saveAutoPaymentModel?.payment_details?.get("AMOUNT")
            if (amount != null) {
                binding.editTextAmount.setText(Format.convertFromTiynDivide(amount))
            }
            binding.editTextName.setText(saveAutoPaymentModel?.name)
            addAllMonths()
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

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.editTextName.addTextChangedListener(textWatcher)
        binding.editTextTime.addTextChangedListener(textWatcher)
        binding.editTextDayOfPayment.addTextChangedListener(textWatcher)
        binding.editTextAmount.addTextChangedListener(textWatcher)
        binding.editTextDayOfPayment.setOnClickListener(this)
        binding.iconTextDayOfPayment.setOnClickListener(this)
        binding.editTextTime.setOnClickListener(this)
        binding.iconTextTime.setOnClickListener(this)
        binding.btnContinue.setOnClickListener {
            editPayment()
        }
    }

    private fun editPayment() {
        if (autoPayment != null) {
            editAutoPayment()
        } else {
            saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
            saveAutoPaymentModel?.name = binding.editTextName.text.toString()
            val monthsArrayList = ArrayList<Int>()
            val monthNameBuilder = StringBuilder()
            monthsList.forEach {
                if (it.isSelected) {
                    monthsArrayList.add(it.code!!.toInt())
                    monthNameBuilder.append(it.name?.replace("\"", "")).append(", ")
                }
            }
            saveAutoPaymentModel?.months = monthsArrayList
            val daysArrayList = ArrayList<Int>()
            daysArrayList.add(binding.editTextDayOfPayment.text.toString().toInt())
            saveAutoPaymentModel?.days = daysArrayList
            saveAutoPaymentModel?.hours = selectedTime
            saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
            saveAutoPaymentModel?.daysName = binding.editTextDayOfPayment.text.toString()
            saveAutoPaymentModel?.monthsName = monthNameBuilder.toString()
            saveAutoPaymentModel?.type = "M"
            gotoWithSlide(
                R.id.saveAutoPaymentFinalFragment,
                bundleOf(SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel)
            )
        }
    }

    private fun editAutoPayment() {
        saveAutoPaymentModel = SaveAutoPaymentModel()
        saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
        saveAutoPaymentModel?.name = binding.editTextName.text.toString()
        val monthsArrayList = ArrayList<Int>()
        val monthNameBuilder = StringBuilder()
        monthsList.forEach {
            if (it.isSelected) {
                monthsArrayList.add(it.code!!.toInt())
                monthNameBuilder.append(" ").append(it.name?.replace("'", " ")).append(" ")
                monthNameBuilder.deleteCharAt(monthNameBuilder.length - 1)
            }
        }
        val daysArrayList = ArrayList<Int>()
        daysArrayList.add(binding.editTextDayOfPayment.text.toString().toInt())
        saveAutoPaymentModel?.days = daysArrayList
        saveAutoPaymentModel?.months = monthsArrayList
        saveAutoPaymentModel?.selected_days = arrayListOf()
        saveAutoPaymentModel?.hours = selectedTime
        saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
        saveAutoPaymentModel?.monthsName = monthNameBuilder.toString()
        saveAutoPaymentModel?.daysName = binding.editTextDayOfPayment.text.toString()
        saveAutoPaymentModel?.type = "M"
        saveAutoPaymentModel?.auto_payment_id = autoPayment?.id.toString()
        if (monthsArrayList.isNotEmpty())
            gotoWithSlide(
                R.id.saveAutoPaymentFinalFragment,
                bundleOf(
                    SaveAutoPaymentFinalFragment.SAVE_AUTO_PAYMENT_MODEL to saveAutoPaymentModel,
                    "operation" to "edit"
                )
            )
    }

    private fun getMonthsList() {
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
        if (binding.editTextTime.text.toString().isEmpty()) {
            return false
        }
        if (binding.editTextAmount.text.toString().isEmpty()) {
            return false
        }
        if (binding.editTextDayOfPayment.text.toString().isEmpty()) {
            return false
        }
        if (monthsList.size == 0) {
            return false
        }

        return true
    }

    override fun justOperation() {
        super.justOperation()
        var selectedDays = 0
        monthsList.forEach {
            if (it.isSelected) {
                ++selectedDays
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
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
                        binding.editTextTime.setText("$selectedHour:$selectedMinute")
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

    override fun invoke(count: Int, name: String) {
        if (count == 31 || count == 30) {
            binding.infoView.visibility = View.VISIBLE
        } else binding.infoView.visibility = View.GONE
        binding.editTextDayOfPayment.setText(count.toString())
        loanMonthDialog.dismiss()
    }
}