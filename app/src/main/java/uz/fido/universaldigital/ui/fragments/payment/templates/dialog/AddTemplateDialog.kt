package uz.fido.universaldigital.ui.fragments.payment.templates.dialog

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogAddTemplateBinding

class AddTemplateDialog(
    private var name: String,
    private var baseInterface: BaseInterface,
    private var title: String? = null,
    private var confirmButtonText: String? = null
) : BaseDialogFragment<DialogAddTemplateBinding>(DialogAddTemplateBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        if (title != null) {
            binding.header.text = title
        }
        if (confirmButtonText != null) {
            binding.add.text = confirmButtonText
        }
        binding.templateName.setText(name)
        binding.add.setOnClickListener {
            baseInterface.addTemplateName(binding.templateName.editableText.toString())
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

}