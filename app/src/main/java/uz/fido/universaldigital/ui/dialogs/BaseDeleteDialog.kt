package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.BaseDialogDeleteBinding

class BaseDeleteDialog(
    private var title: String,
    private var subtitle: String,
    private var setOnClickDelete: () -> Unit
) : BaseDialogFragment<BaseDialogDeleteBinding>(BaseDialogDeleteBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)

        initDetails()
        initSetOnClickListeners()
    }

    private fun initDetails() {
        binding.apply {
            title.text = this@BaseDeleteDialog.title
            subtitle.text = this@BaseDeleteDialog.subtitle
        }
    }

    private fun initSetOnClickListeners() {
        binding.dismiss.setOnClickListener {
            dismiss()
        }
        binding.ok.setOnClickListener {
            setOnClickDelete.invoke()
            dismiss()
        }
    }

}