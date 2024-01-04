package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_deposit_constructor.year_layout
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogMonitoringDateBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class MonitoringDateDialog(private val onClick:(String,String)->Unit): BottomSheetDialogFragment(),
    View.OnClickListener {

  private lateinit var binding:DialogMonitoringDateBinding
  private var startDate=""
  private var endDate=""
    val c = Calendar.getInstance()
    var  mYear = c[Calendar.YEAR]
    var  mMonth = c[Calendar.MONTH]
    var  mDay = c[Calendar.DAY_OF_MONTH]
    var format=SimpleDateFormat("dd.MM.yyyy")
    var startOnclickCurrent=false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogMonitoringDateBinding.inflate(inflater,container,false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCLickView()

        binding.btnContinue.setOnClickListener {
            onClick.invoke(startDate,endDate)
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
        var  year = startCalendar[Calendar.YEAR]
        var  month = startCalendar[Calendar.MONTH]
        var  day = startCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(),
            uz.fido.utils.R.style.DatePickerDialog , {
                _, year, monthOfYear, dayOfMonth ->
                binding.edDateUpTo.setText(getString(R.string.before))
                endDate=""
                startOnclickCurrent=true
                 c.set(Calendar.MONTH,monthOfYear)
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                c.set(Calendar.YEAR,year)

                startDate=format.format(c.time)
            binding.edDate.setText(startDate)
            btnCurrent()},
            year, month, day
        )
        datePickerDialog.datePicker.maxDate=startCalendar.timeInMillis
        datePickerDialog.show()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.ed_date,R.id.start->{
                itemStartDate()
            }
            R.id.end_icon,R.id.ed_date_up_to->{
                itemEndDate()
            }
        }
    }

    private fun itemEndDate() {
        val newCalendar = Calendar.getInstance()
        var  year = newCalendar[Calendar.YEAR]
        var  month = newCalendar[Calendar.MONTH]
        var  day = newCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(),
            uz.fido.utils.R.style.DatePickerDialog , {
                    _, year, monthOfYear, dayOfMonth ->
                val calendar=Calendar.getInstance()
                calendar.set(Calendar.MONTH,monthOfYear)
                calendar.set(Calendar.YEAR,year)
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                 endDate=format.format(calendar.time)
                binding.edDateUpTo.setText(endDate)
                btnCurrent()},
            year, month, day
        )
        if (startOnclickCurrent)
        datePickerDialog.datePicker.minDate=c.timeInMillis
        datePickerDialog.datePicker.maxDate=newCalendar.timeInMillis
        datePickerDialog.show()
    }

    fun btnCurrent(){
        if (startDate!="" && endDate!=""){
            binding.btnContinue.isEnabled(true)
        }else binding.btnContinue.isEnabled(false)
    }
}