package uz.fido.universaldigital.ui.fragments.login.confirm_sms.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogYouHaveAnAccountBinding

@AndroidEntryPoint
class YouHaveAccountDialog(
    private val openMyId: () -> Unit,
    private val continueSignUp: () -> Unit,
    private val isRequireContinueSignUp: Boolean? = true
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogYouHaveAnAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogYouHaveAnAccountBinding.inflate(inflater, container, false)
        if (isRequireContinueSignUp == false) {
            binding.title.text = getString(R.string.reset_password)
            binding.continueSignUp.visibility = View.GONE
        }
        binding.openMyid.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            dismiss()
            openMyId.invoke()
            dismiss()
        }
        binding.continueSignUp.setOnClickListener {
            dismiss()
            continueSignUp.invoke()
        }
        return binding.root
    }

}