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
import uz.fido.universaldigital.databinding.DialogMonitoringChooseBinding
import uz.fido.universaldigital.databinding.DialogMonitoringDateBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class MonitoringChooseDialog(private val onClick:(String)->Unit): BottomSheetDialogFragment(){

  private lateinit var binding:DialogMonitoringChooseBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogMonitoringChooseBinding.inflate(inflater,container,false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.enrollments.setOnClickListener {
           onClick.invoke(getString(R.string.enrollments))
       }
        binding.writeOffs.setOnClickListener {
            onClick.invoke(getString(R.string.write_offs))
        }
    }


}