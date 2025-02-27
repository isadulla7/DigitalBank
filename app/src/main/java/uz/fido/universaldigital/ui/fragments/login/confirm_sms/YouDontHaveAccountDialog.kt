package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogYouDontHaveAccountBinding

class YouDontHaveAccountDialog(
    private val cancelOperation: () -> Unit,
    private val continueSignUp: () -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogYouDontHaveAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogYouDontHaveAccountBinding.inflate(inflater, container, false)
        binding.cancel.setOnClickListener {
            dismiss()
            cancelOperation.invoke()
        }
        binding.doneButton.setOnClickListener {
            dismiss()
            continueSignUp.invoke()
        }
        return binding.root
    }
}