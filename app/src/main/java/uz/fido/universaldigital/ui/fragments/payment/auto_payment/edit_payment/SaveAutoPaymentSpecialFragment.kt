package uz.fido.universaldigital.ui.fragments.payment.auto_payment.edit_payment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.SimpleAbstractFragment
import uz.fido.universaldigital.databinding.FragmentSavePaymentSpecialBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentCustomDateAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment.SaveAutoPaymentFinalFragment
import uz.fido.universaldigital.ui.utils.extensions.hideSoftKeyboard
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SaveAutoPaymentSpecialFragment : SimpleAbstractFragment<FragmentSavePaymentSpecialBinding>(
    FragmentSavePaymentSpecialBinding::inflate
), View.OnClickListener {
    private var autoPayment: AutoPayment? = null
    private lateinit var customDatesAdapterAdapter: AutoPaymentCustomDateAdapter
    private var customDates = java.util.ArrayList<String>()
    private val df = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    private var selectedTime: String? = null
    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            autoPayment = it.serializable<AutoPayment>("item")
        }
        getAutoPayment()
        setAdapter()
        onClickView()
        textWatchers()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.editTextDayOfPayment.setOnClickListener(this)
        binding.iconTextDayOfPayment.setOnClickListener(this)
        binding.editTextTime.setOnClickListener(this)
        binding.iconTextTime.setOnClickListener(this)
        binding.btnContinue.setOnClickListener {
            hideSoftKeyboard()
            editAutoPayment()
        }
    }

    private fun setAdapter() {
        customDatesAdapterAdapter = AutoPaymentCustomDateAdapter(customDates) {
            customDates.remove(it)
            binding.btnContinue.isEnabled(customDates.isNotEmpty())
            customDatesAdapterAdapter.notifyDataSetChanged()
        }
        binding.recyclerView.apply {
            adapter = customDatesAdapterAdapter
        }
    }

    private fun getAutoPayment() {
        if (autoPayment != null) {
            customDates = autoPayment!!.selected_days!!
            binding.editTextAmount.setText(Format.convertFromTiynDivide(autoPayment?.amount.toString()))
            binding.editTextName.setText(autoPayment?.name.toString())
            val time = autoPayment?.hour
            binding.editTextTime.setText("$time:00")
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.edit_text_day_of_payment, R.id.icon_text_day_of_payment -> {
                pickDateTime()
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

    private fun textWatchers() {
        Observable.combineLatest(
            binding.editTextName.textChanges(),
            binding.editTextAmount.textChanges(),
            BiFunction(this::isValit)
        ).doOnNext {
            binding.btnContinue.isEnabled(it)
        }
            .subscribe()
    }

    private fun isValit(
        name: CharSequence,
        amount: CharSequence,
    ): Boolean {
        if (name.isEmpty()) return false
        if (amount.isEmpty()) return false
        if (binding.editTextTime.text.isNullOrEmpty()) return false
        if (customDates.isEmpty()) return false
        return true
    }

    private fun editAutoPayment() {
        saveAutoPaymentModel = SaveAutoPaymentModel()
        saveAutoPaymentModel?.phone_number = getClientPhoneNumber()
        saveAutoPaymentModel?.name = binding.editTextName.text.toString()
        val monthsArrayList = ArrayList<Int>()
        val monthNameBuilder = StringBuilder()

        val daysArrayList = ArrayList<Int>()
        // daysArrayList.add(binding.editTextDayOfPayment.text.toString().toInt())
        saveAutoPaymentModel?.days = daysArrayList
        saveAutoPaymentModel?.months = monthsArrayList
        saveAutoPaymentModel?.selected_days = customDates
        saveAutoPaymentModel?.hours = if (selectedTime == null) autoPayment?.hour.toString() else selectedTime
        saveAutoPaymentModel?.amount = Format.formatAmountToTiyn(binding.editTextAmount.text.toString().replace(" ", ""))
        saveAutoPaymentModel?.monthsName = monthNameBuilder.toString()
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
}