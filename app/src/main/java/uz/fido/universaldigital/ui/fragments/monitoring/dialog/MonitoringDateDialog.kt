package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogMonitoringDateBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MonitoringDateDialog(private val onClick: (String, String) -> Unit) : BottomSheetDialogFragment(),
    View.OnClickListener {

    private lateinit var binding: DialogMonitoringDateBinding
    private var startDate = ""
    private var endDate = ""
    val c: Calendar = Calendar.getInstance()
    var format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var startOnclickCurrent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMonitoringDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCLickView()

        binding.btnContinue.setOnClickListener {
            onClick.invoke(startDate, endDate)
        }
    }

    private fun onCLickView() {
        binding.edDate.setOnClickListener(this)
        binding.start.setOnClickListener(this)
        binding.edDateUpTo.setOnClickListener(this)
        binding.endIcon.setOnClickListener(this)

    }

    private fun itemStartDate() {
        val startCalendar = Calendar.getInstance()
        val year = startCalendar[Calendar.YEAR]
        val month = startCalendar[Calendar.MONTH]
        val day = startCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            uz.fido.utils.R.style.DatePickerDialog, { _, year, monthOfYear, dayOfMonth ->
                binding.edDateUpTo.setText(getString(R.string.before))
                endDate = ""
                startOnclickCurrent = true
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                c.set(Calendar.YEAR, year)

                startDate = format.format(c.time)
                binding.edDate.setText(startDate)
                btnCurrent()
            },
            year, month, day
        )
        datePickerDialog.datePicker.maxDate = startCalendar.timeInMillis
        datePickerDialog.show()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ed_date, R.id.start -> {
                itemStartDate()
            }

            R.id.end_icon, R.id.ed_date_up_to -> {
                itemEndDate()
            }
        }
    }

    private fun itemEndDate() {
        val newCalendar = Calendar.getInstance()
        val year = newCalendar[Calendar.YEAR]
        val month = newCalendar[Calendar.MONTH]
        val day = newCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            uz.fido.utils.R.style.DatePickerDialog, { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                endDate = format.format(calendar.time)
                binding.edDateUpTo.setText(endDate)
                btnCurrent()
            },
            year, month, day
        )
        if (startOnclickCurrent)
            datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.datePicker.maxDate = newCalendar.timeInMillis
        datePickerDialog.show()
    }

    private fun btnCurrent() {
        if (startDate != "" && endDate != "") {
            binding.btnContinue.isEnabled(true)
        } else binding.btnContinue.isEnabled(false)
    }
}