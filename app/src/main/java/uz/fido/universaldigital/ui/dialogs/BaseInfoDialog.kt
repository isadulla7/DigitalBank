import android.os.Bundle
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.BaseDialogInfoBinding

class BaseInfoDialog(
    private var title: String,
    private var subtitle: String,
    private var buttonText: String? = null,
    private var onClickListener: (() -> Unit)? = null
) : BaseDialogFragment<BaseDialogInfoBinding>(BaseDialogInfoBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initDialogDetails()
        initSetOnClickListeners()
    }

    private fun initDialogDetails() {
        binding.apply {
            title.text = this@BaseInfoDialog.title
            subtitle.text = this@BaseInfoDialog.subtitle
            buttonText?.let {
                ok.text = it
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.ok.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.invoke()
            } else {
                dismiss()
            }
        }
    }

}