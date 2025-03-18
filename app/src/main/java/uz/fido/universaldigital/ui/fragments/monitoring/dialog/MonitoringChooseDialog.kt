package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogMonitoringChooseBinding

class MonitoringChooseDialog(private val onClick: (String) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogMonitoringChooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMonitoringChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enrollments.setOnClickListener {
            dismiss()
            onClick.invoke(getString(R.string.enrollments))
        }
        binding.writeOffs.setOnClickListener {
            dismiss()
            onClick.invoke(getString(R.string.write_offs))
        }
    }

}