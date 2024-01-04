package uz.fido.universaldigital.ui.fragments.payment.my_home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.databinding.DialogMyHouseServiceOperationBinding

class MyHouseServiceOperationDialog(
    private var template: Template,
    private val onCLick: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    private lateinit var binding: DialogMyHouseServiceOperationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMyHouseServiceOperationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textName.text = template.name
        if (template.service_state != "A") {
            binding.payment.visibility = View.GONE
        }

        binding.payment.setOnClickListener {
            onCLick.invoke("payment")
        }
        binding.delete.setOnClickListener {
            onCLick.invoke("delete")
        }
        binding.monitoring.setOnClickListener {
            onCLick.invoke("monitoring")
        }
    }


}