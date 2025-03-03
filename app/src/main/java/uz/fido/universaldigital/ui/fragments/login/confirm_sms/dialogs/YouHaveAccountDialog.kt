package uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogYouHaveAnAccountBinding

class YouHaveAccountDialog(
    private val openMyId: () -> Unit,
    private val continueSignUp: () -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogYouHaveAnAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogYouHaveAnAccountBinding.inflate(inflater, container, false)
        binding.openMyid.setOnClickListener {
            dismiss()
            openMyId.invoke()
        }
        binding.continueSignUp.setOnClickListener {
            dismiss()
            continueSignUp.invoke()
        }
        return binding.root
    }
}