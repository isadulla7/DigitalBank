import android.os.Bundle
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.DialogTerminateSessionBinding

class TerminateSessionDialog(
    private var type: String,
    private var okClickListener: () -> Unit
) : BaseDialogFragment<DialogTerminateSessionBinding>(DialogTerminateSessionBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        if (type == "all") {
            binding.title.text = getString(R.string.are_you_sure_terminate_all_sessions)
            binding.ok.text = getString(R.string.terminate_all)
        }
        binding.cancel.setOnClickListener { dismiss() }
        binding.ok.setOnClickListener {
            okClickListener.invoke()
            dismiss()
        }
    }

}