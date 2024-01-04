package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import uz.fido.universaldigital.databinding.DialogDeleteWalletBinding

class CloseWalletDialog(private val clockButtonClickListener: () -> Unit) : DialogFragment() {

    private lateinit var binding: DialogDeleteWalletBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteWalletBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding.close.setOnClickListener {
            clockButtonClickListener.invoke()
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

}