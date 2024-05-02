package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogOpenSettingsBinding

class OpenSettingsDialog(
    private var description: String,
    private var okClickListener: () -> Unit
) : BaseDialogFragment<DialogOpenSettingsBinding>(DialogOpenSettingsBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.subtitle.text = description
        binding.cancel.setOnClickListener { dismiss() }
        binding.logOut.setOnClickListener {
            okClickListener.invoke()
            dismiss()
        }
    }

}