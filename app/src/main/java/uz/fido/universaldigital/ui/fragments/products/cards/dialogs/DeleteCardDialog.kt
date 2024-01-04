package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import uz.fido.universaldigital.databinding.DialogDeleteCardBinding

class DeleteCardDialog(private val onDeleteClickListener: () -> Unit) : DialogFragment() {

    private lateinit var binding: DialogDeleteCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteCardBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding.delete.setOnClickListener {
            onDeleteClickListener.invoke()
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

}