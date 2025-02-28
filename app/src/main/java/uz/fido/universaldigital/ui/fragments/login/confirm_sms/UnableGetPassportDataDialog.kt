package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogUnableGetPassportBinding

class UnableGetPassportDataDialog(
    private var okClickListener: () -> Unit
) : BaseDialogFragment<DialogUnableGetPassportBinding>(DialogUnableGetPassportBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.delete.setOnClickListener {
            dismiss()
            okClickListener.invoke()
        }
    }

}