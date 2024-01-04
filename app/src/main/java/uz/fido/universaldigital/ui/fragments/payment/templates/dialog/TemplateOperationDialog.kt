package uz.fido.universaldigital.ui.fragments.payment.templates.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogTemplateOperationBinding
import uz.fido.utils.const.Const

class TemplateOperationDialog(
    private var baseInterface: BaseInterface
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogTemplateOperationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTemplateOperationBinding.inflate(inflater, container, false)
        binding.editTemplate.setOnClickListener {
            dismiss()
            baseInterface.selectTemplateType(Const.TEMPLATE_TYPE_1)
        }
        binding.deleteTemplate.setOnClickListener {
            dismiss()
            baseInterface.selectTemplateType(Const.TEMPLATE_TYPE_2)
        }
        return binding.root
    }
}