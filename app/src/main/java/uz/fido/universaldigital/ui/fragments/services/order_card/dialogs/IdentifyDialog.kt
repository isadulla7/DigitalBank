package uz.fido.universaldigital.ui.fragments.services.order_card.dialogs

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogNoIndetifiedBinding

class IdentifyDialog(
    private var dialogTitle: String? = null,
    private var dialogDescription: String? = null,
    private var dialogPositiveButton: String? = null,
    private val identifyClickListener: () -> Unit
) : BaseDialogFragment<DialogNoIndetifiedBinding>(DialogNoIndetifiedBinding::inflate) {

    companion object {
        const val TAG = "IdentifyDialog"
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