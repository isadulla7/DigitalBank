package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogUnableGetProfileBinding

class UnableGetProfileDialog(
    private var okClickListener: () -> Unit
) : BaseDialogFragment<DialogUnableGetProfileBinding>(DialogUnableGetProfileBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.delete.setOnClickListener {
            dismiss()
            okClickListener.invoke()
        }
    }

}