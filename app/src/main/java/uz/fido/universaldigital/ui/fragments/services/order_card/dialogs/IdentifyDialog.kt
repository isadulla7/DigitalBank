package uz.fido.universaldigital.ui.fragments.services.order_card.dialogs

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogNoIndetifiedBinding

class IdentifyDialog(
    var dialogTitle: String? = null,
    var dialogDescription: String? = null,
    var dialogPositiveButton: String? = null,
    val identifyClickListener: () -> Unit
) : BaseDialogFragment<DialogNoIndetifiedBinding>(DialogNoIndetifiedBinding::inflate) {

    companion object {
        val TAG = "IdentifyDialog"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        dialogTitle?.let {
            binding.dialogTitle.text = it
        }
        dialogDescription?.let {
            binding.dialogDescription.text = it
        }
        dialogPositiveButton?.let {
            binding.doneButton.text = it
        }
        binding.doneButton.setOnClickListener {
            dismiss()
            identifyClickListener.invoke()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

}