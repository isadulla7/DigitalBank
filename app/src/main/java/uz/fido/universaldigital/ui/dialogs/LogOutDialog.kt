package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.LogOutDialogBinding

class LogOutDialog(
    private var okClickListener: () -> Unit
) : BaseDialogFragment<LogOutDialogBinding>(LogOutDialogBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.cancel.setOnClickListener { dismissAllowingStateLoss() }
        binding.logOut.setOnClickListener {
            okClickListener.invoke()
            dismissAllowingStateLoss()
        }
    }

}