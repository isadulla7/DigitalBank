package uz.fido.universaldigital.ui.fragments.products.cards.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.AddCardDialogBinding

class AddCardDialog(
    private val selectedOption: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: AddCardDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AddCardDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addCardBtn.setOnClickListener {
            selectedOption.invoke(it.tag.toString())
            dismiss()
        }
        binding.orderCardBtn.setOnClickListener {
            selectedOption.invoke(it.tag.toString())
            dismiss()
        }
    }
}