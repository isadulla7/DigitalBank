import android.os.Bundle
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.LogOutDialogBinding

class DeleteAccountDialog(
    private var okClickListener: () -> Unit
) : BaseDialogFragment<LogOutDialogBinding>(LogOutDialogBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.title.text = getString(R.string.delete_account_q)
        binding.subtitle.text = getString(R.string.delete_account_description)
        binding.cancel.setOnClickListener { dismiss() }
        binding.logOut.setOnClickListener {
            okClickListener.invoke()
            dismiss()
        }
    }

}